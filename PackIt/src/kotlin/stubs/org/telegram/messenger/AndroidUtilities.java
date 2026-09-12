package org.telegram.messenger;

import android.graphics.Typeface;

public class AndroidUtilities {
    public static final int TYPEFACE_ROBOTO_MEDIUM = 1;

    public static int dp(float value) {
        return (int) value;
    }

    public static void runOnUIThread(Runnable runnable) {}
    public static void runOnUIThread(Runnable runnable, long delay) {}
    public static Typeface getTypeface(int type) {
        return null;
    }
}
