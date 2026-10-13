package notification;

import sender.MessageSender;

public class MarketingNotification extends Notification {
    public MarketingNotification(MessageSender sender) {
        super(sender);
    }

    @Override
    public void send(String recipient, String message) {
        if (!sender.isAvailable()) {
            System.out.println("Marketing notification delivery failed: provider is unavailable.");
            return;
        }

        String promotionalMessage = "[PROMO] " + message;
        if (!sender.sendMessage(recipient, "Marketing Notification", promotionalMessage)) {
            System.out.println("Marketing notification delivery failed.");
        }
    }
}
