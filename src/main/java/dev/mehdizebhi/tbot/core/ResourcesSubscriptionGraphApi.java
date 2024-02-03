package dev.mehdizebhi.tbot.core;

import com.microsoft.graph.models.Subscription;
import com.microsoft.graph.requests.SubscriptionCollectionPage;

import java.util.Optional;

public interface ResourcesSubscriptionGraphApi {

    Optional<SubscriptionCollectionPage> getSubscriptions();

    Optional<Subscription> createSubscription(Subscription subscription);

    Optional<Subscription> getSubscription(String subscriptionId);

    Optional<Subscription> updateSubscription(Subscription subscription);

    Optional<Subscription> deleteSubscription(String subscriptionId);

    boolean subscriptionReauthorize(String subscriptionId);
}
