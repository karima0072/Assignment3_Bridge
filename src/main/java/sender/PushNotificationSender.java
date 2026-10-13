package sender;

public class PushNotificationSender implements MessageSender {
    private boolean available = true;

    public PushNotificationSender() {
    }

    @Override
    public boolean sendMessage(String recipient, String title, String body) {
        if (!available) {
            System.out.println("Push is unavailable. Notification was not sent.");
            return false;
        }

        System.out.println("=== Push Notification ===");
        System.out.println("Device recipient: " + recipient);
        System.out.println("Title: " + title);
        System.out.println("Body: " + body);
        System.out.println("Push notification delivered to device.");
        return true;
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String getChannelName() {
        return "Push";
    }
}
