import notification.EncryptedNotification;
import notification.MarketingNotification;
import notification.ScheduledBatchNotification;
import notification.UrgentNotification;
import sender.EmailMessageSender;
import sender.PushNotificationSender;
import sender.SmsMessageSender;
import sender.TelegramMessageSender;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Multi-Channel Notification Gateway");

        TelegramMessageSender telegram = new TelegramMessageSender();
        EmailMessageSender email = new EmailMessageSender();
        SmsMessageSender sms = new SmsMessageSender();

        System.out.println("\n=== 1. Urgent Notification + Telegram ===");
        UrgentNotification urgent = new UrgentNotification(telegram);
        urgent.send("@student", "Please check the assignment deadline.");

        System.out.println("\n=== 2. Encrypted Notification + Email (Base64 encoding) ===");
        EncryptedNotification encoded = new EncryptedNotification(email);
        encoded.send("student@example.com", "Hello, student!");

        System.out.println("\n=== 3. Scheduled Batch Notification + SMS ===");
        List<String> phoneNumbers = List.of(
                "+77001234561",
                "+77001234562",
                "+77001234563",
                "+77001234564",
                "+77001234565"
        );
        ScheduledBatchNotification batch = new ScheduledBatchNotification(sms, phoneNumbers);
        // Batch sending uses the stored phone numbers, so this recipient is ignored.
        batch.send("", "Reminder: the workshop starts at 10:00.");

        System.out.println("\n=== 4. Runtime Switching: Telegram -> Email ===");
        UrgentNotification switchingNotification = new UrgentNotification(telegram);
        System.out.println("Sending through Telegram.");
        switchingNotification.send("@student", "Please review your assignment.");
        System.out.println("Reusing the SAME UrgentNotification object: switching to Email.");
        switchingNotification.setSender(email);
        switchingNotification.send("student@example.com", "Please submit your assignment today.");

        System.out.println("\n=== 5. Telegram Unavailable -> SMS Fallback ===");
        TelegramMessageSender primaryTelegram = new TelegramMessageSender();
        SmsMessageSender fallbackSms = new SmsMessageSender();
        primaryTelegram.setAvailable(false);
        System.out.println("Telegram availability: " + primaryTelegram.isAvailable());

        UrgentNotification fallbackNotification = new UrgentNotification(primaryTelegram);
        fallbackNotification.setFallbackSender(fallbackSms);
        fallbackNotification.send("+77001234567", "Deadline is today. Submit now.");

        System.out.println("\n=== 6. Marketing Notification + Push ===");
        PushNotificationSender push = new PushNotificationSender();
        MarketingNotification marketingPush = new MarketingNotification(push);
        marketingPush.send("student-device", "Save 20% on our Java course.");

    }
}
