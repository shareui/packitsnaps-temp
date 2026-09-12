package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public class MediaDataController {
    public static MediaDataController getInstance(int currentAccount) {
        return null;
    }

    public TLRPC.TL_messages_stickerSet getStickerSetByName(String name) {
        return null;
    }

    public TLRPC.TL_messages_stickerSet getStickerSetByEmojiOrName(String name) {
        return null;
    }

    public TLRPC.TL_messages_stickerSet getStickerSet(TLRPC.InputStickerSet inputStickerSet, Integer hash, boolean cacheOnly, Utilities.Callback<TLRPC.TL_messages_stickerSet> callback) {
        return null;
    }

    public TLRPC.TL_messages_stickerSet getStickerSet(TLRPC.InputStickerSet inputStickerSet, boolean cacheOnly) {
        return null;
    }

    public void loadStickersByEmojiOrName(String name, boolean isDice, boolean isAnimated) {}
}
