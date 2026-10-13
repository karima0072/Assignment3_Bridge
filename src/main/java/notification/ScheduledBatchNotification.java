package notification;

import sender.MessageSender;

import java.util.ArrayList;
import java.util.List;

public class ScheduledBatchNotification extends Notification {
    private final List<String> recipients;
    private int total;
    private int successful;
    private int failed;

    public ScheduledBatchNotification(MessageSender sender, List<String> recipients) {
        super(sender);
        this.recipients = new ArrayList<>(recipients);
    }

    /**
     * Sends to the stored recipient list. The single recipient argument is ignored.
     */
    @Override
    public void send(String recipient, String message) {
        total = recipients.size();
        successful = 0;
        failed = 0;

        for (String batchRecipient : recipients) {
            if (sender.isAvailable()
                    && sender.sendMessage(batchRecipient, "Scheduled Batch Notification", message)) {
                successful++;
            } else {
                failed++;
            }
        }

        System.out.println("Total: " + total);
        System.out.println("Successful: " + successful);
        System.out.println("Failed: " + failed);
    }

    public int getTotal() {
        return total;
    }

    public int getSuccessful() {
        return successful;
    }

    public int getFailed() {
        return failed;
    }
}
