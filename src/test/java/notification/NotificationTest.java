package notification;

import org.junit.jupiter.api.Test;
import sender.EmailMessageSender;
import sender.PushNotificationSender;
import sender.SmsMessageSender;
import sender.TelegramMessageSender;
import testsupport.ConsoleTestSupport;
import testsupport.RecordingMessageSender;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTest extends ConsoleTestSupport {
    @Test
    void urgentUsesPrimaryOnceAndDoesNotCallFallbackAfterSuccess() {
        RecordingMessageSender primary = new RecordingMessageSender();
        RecordingMessageSender fallback = new RecordingMessageSender();
        UrgentNotification notification = new UrgentNotification(primary);
        notification.setFallbackSender(fallback);
        notification.send("student", "Deadline today");
        assertEquals(1, primary.calls);
        assertEquals(0, fallback.calls);
        assertEquals("student", primary.recipient);
        assertEquals("Urgent Notification", primary.title);
        assertEquals("[URGENT] Deadline today", primary.body);
        assertEquals(List.of(true), primary.results);
    }

    @Test
    void urgentUsesSmsWhenTelegramIsUnavailable() {
        TelegramMessageSender telegram = new TelegramMessageSender();
        telegram.setAvailable(false);
        RecordingMessageSender primary = new RecordingMessageSender(telegram);
        RecordingMessageSender fallback = new RecordingMessageSender(new SmsMessageSender());
        UrgentNotification notification = new UrgentNotification(primary);
        notification.setFallbackSender(fallback);
        notification.send("+77001234567", "Submit now");
        assertEquals(0, primary.calls);
        assertEquals(1, fallback.calls);
        assertEquals("+77001234567", fallback.recipient);
        assertEquals("Urgent Notification", fallback.title);
        assertEquals("[URGENT] Submit now", fallback.body);
        assertEquals(List.of(true), fallback.results);
        assertTrue(consoleOutput().contains("SMS delivered."));
    }

    @Test
    void urgentUsesFallbackWhenAvailablePrimaryReturnsFalse() {
        RecordingMessageSender primary = new RecordingMessageSender();
        primary.deliveryResult = false;
        RecordingMessageSender fallback = new RecordingMessageSender();
        UrgentNotification notification = new UrgentNotification(primary);
        notification.setFallbackSender(fallback);
        notification.send("student", "Help");
        assertEquals(1, primary.calls);
        assertEquals(1, fallback.calls);
        assertEquals(primary.recipient, fallback.recipient);
        assertEquals(primary.title, fallback.title);
        assertEquals(primary.body, fallback.body);
        assertEquals(List.of(false), primary.results);
        assertEquals(List.of(true), fallback.results);
    }

    @Test
    void urgentReportsFailureWhenBothDeliveriesFail() {
        RecordingMessageSender primary = new RecordingMessageSender();
        RecordingMessageSender fallback = new RecordingMessageSender();
        primary.deliveryResult = false;
        fallback.deliveryResult = false;
        UrgentNotification notification = new UrgentNotification(primary);
        notification.setFallbackSender(fallback);
        assertDoesNotThrow(() -> notification.send("student", "Help"));
        assertEquals(1, primary.calls);
        assertEquals(1, fallback.calls);
        assertEquals(List.of(false), primary.results);
        assertEquals(List.of(false), fallback.results);
        assertTrue(consoleOutput().contains("Urgent notification delivery failed: primary and fallback could not deliver."));
    }

    @Test
    void urgentDoesNotCallUnavailableFallback() {
        RecordingMessageSender primary = new RecordingMessageSender();
        primary.deliveryResult = false;
        RecordingMessageSender fallback = new RecordingMessageSender();
        fallback.available = false;
        UrgentNotification notification = new UrgentNotification(primary);
        notification.setFallbackSender(fallback);
        notification.send("student", "Help");
        assertEquals(1, primary.calls);
        assertEquals(0, fallback.calls);
        assertTrue(consoleOutput().contains("Urgent notification delivery failed:"));
    }

    @Test
    void encryptedSendsUtf8Base64Body() {
        RecordingMessageSender sender = new RecordingMessageSender();
        String original = "Hello, Қазақ!";
        new EncryptedNotification(sender).send("student", original);
        assertEquals(1, sender.calls);
        assertEquals("student", sender.recipient);
        assertEquals("Encrypted Notification", sender.title);
        assertEquals(Base64.getEncoder().encodeToString(original.getBytes(StandardCharsets.UTF_8)), sender.body);
        assertEquals(original, new String(Base64.getDecoder().decode(sender.body), StandardCharsets.UTF_8));
    }

    @Test
    void batchCountsMixedResultsAndUsesStoredRecipients() {
        RecordingMessageSender sender = new RecordingMessageSender();
        sender.plannedResults.addAll(List.of(true, false, true));
        ScheduledBatchNotification notification = new ScheduledBatchNotification(sender, List.of("a", "b", "c"));
        notification.send("ignored", "Batch message");
        assertEquals(List.of("a", "b", "c"), sender.recipients);
        assertEquals(3, sender.calls);
        assertEquals("Batch message", sender.body);
        assertEquals(3, notification.getTotal());
        assertEquals(2, notification.getSuccessful());
        assertEquals(1, notification.getFailed());
        assertEquals("Total: 3\nSuccessful: 2\nFailed: 1\n", consoleOutput());
    }

    @Test
    void batchResetsCountersAndCountsUnavailableDeliveriesAsFailures() {
        RecordingMessageSender sender = new RecordingMessageSender();
        ScheduledBatchNotification notification = new ScheduledBatchNotification(sender, List.of("a", "b"));
        notification.send("ignored", "First batch");
        assertEquals(2, notification.getSuccessful());
        sender.available = false;
        notification.send("ignored", "Second batch");
        assertEquals(2, sender.calls);
        assertEquals(2, notification.getTotal());
        assertEquals(0, notification.getSuccessful());
        assertEquals(2, notification.getFailed());
    }

    @Test
    void sameNotificationSwitchesFromTelegramToEmail() {
        RecordingMessageSender telegram = new RecordingMessageSender(new TelegramMessageSender());
        RecordingMessageSender email = new RecordingMessageSender(new EmailMessageSender());
        Notification notification = new UrgentNotification(telegram);
        notification.send("@student", "First message");
        assertEquals(1, telegram.calls);
        assertEquals(0, email.calls);
        notification.setSender(email);
        notification.send("student@example.com", "Second message");
        assertEquals(1, telegram.calls);
        assertEquals(1, email.calls);
        assertEquals("@student", telegram.recipient);
        assertEquals("[URGENT] First message", telegram.body);
        assertEquals("student@example.com", email.recipient);
        assertEquals("[URGENT] Second message", email.body);
        assertEquals(List.of(true), telegram.results);
        assertEquals(List.of(true), email.results);
    }

    @Test
    void marketingWorksWithPush() {
        RecordingMessageSender sender = new RecordingMessageSender(new PushNotificationSender());
        new MarketingNotification(sender).send("device-1", "Save 20%");
        assertEquals(1, sender.calls);
        assertEquals("device-1", sender.recipient);
        assertEquals("Marketing Notification", sender.title);
        assertEquals("[PROMO] Save 20%", sender.body);
        assertEquals(List.of(true), sender.results);
        assertTrue(consoleOutput().contains("Push notification delivered to device."));
    }

    @Test
    void marketingWorksWithExistingEmail() {
        RecordingMessageSender sender = new RecordingMessageSender(new EmailMessageSender());
        new MarketingNotification(sender).send("student@example.com", "Join our workshop");
        assertEquals(1, sender.calls);
        assertEquals("student@example.com", sender.recipient);
        assertEquals("Marketing Notification", sender.title);
        assertEquals("[PROMO] Join our workshop", sender.body);
        assertEquals(List.of(true), sender.results);
        assertTrue(consoleOutput().contains("Email delivered."));
    }
}
