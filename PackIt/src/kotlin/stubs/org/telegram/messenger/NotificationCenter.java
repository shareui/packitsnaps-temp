package org.telegram.messenger;

public class NotificationCenter {
    public static final int diceStickersDidLoad = 100;

    public interface NotificationCenterDelegate {
        void didReceivedNotification(int id, int account, Object... args);
    }

    public static NotificationCenter getInstance(int account) {
        return null;
    }

    public void addObserver(NotificationCenterDelegate observer, int id) {}
    public void removeObserver(NotificationCenterDelegate observer, int id) {}
}
