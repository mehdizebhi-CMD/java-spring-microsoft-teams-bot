package dev.mehdizebhi.tbot.core;

import dev.mehdizebhi.tbot.service.SubscriptionStoreService;
import com.microsoft.graph.models.Subscription;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.SubscriptionCollectionPage;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;

import java.util.Objects;
import java.util.Optional;

@Slf4j
public class ResourcesSubscriptionGraphApiImpl implements ResourcesSubscriptionGraphApi {

    private final GraphServiceClient<Request> graphServiceClient;

    private final SubscriptionStoreService subscriptionStore;


    public ResourcesSubscriptionGraphApiImpl(GraphServiceClient<Request> graphServiceClient, SubscriptionStoreService subscriptionStore) {
        this.graphServiceClient = graphServiceClient;
        this.subscriptionStore = subscriptionStore;
    }

    private static final String APP_ONLY = "APP-ONLY";

    @Override
    public Optional<SubscriptionCollectionPage> getSubscriptions() {
        try {
            SubscriptionCollectionPage subscriptions = graphServiceClient.subscriptions()
                    .buildRequest()
                    .get();

            return Optional.of(subscriptions);
        } catch (Exception e) {
            log.error("Can not get subscriptions: ", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Subscription> createSubscription(Subscription subscription) {
        try {
            var newSubscription = graphServiceClient.subscriptions()
                    .buildRequest()
                    .post(subscription);

            // Add record in subscription store
            subscriptionStore.addSubscription(newSubscription, APP_ONLY);
            return Optional.of(newSubscription);
        } catch (Exception e) {
            log.error("Can not create subscription: ", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Subscription> getSubscription(String subscriptionId) {
        try {
            Subscription subscription = graphServiceClient.subscriptions(subscriptionId)
                    .buildRequest()
                    .get();

            return Optional.of(subscription);
        } catch (Exception e) {
            log.error("Can not get subscription: ", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Subscription> updateSubscription(Subscription subscription) {
        try {
            var newSubscription = graphServiceClient.subscriptions(subscription.id)
                    .buildRequest()
                    .patch(subscription);

            // Update record in subscription store
            subscriptionStore.addSubscription(newSubscription, APP_ONLY);
            return Optional.of(newSubscription);
        } catch (Exception e) {
            log.error("Can not update subscription: ", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Subscription> deleteSubscription(String subscriptionId) {
        try {
            var deleteSubscription = graphServiceClient.subscriptions(subscriptionId)
                    .buildRequest()
                    .delete();

            // Remove subscription from store
            subscriptionStore.deleteSubscription(Objects.requireNonNull(subscriptionId));
            return Optional.of(deleteSubscription);
        } catch (Exception e) {
            log.error("Can not delete subscription: ", e);
            return Optional.empty();
        }
    }

    @Override
    public boolean subscriptionReauthorize(String subscriptionId) {
        try {
            graphServiceClient.subscriptions("subscriptionId")
                    .reauthorize()
                    .buildRequest()
                    .post();

            return true;
        } catch (Exception e) {
            log.error("Can not reauthorize subscription: ", e);
            return false;
        }
    }
}
