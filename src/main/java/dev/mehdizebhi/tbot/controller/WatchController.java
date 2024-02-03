package dev.mehdizebhi.tbot.controller;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import java.util.Objects;

import dev.mehdizebhi.tbot.core.ResourcesSubscriptionGraphApi;
import dev.mehdizebhi.tbot.service.BotAuthorizationStoreService;
import dev.mehdizebhi.tbot.service.CertificateStoreService;
import dev.mehdizebhi.tbot.service.SubscriptionStoreService;
import dev.mehdizebhi.tbot.core.GraphClientHelper;
import dev.mehdizebhi.tbot.util.Utilities;
import com.microsoft.graph.models.ChangeType;
import com.microsoft.graph.models.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/watch")
public class WatchController {

    private static final String CREATE_SUBSCRIPTION_ERROR = "Error creating subscription";
    private static final String REDIRECT_HOME = "redirect:/";
    private static final String REDIRECT_LOGOUT = "redirect:/logout";
    private static final String APP_ONLY = "APP-ONLY";

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SubscriptionStoreService subscriptionStore;

    @Autowired
    private CertificateStoreService certificateStore;

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    private BotAuthorizationStoreService botAuthorizationStoreService;

    @Autowired
    private ResourcesSubscriptionGraphApi subscriptionGraphApi;

    @Value("${notifications.host}")
    private String notificationHost;

    /**
     * The delegated auth page of the app. This will subscribe for the authenticated user's inbox on
     * Exchange Online
     *
     * @param model the model provided by Spring
     * @param authentication authentication information for the request
     * @param redirectAttributes redirect attributes provided by Spring
     * @param oauthClient a delegated auth OAuth2 client for the authenticated user
     * @return the name of the template used to render the response
     */
    @GetMapping("/delegated-subscription-notification")
    public CompletableFuture<String> delegated(Model model,
            OAuth2AuthenticationToken authentication, RedirectAttributes redirectAttributes,
            @RegisteredOAuth2AuthorizedClient("graph") OAuth2AuthorizedClient oauthClient) {

        final var graphClient =
                GraphClientHelper.getGraphClient(Objects.requireNonNull(oauthClient));

        // Get the authenticated user's info
        final var userFuture = graphClient.me().buildRequest()
                .select("displayName,mail,userPrincipalName").getAsync();

        // Create the subscription
        final var subscriptionRequest = new Subscription();
        subscriptionRequest.changeType = ChangeType.CREATED.toString();
        subscriptionRequest.notificationUrl = notificationHost + "/listen";
        subscriptionRequest.resource = "me/mailfolders/inbox/messages";
        subscriptionRequest.clientState = UUID.randomUUID().toString();
        subscriptionRequest.includeResourceData = false;
        subscriptionRequest.expirationDateTime = OffsetDateTime.now().plusHours(1);

        final var subscriptionFuture =
                graphClient.subscriptions().buildRequest().postAsync(subscriptionRequest);

        return userFuture.thenCombine(subscriptionFuture, (user, subscription) -> {
            log.info("Created subscription {} for user {}", subscription.id, user.displayName);

            // Save the authorized client so we can use it later from the notification
            // controller
            authorizedClientService.saveAuthorizedClient(oauthClient, authentication);

            // Add information to the model
            model.addAttribute("user", user);
            model.addAttribute("subscriptionId", subscription.id);

            final var subscriptionJson = Objects
                    .requireNonNull(
                            Objects.requireNonNull(graphClient.getHttpProvider()).getSerializer())
                    .serializeObject(subscription);
            model.addAttribute("subscription", subscriptionJson);

            // Add record in subscription store
            subscriptionStore.addSubscription(subscription,
                    Objects.requireNonNull(authentication.getName()));

            model.addAttribute("success", "Subscription created.");

            return "delegated";
        }).exceptionally(e -> {
            log.error(CREATE_SUBSCRIPTION_ERROR, e);
            redirectAttributes.addFlashAttribute("error", CREATE_SUBSCRIPTION_ERROR);
            redirectAttributes.addFlashAttribute("debug", e.getMessage());
            return REDIRECT_HOME;
        });
    }

