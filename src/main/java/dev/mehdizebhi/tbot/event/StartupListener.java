package dev.mehdizebhi.tbot.event;

import dev.mehdizebhi.tbot.core.ResourcesSubscriptionGraphApi;
import com.microsoft.graph.models.Subscription;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StartupListener {

    private @Autowired ResourcesSubscriptionGraphApi subscriptionGraphApi;

    @Async
    @EventListener(StartupEvent.class)
    public void handleStartupEvent() {
        this.unsubscribeAllActiveSubscribe();
    }

    private void unsubscribeAllActiveSubscribe() {
        final var existingSubscriptions = subscriptionGraphApi.getSubscriptions();
        if (existingSubscriptions.isPresent()) {
            log.info("Start unsubscribe all active subscriptions for this app");
            for (final Subscription sub : existingSubscriptions.get().getCurrentPage()) {
                subscriptionGraphApi.deleteSubscription(sub.id);
            }
            log.info("End unsubscribe all active subscriptions for this app");
        }
    }
}
