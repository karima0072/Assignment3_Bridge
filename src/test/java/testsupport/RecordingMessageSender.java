package testsupport;

import sender.MessageSender;

import java.util.ArrayList;
import java.util.List;

// Records calls and either returns configured results or forwards to a real provider.
public class RecordingMessageSender implements MessageSender {
    public boolean available = true;
    public boolean deliveryResult = true;
    public String recipient;
    public String title;
    public String body;
    public int calls;
    public final List<String> recipients = new ArrayList<>();
    public final List<Boolean> results = new ArrayList<>();
    public final List<Boolean> plannedResults = new ArrayList<>();
    private final MessageSender delegate;

    public RecordingMessageSender() {
        this.delegate = null;
    }

    public RecordingMessageSender(MessageSender delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean sendMessage(String recipient, String title, String body) {
        this.recipient = recipient;
        this.title = title;
        this.body = body;
        recipients.add(recipient);
        calls++;

        boolean result;
        if (delegate != null) {
            result = delegate.sendMessage(recipient, title, body);
        } else if (calls <= plannedResults.size()) {
            result = plannedResults.get(calls - 1);
        } else {
            result = deliveryResult;
        }
        results.add(result);
        return result;
    }

    @Override
    public boolean isAvailable() {
        return delegate == null ? available : delegate.isAvailable();
    }

    @Override
    public String getChannelName() {
        return delegate == null ? "Test" : delegate.getChannelName();
    }
}