    /**
     * The delegated auth page of the app.
     *
     * @param authentication authentication information for the request
     * @param redirectAttributes redirect attributes provided by Spring
     * @param oauthClient a delegated auth OAuth2 client for the authenticated user
     * @return the name of the template used to render the response
     */
    @GetMapping("/authorize-delegated-bot")
    public CompletableFuture<String> delegatedBot(
            OAuth2AuthenticationToken authentication,
            RedirectAttributes redirectAttributes,
            @RegisteredOAuth2AuthorizedClient("graph") OAuth2AuthorizedClient oauthClient) {

        final var graphClient =
                GraphClientHelper.getGraphClient(Objects.requireNonNull(oauthClient));

        // Get the authenticated user's info
        final var userFuture = graphClient.me().buildRequest()
                .select("displayName,mail,userPrincipalName").getAsync();

        return userFuture.thenApply(user -> {

            // Save the authorized client so we can use it later from the notification
            // controller
            botAuthorizationStoreService.save(oauthClient, authentication);

            log.info("Save bot authorization for user {}", user.displayName);

            redirectAttributes.addFlashAttribute("success", "Subscription created.");
            return REDIRECT_HOME;

        }).exceptionally(e -> {
            log.error(CREATE_SUBSCRIPTION_ERROR, e);
            redirectAttributes.addFlashAttribute("error", CREATE_SUBSCRIPTION_ERROR);
            redirectAttributes.addFlashAttribute("debug", e.getMessage());
            return REDIRECT_HOME;
        });
    }


    /**
     * The app-only auth page of the app. This will subscribe for notifications on all new Teams
     * channel messages
     *
     * @param model the model provided by Spring
     * @param redirectAttributes redirect attributes provided by Spring
     * @param oauthClient an app-only auth OAuth2 client
     * @return the name of the template used to render the response
     */
    @GetMapping("/subscribe-apponly")
    public CompletableFuture<String> apponly(Model model, RedirectAttributes redirectAttributes,
            @RegisteredOAuth2AuthorizedClient("apponly") OAuth2AuthorizedClient oauthClient) {

        final var graphClient =
                GraphClientHelper.getGraphClient(Objects.requireNonNull(oauthClient));

        // Apps are only allowed one subscription to the /teams/getAllMessages resource
        // If we already had one, delete it so we can create a new one
        final var existingSubscriptions = subscriptionStore.getSubscriptionsForUser(APP_ONLY);
        for (final var sub : existingSubscriptions) {
            graphClient.subscriptions(Utilities.ensureNonNull(sub.subscriptionId)).buildRequest()
                    .delete();
        }

        // Create the subscription
        final var subscriptionRequest = new Subscription();
        subscriptionRequest.changeType = ChangeType.CREATED.toString();
        subscriptionRequest.notificationUrl = notificationHost + "/listen";
        subscriptionRequest.resource = "/teams/getAllMessages";
        subscriptionRequest.clientState = UUID.randomUUID().toString();
        subscriptionRequest.includeResourceData = true;
        subscriptionRequest.expirationDateTime = OffsetDateTime.now().plusHours(1);
        subscriptionRequest.encryptionCertificate = certificateStore.getBase64EncodedCertificate();
        subscriptionRequest.encryptionCertificateId = certificateStore.getCertificateId();

        return graphClient.subscriptions().buildRequest().postAsync(subscriptionRequest)
                .thenApply(subscription -> {
                    log.info("Created subscription {} for all Teams messages", subscription.id);

                    // TODO: Move to subscription info page
                    // Add information to the model
                    /*model.addAttribute("subscriptionId", subscription.id);

                    var subscriptionJson = Objects.requireNonNull(
                            Objects.requireNonNull(graphClient.getHttpProvider()).getSerializer())
                            .serializeObject(subscription);
                    model.addAttribute("subscription", subscriptionJson);*/

                    // Add record in subscription store
                    subscriptionStore.addSubscription(subscription, APP_ONLY);

                    redirectAttributes.addFlashAttribute("success", "Subscription created.");
                    return REDIRECT_HOME;
                }).exceptionally(e -> {
                    log.error(CREATE_SUBSCRIPTION_ERROR, e);
                    redirectAttributes.addFlashAttribute("error", CREATE_SUBSCRIPTION_ERROR);
                    redirectAttributes.addFlashAttribute("debug", e.getMessage());
                    return REDIRECT_HOME;
                });
    }


