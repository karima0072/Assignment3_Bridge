package notification;

import sender.MessageSender;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class EncryptedNotification extends Notification {
    public EncryptedNotification(MessageSender sender) {
        super(sender);
    }

    @Override
    public void send(String recipient, String message) {
        if (!sender.isAvailable()) {
            System.out.println("Encrypted notification delivery failed: provider is unavailable.");
            return;
        }

        // Base64 is encoding, not secure encryption.
        String encodedMessage = Base64.getEncoder()
                .encodeToString(message.getBytes(StandardCharsets.UTF_8));

        if (!sender.sendMessage(recipient, "Encrypted Notification", encodedMessage)) {
            System.out.println("Encrypted notification delivery failed.");
        }
    }
}
