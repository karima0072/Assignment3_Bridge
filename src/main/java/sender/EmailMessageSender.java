package sender;

public class EmailMessageSender implements MessageSender {
    private boolean available = true;

    public EmailMessageSender() {
    }

    @Override
    public boolean sendMessage(String recipient, String title, String body) {
        if (!available) {
            System.out.println("Email is unavailable. Email was not sent.");
            return false;
        }

        System.out.println("=== Email ===");
        System.out.println("To: " + recipient);
        System.out.println("Subject: " + title);
        System.out.println("Body: " + body);
        System.out.println("--");
        System.out.println("Best regards, Notification Gateway");
        System.out.println("Email delivered.");
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
        return "Email";
    }
}