    /**
     * Deletes a subscription and logs the user out
     *
     * @param subscriptionId the subscription ID to delete
     * @param oauthClient a delegated auth OAuth2 client for the authenticated user
     * @return a redirect to the logout page
     */
    @GetMapping("/unsubscribe-delegated-notification")
    public CompletableFuture<String> unsubscribe(
            @RequestParam(value = "subscriptionId") @Nonnull final String subscriptionId,
            @RegisteredOAuth2AuthorizedClient("graph") OAuth2AuthorizedClient oauthClient) {

        final var graphClient =
                GraphClientHelper.getGraphClient(Objects.requireNonNull(oauthClient));

        return graphClient.subscriptions(subscriptionId).buildRequest().deleteAsync()
                .thenApply(sub -> {
                    // Remove subscription from store
                    subscriptionStore.deleteSubscription(Objects.requireNonNull(subscriptionId));

                    // Logout user
                    return REDIRECT_LOGOUT;
                });
    }

    /**
     * Deletes a subscription and logs the user out
     *
     * @param redirectAttributes
     * @param oauthClient a delegated auth OAuth2 client for the authenticated user
     * @return a redirect to the logout page
     */
    @GetMapping("/unauthorized-delegated-bot")
    public CompletableFuture<String> unsubscribeDelegatedBot(
            RedirectAttributes redirectAttributes,
            @RegisteredOAuth2AuthorizedClient("graph") OAuth2AuthorizedClient oauthClient) {

        final var graphClient =
                GraphClientHelper.getGraphClient(Objects.requireNonNull(oauthClient));

        // Get the authenticated user's info
        final var userFuture = graphClient.me().buildRequest()
                .select("displayName,mail,userPrincipalName").getAsync();

        return userFuture.thenApply(user -> {

            // Save the authorized client so we can use it later from the notification
            // controller
            botAuthorizationStoreService.remove();

            log.info("User {} removed authorization bot", user.displayName);

            redirectAttributes.addFlashAttribute("success", "Subscription removed.");
            return REDIRECT_HOME;

        }).exceptionally(e -> {
            log.error(CREATE_SUBSCRIPTION_ERROR, e);
            redirectAttributes.addFlashAttribute("error", CREATE_SUBSCRIPTION_ERROR);
            redirectAttributes.addFlashAttribute("debug", e.getMessage());
            return REDIRECT_HOME;
        });
    }


    /**
     * Deletes an app-only subscription
     *
     * @param subscriptionId the subscription ID to delete
     * @param oauthClient an app-only auth OAuth2 client
     * @return a redirect to the home page
     */
    @GetMapping("/unsubscribe-apponly-message")
    public CompletableFuture<String> unsubscribeapponly(
            @RequestParam(value = "subscriptionId") @Nonnull final String subscriptionId,
            @RegisteredOAuth2AuthorizedClient("apponly") OAuth2AuthorizedClient oauthClient) {

        final var graphClient =
                GraphClientHelper.getGraphClient(Objects.requireNonNull(oauthClient));

        return graphClient.subscriptions(subscriptionId).buildRequest().deleteAsync()
                .thenApply(sub -> {
                    // Remove subscription from store
                    subscriptionStore.deleteSubscription(Objects.requireNonNull(subscriptionId));

                    // Logout user
                    return REDIRECT_HOME;
                });
    }

    @GetMapping("/unsubscribe-apponly")
    public String removeAllApponlySubscriptions(
            RedirectAttributes redirectAttributes) {

        final var existingSubscriptions = subscriptionGraphApi.getSubscriptions();
        if (existingSubscriptions.isPresent()) {
            for (final Subscription sub : existingSubscriptions.get().getCurrentPage()) {
                subscriptionGraphApi.deleteSubscription(sub.id);
                subscriptionStore.deleteSubscription(sub.id);
            }
        }
        redirectAttributes.addFlashAttribute("success", "All App-only subscriptions deleted.");
        return REDIRECT_HOME;
    }

    @GetMapping("/resubscribe-apponly")
    public String updateSubscriptions(
            RedirectAttributes redirectAttributes) {

        final var existingSubscriptions = subscriptionGraphApi.getSubscriptions();
        if (existingSubscriptions.isPresent()) {
            for (final Subscription sub : existingSubscriptions.get().getCurrentPage()) {
                sub.expirationDateTime = OffsetDateTime.now().plusHours(1);
                subscriptionGraphApi.updateSubscription(sub);
            }
        }
        redirectAttributes.addFlashAttribute("success", "All App-only subscriptions updated.");
        return REDIRECT_HOME;
    }
}
