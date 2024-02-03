package dev.mehdizebhi.tbot.controller;

import dev.mehdizebhi.tbot.core.GraphClientHelper;
import dev.mehdizebhi.tbot.service.BotAuthorizationStoreService;
import dev.mehdizebhi.tbot.service.SubscriptionStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Controller
@Slf4j
public class HomeController {

    @Autowired
    private BotAuthorizationStoreService botAuthorizationStoreService;

    @Autowired
    private SubscriptionStoreService subscriptionStore;

    private static final String APP_ONLY = "APP-ONLY";

    /**
     * @return the template name to render
     */
    @GetMapping("/")
    public CompletableFuture<String> home(Model model, @RegisteredOAuth2AuthorizedClient("graph") OAuth2AuthorizedClient oauthClient) {
        return GraphClientHelper.getGraphClient(oauthClient).me().buildRequest().getAsync().thenApply(user -> {
            model.addAttribute("userPrincipalName", user.userPrincipalName);
            model.addAttribute("isAuthorizedBot", Objects.nonNull(botAuthorizationStoreService.load()));
            model.addAttribute("isApponlySubscribed", !subscriptionStore.getSubscriptionsForUser(APP_ONLY).isEmpty());
            return "home";
        }).exceptionally(e -> {
            model.addAttribute("userPrincipalName", "unknown");
            return "home";
        });
    }
}
