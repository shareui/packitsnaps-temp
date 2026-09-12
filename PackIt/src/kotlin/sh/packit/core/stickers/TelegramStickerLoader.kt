package sh.packit.core.stickers

import android.graphics.drawable.GradientDrawable
import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.DocumentObject
import org.telegram.messenger.ImageLocation
import org.telegram.messenger.MediaDataController
import org.telegram.messenger.NotificationCenter
import org.telegram.messenger.UserConfig
import org.telegram.messenger.Utilities
import org.telegram.tgnet.TLRPC
import org.telegram.ui.ActionBar.Theme
import org.telegram.ui.Components.BackupImageView

object TelegramStickerLoader {
    private const val MAX_PENDING_REQUESTS = 128
    private val inflightCallbacks = mutableListOf<Utilities.Callback<TLRPC.TL_messages_stickerSet>>()
    private val pendingViews = mutableListOf<PendingStickerView>()
    private var isObserverRegistered: Boolean = false

    private data class PendingStickerView(
        val view: BackupImageView,
        val pack: String,
        val index: Int,
        val sizeDp: Int
    )

    private val selectedAccount: Int
        get() = UserConfig.selectedAccount

    private fun getMediaController(): MediaDataController =
        MediaDataController.getInstance(selectedAccount)

    private fun parseKey(key: String): Pair<String, Int>? {
        if (!key.contains("/")) return null
        val parts = key.split("/", limit = 2)
        val index = parts[1].toIntOrNull() ?: 0
        return Pair(parts[0], index)
    }

    private fun buildTag(pack: String, index: Int): String =
        "packit_sticker_${pack}_${index}"

    private fun isTagMatching(view: BackupImageView, expectedTag: String): Boolean {
        val tag = view.tag ?: return true
        return tag.toString() == expectedTag
    }

    private fun setPlaceholder(view: BackupImageView, sizeDp: Int) {
        val color = Theme.getColor(Theme.key_emptyListPlaceholder)
        val red = (color shr 16) and 0xFF
        val green = (color shr 8) and 0xFF
        val blue = color and 0xFF
        val alphaColor = (0x33 shl 24) or (red shl 16) or (green shl 8) or blue

        val cornerRadius = AndroidUtilities.dp(maxOf(4f, (sizeDp / 6).toFloat()))
        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            this.cornerRadius = cornerRadius.toFloat()
            setColor(alphaColor)
        }
        view.background = drawable
    }

    private fun clearPlaceholder(view: BackupImageView) {
        view.background = null
    }

    private fun bindDocument(
        view: BackupImageView,
        stickerSet: TLRPC.TL_messages_stickerSet?,
        index: Int,
        sizeDp: Int
    ): Boolean {
        val set = stickerSet ?: return false
        val documents = set.documents
        if (index < 0 || index >= documents.size) return false
        val doc = documents[index] ?: return false

        val svgThumb = try {
            DocumentObject.getSvgThumb(doc, Theme.key_emptyListPlaceholder, 0.2f)?.also {
                it.overrideWidthAndHeight(512, 512)
            }
        } catch (_: Throwable) {
            null
        }

        view.setImage(
            ImageLocation.getForDocument(doc),
            "${sizeDp}_${sizeDp}",
            "tgs",
            svgThumb,
            set
        )
        clearPlaceholder(view)
        return true
    }

    private fun resolveFromCache(pack: String): TLRPC.TL_messages_stickerSet? {
        val mdc = getMediaController()
        return mdc.getStickerSetByName(pack) ?: mdc.getStickerSetByEmojiOrName(pack)
    }

    private fun ensureObserver() {
        if (isObserverRegistered) return
        val center = NotificationCenter.getInstance(selectedAccount)
        center.addObserver(
            { id, _, args ->
                if (id == NotificationCenter.diceStickersDidLoad) {
                    val packName = args.firstOrNull()?.toString()
                    AndroidUtilities.runOnUIThread { flushPending(packName) }
                }
            },
            NotificationCenter.diceStickersDidLoad
        )
        isObserverRegistered = true
    }

    private fun flushPending(packName: String?) {
        val remaining = mutableListOf<PendingStickerView>()
        for (item in pendingViews) {
            if (packName != null && item.pack != packName) {
                remaining.add(item)
                continue
            }
            val set = resolveFromCache(item.pack)
            val success = bindDocument(item.view, set, item.index, item.sizeDp)
            if (!success) remaining.add(item)
        }
        pendingViews.clear()
        pendingViews.addAll(remaining)
    }

    @JvmStatic
    fun loadSticker(view: BackupImageView, stickerKey: String, sizeDp: Int = 130) {
        val parsed = parseKey(stickerKey) ?: return
        val pack = parsed.first
        val index = parsed.second
        val expectedTag = buildTag(pack, index)

        view.tag = expectedTag

        val cachedSet = resolveFromCache(pack)
        if (cachedSet != null && bindDocument(view, cachedSet, index, sizeDp)) {
            return
        }

        setPlaceholder(view, sizeDp)

        val inputSet = TLRPC.TL_inputStickerSetShortName().apply {
            short_name = pack
        }

        val callback = Utilities.Callback<TLRPC.TL_messages_stickerSet> { loadedSet ->
            AndroidUtilities.runOnUIThread {
                if (isTagMatching(view, expectedTag)) {
                    bindDocument(view, loadedSet, index, sizeDp)
                }
            }
        }

        inflightCallbacks.add(callback)
        if (inflightCallbacks.size > MAX_PENDING_REQUESTS) {
            inflightCallbacks.removeAt(0)
        }

        try {
            getMediaController().getStickerSet(inputSet, 0, false, callback)
        } catch (_: Throwable) {
            getMediaController().loadStickersByEmojiOrName(pack, false, true)
            pendingViews.add(PendingStickerView(view, pack, index, sizeDp))
            if (pendingViews.size > MAX_PENDING_REQUESTS) {
                pendingViews.removeAt(0)
            }
            ensureObserver()
        }
    }
}
