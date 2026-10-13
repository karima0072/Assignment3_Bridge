package notification;

import sender.MessageSender;

public class UrgentNotification extends Notification {
    private MessageSender fallbackSender;

    public UrgentNotification(MessageSender sender) {
        super(sender);
    }

    public void setFallbackSender(MessageSender fallbackSender) {
        this.fallbackSender = fallbackSender;
    }

    @Override
    public void send(String recipient, String message) {
        String urgentMessage = "[URGENT] " + message;

        if (sender.isAvailable()
                && sender.sendMessage(recipient, "Urgent Notification", urgentMessage)) {
            return;
        }

        System.out.println("Primary urgent delivery failed. Trying fallback.");
        if (fallbackSender != null && fallbackSender.isAvailable()
                && fallbackSender.sendMessage(recipient, "Urgent Notification", urgentMessage)) {
            return;
        }

        System.out.println("Urgent notification delivery failed: primary and fallback could not deliver.");
    }
}
