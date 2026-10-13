package sender;

public class TelegramMessageSender implements MessageSender {
    private boolean available = true;

    public TelegramMessageSender() {
    }

    @Override
    public boolean sendMessage(String recipient, String title, String body) {
        if (!available) {
            System.out.println("Telegram is unavailable. Message was not sent.");
            return false;
        }

        System.out.println("=== Telegram Message ===");
        System.out.println("Chat recipient: " + recipient);
        System.out.println("Title: *" + title + "*");
        System.out.println("Message: " + body);
        System.out.println("Telegram message delivered.");
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
        return "Telegram";
    }
}
