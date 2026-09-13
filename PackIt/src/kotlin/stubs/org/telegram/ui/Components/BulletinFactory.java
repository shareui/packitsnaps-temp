package org.telegram.ui.Components;

import org.telegram.ui.ActionBar.BaseFragment;

public class BulletinFactory {
    public static BulletinFactory of(BaseFragment fragment) { return null; }
    public static BulletinFactory global() { return null; }
    public Bulletin createSimpleBulletin(int iconRawId, CharSequence text) { return null; }
    public Bulletin createSimpleBulletin(int iconRawId, CharSequence text, CharSequence button, Runnable onButtonClick) { return null; }
    public Bulletin createSimpleBulletin(int iconRawId, CharSequence text, CharSequence button, int duration, Runnable onButtonClick) { return null; }
}
