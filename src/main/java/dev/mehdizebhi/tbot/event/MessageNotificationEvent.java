package dev.mehdizebhi.tbot.event;

import dev.mehdizebhi.tbot.dto.SubscriptionRecord;
import com.microsoft.graph.models.ChangeNotification;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class MessageNotificationEvent extends ApplicationEvent {

    private ChangeNotification notification;
    private SubscriptionRecord subscription;

    public MessageNotificationEvent(Object source, ChangeNotification notification, SubscriptionRecord subscription) {
        super(source);
        this.notification = notification;
        this.subscription = subscription;
    }
}
