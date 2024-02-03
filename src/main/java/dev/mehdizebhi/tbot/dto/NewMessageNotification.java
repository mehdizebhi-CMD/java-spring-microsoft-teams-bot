package dev.mehdizebhi.tbot.dto;

import java.util.Objects;
import com.microsoft.graph.models.Message;
import org.springframework.lang.NonNull;

/**
 * Represents the information sent via SocketIO to subscribed
 * clients when a new message notification is received
 */
public class NewMessageNotification {

    /**
     * The subject of the message
     */
    public final String subject;

    /**
     * The id of the message, can be used to GET the message via Graph
     */
    public final String id;

    public NewMessageNotification(@NonNull Message message) {
        Objects.requireNonNull(message);
        subject = message.subject;
        id = message.id;
    }
}
