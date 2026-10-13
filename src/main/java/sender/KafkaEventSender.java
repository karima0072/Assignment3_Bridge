package sender;

public class KafkaEventSender implements MessageSender {
    private boolean available = true;

    public KafkaEventSender() {
    }

    @Override
    public boolean sendMessage(String recipient, String title, String body) {
        if (!available) {
            System.out.println("Kafka is unavailable. Event was not published.");
            return false;
        }

        System.out.println("=== Kafka Event ===");
        System.out.println("Topic: notifications");
        System.out.println("Recipient: " + recipient);
        System.out.println("Event title: " + title);
        System.out.println("Event body: " + body);
        System.out.println("Event published to Kafka topic.");
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
        return "Kafka";
    }
}
