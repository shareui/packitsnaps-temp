package org.telegram.messenger;

import android.graphics.drawable.Drawable;
import org.telegram.tgnet.TLRPC;

public class DocumentObject {
    public static class ThemeDocument extends Drawable {
        public void overrideWidthAndHeight(int width, int height) {}
        @Override
        public void draw(android.graphics.Canvas canvas) {}
        @Override
        public void setAlpha(int alpha) {}
        @Override
        public void setColorFilter(android.graphics.ColorFilter colorFilter) {}
        @Override
        public int getOpacity() { return 0; }
    }

    public static SvgHelper.SvgDrawable getSvgThumb(TLRPC.Document document, int colorKey, float alpha) {
        return null;
    }
}
