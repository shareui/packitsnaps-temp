package sh.packit.compose.components

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.telegram.messenger.AndroidUtilities
import org.telegram.ui.Components.BackupImageView
import sh.packit.core.stickers.TelegramStickerLoader

@Composable
fun TelegramSticker(
    stickerKey: String,
    modifier: Modifier = Modifier,
    sizeDp: Dp = 120.dp,
    roundRadiusDp: Dp = 36.dp,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null
) {
    val cornerShape = RoundedCornerShape(roundRadiusDp)
    val clickModifier = if (onClick != null || onLongClick != null) {
        Modifier.combinedClickable(
            onClick = { onClick?.invoke() },
            onLongClick = { onLongClick?.invoke() }
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .size(sizeDp)
            .clip(cornerShape)
            .then(clickModifier)
    ) {
        AndroidView(
            modifier = Modifier.matchParentSize(),
            factory = { context ->
                BackupImageView(context).apply {
                    if (roundRadiusDp > 0.dp) {
                        setRoundRadius(AndroidUtilities.dp(roundRadiusDp.value))
                    }
                    TelegramStickerLoader.loadSticker(this, stickerKey, sizeDp.value.toInt())
                }
            },
            update = { view ->
                TelegramStickerLoader.loadSticker(view, stickerKey, sizeDp.value.toInt())
            }
        )
    }
}
