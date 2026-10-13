import notification.UrgentNotification;
import sender.EmailMessageSender;
import sender.SmsMessageSender;
import sender.TelegramMessageSender;

public class Main {
    public static void main(String[] args) {
        System.out.println("Multi-Channel Notification Gateway");

        TelegramMessageSender telegram = new TelegramMessageSender();
        EmailMessageSender email = new EmailMessageSender();
        UrgentNotification notification = new UrgentNotification(telegram);

        System.out.println("\n=== Runtime switching: send through Telegram ===");
        notification.send("@student", "Please check the assignment deadline.");

        System.out.println("\n=== Runtime switching: same notification, now using Email ===");
        notification.setSender(email);
        notification.send("student@example.com", "Please submit your assignment today.");

        System.out.println("\n=== Fallback: unavailable Telegram, available SMS ===");
        TelegramMessageSender primaryTelegram = new TelegramMessageSender();
        SmsMessageSender fallbackSms = new SmsMessageSender();
        primaryTelegram.setAvailable(false);
        System.out.println("Telegram availability: " + primaryTelegram.isAvailable());

        UrgentNotification fallbackNotification = new UrgentNotification(primaryTelegram);
        fallbackNotification.setFallbackSender(fallbackSms);
        fallbackNotification.send("+77001234567", "Deadline is today. Submit now.");
    }
}
