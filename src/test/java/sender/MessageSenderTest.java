package sender;

import org.junit.jupiter.api.Test;
import testsupport.ConsoleTestSupport;

import static org.junit.jupiter.api.Assertions.*;

class MessageSenderTest extends ConsoleTestSupport {
    @Test
    void telegramSendsRecipientTitleAndBody() {
        TelegramMessageSender sender = new TelegramMessageSender();
        assertTrue(sender.isAvailable());
        assertTrue(sender.sendMessage("@student", "Deadline", "Submit today"));
        assertEquals("Telegram", sender.getChannelName());
        assertTrue(consoleOutput().contains("Chat recipient: @student"));
        assertTrue(consoleOutput().contains("Title: *Deadline*"));
        assertTrue(consoleOutput().contains("Message: Submit today"));
    }

    @Test
    void emailSendsTitleBodyAndSignature() {
        EmailMessageSender sender = new EmailMessageSender();
        assertTrue(sender.sendMessage("student@example.com", "Welcome", "Hello"));
        assertTrue(consoleOutput().contains("To: student@example.com"));
        assertTrue(consoleOutput().contains("Subject: Welcome"));
        assertTrue(consoleOutput().contains("Body: Hello"));
        assertTrue(consoleOutput().contains("Best regards, Notification Gateway"));
    }

    @Test
    void unavailableProviderRejectsDeliveryAndCanBeRestored() {
        TelegramMessageSender sender = new TelegramMessageSender();
        sender.setAvailable(false);
        assertFalse(sender.isAvailable());
        assertFalse(sender.sendMessage("@student", "Title", "Body"));
        assertTrue(consoleOutput().contains("Telegram is unavailable"));
        assertFalse(consoleOutput().contains("Telegram message delivered."));
        sender.setAvailable(true);
        assertTrue(sender.isAvailable());
        assertTrue(sender.sendMessage("@student", "Title", "Body"));
    }

    @Test
    void smsAcceptsExactly160Characters() {
        String body = "x".repeat(160);
        assertTrue(new SmsMessageSender().sendMessage("+77001234567", "Title", body));
        assertTrue(consoleOutput().contains("Text: " + body));
        assertTrue(consoleOutput().contains("Body length: 160/160"));
    }

    @Test
    void smsRejects161CharactersWithoutTruncatingOrSending() {
        assertFalse(new SmsMessageSender().sendMessage("+77001234567", "Title", "x".repeat(161)));
        assertTrue(consoleOutput().contains("SMS error: message body exceeds 160 characters"));
        assertFalse(consoleOutput().contains("Text:"));
        assertFalse(consoleOutput().contains("SMS delivered."));
    }

    @Test
    void pushSendsToDeviceAndRejectsWhenUnavailable() {
        PushNotificationSender sender = new PushNotificationSender();
        assertTrue(sender.isAvailable());
        assertEquals("Push", sender.getChannelName());
        assertTrue(sender.sendMessage("device-1", "Update", "New message"));
        assertTrue(consoleOutput().contains("Device recipient: device-1"));
        assertTrue(consoleOutput().contains("Title: Update"));
        assertTrue(consoleOutput().contains("Body: New message"));
        sender.setAvailable(false);
        assertFalse(sender.isAvailable());
        assertFalse(sender.sendMessage("device-1", "Update", "New message"));
    }
}
