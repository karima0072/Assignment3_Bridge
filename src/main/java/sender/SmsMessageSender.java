package sender;

public class SmsMessageSender implements MessageSender {
    private static final int MAX_BODY_LENGTH = 160;

    private boolean available = true;

    public SmsMessageSender() {
    }

    @Override
    public boolean sendMessage(String recipient, String title, String body) {
        if (!available) {
            System.out.println("SMS is unavailable. Message was not sent.");
            return false;
        }

        if (body.length() > MAX_BODY_LENGTH) {
            System.out.println("SMS error: message body exceeds 160 characters. Message was not sent.");
            return false;
        }

        System.out.println("=== SMS ===");
        System.out.println("Phone recipient: " + recipient);
        System.out.println("Title: " + title);
        System.out.println("Text: " + body);
        System.out.println("Body length: " + body.length() + "/" + MAX_BODY_LENGTH);
        System.out.println("SMS delivered.");
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
        return "SMS";
    }
}
