package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC {
    public static class InputStickerSet {}

    public static class TL_inputStickerSetShortName extends InputStickerSet {
        public String short_name;
    }

    public static class StickerSet {
        public long id;
        public String short_name;
    }

    public static class Document {}

    public static class TL_messages_stickerSet {
        public StickerSet set;
        public ArrayList<Document> documents = new ArrayList<>();
    }
}
