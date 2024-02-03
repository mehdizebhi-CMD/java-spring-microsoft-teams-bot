package dev.mehdizebhi.tbot.service;

import dev.mehdizebhi.tbot.dto.UserAuthorizedClientId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;

@Service
public class BotAuthorizationStoreService {

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;
    private UserAuthorizedClientId userKey = null;

    public void save(OAuth2AuthorizedClient authorizedClient, Authentication principal) {
        this.userKey = new UserAuthorizedClientId(
                authorizedClient.getClientRegistration().getRegistrationId(), principal.getName());

        authorizedClientService.saveAuthorizedClient(authorizedClient, principal);
    }

    public <T extends OAuth2AuthorizedClient> T load() {
        try {
            return authorizedClientService.loadAuthorizedClient(userKey.getClientRegistrationId(), userKey.getPrincipalName());
        } catch (Exception e) {
            return null;
        }
    }

    public void remove() {
        try {
            authorizedClientService.removeAuthorizedClient(userKey.getClientRegistrationId(), userKey.getPrincipalName());
            this.userKey = null;
        } catch (Exception e) {
            this.userKey = null;
        }
    }
}
