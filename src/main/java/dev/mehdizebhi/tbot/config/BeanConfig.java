package dev.mehdizebhi.tbot.config;

import com.azure.identity.ClientSecretCredentialBuilder;
import com.corundumstudio.socketio.SocketIOServer;
import dev.mehdizebhi.tbot.core.*;
import dev.mehdizebhi.tbot.service.BotAuthorizationStoreService;
import dev.mehdizebhi.tbot.service.SubscriptionStoreService;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.requests.GraphServiceClient;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Configuration
public class BeanConfig {

    @Value("${spring.cloud.azure.active-directory.credential.client-id}")
    private String clientId;

    @Value("${spring.cloud.azure.active-directory.credential.client-secret}")
    private String clientSecret;

    @Value("${spring.cloud.azure.active-directory.profile.tenant-id}")
    private String tenantId;

    @Value("${spring.cloud.azure.active-directory.authorization-clients.apponly.scopes}")
    private String scopes;

    @Autowired
    private BotAuthorizationStoreService botAuthorizationStoreService;

    /**
     * @return A configured SocketIO server instance
     */
    @Bean
    public SocketIOServer socketIOServer() {
        var config = new com.corundumstudio.socketio.Configuration();
        config.setHostname("localhost");
        config.setPort(8081);
        return new SocketIOServer(config);
    }

    @Bean
    public TokenCredentialAuthProvider appAuthProvider() {
        var clientSecretCredential =  new ClientSecretCredentialBuilder()
                .clientId(this.clientId)
                .tenantId(this.tenantId)
                .clientSecret(this.clientSecret)
                .build();

        // Use the .default scope when using app-only auth
        return new TokenCredentialAuthProvider(
                List.of(this.scopes), clientSecretCredential);
    }

    @Bean
    public GraphServiceClient<Request> appClient() {
        return GraphServiceClient.builder()
                .authenticationProvider(this.appAuthProvider())
                .buildClient();
    }

    @Bean
    @Primary
    public MicrosoftTeamsGraphApi teamsGraphApi() {
        return new MicrosoftTeamsGraphApiImpl(this.appClient());
    }

    @Bean
    @Primary
    public ResourcesSubscriptionGraphApi subscriptionGraphApi(SubscriptionStoreService subscriptionStore) {
        return new ResourcesSubscriptionGraphApiImpl(this.appClient(), subscriptionStore);
    }

    @Bean(name = "delegatedTeamsGraphApi")
    @Lazy
    public MicrosoftTeamsGraphApi delegatedTeamsGraphApi() {
        return new MicrosoftTeamsGraphApiImpl(
                GraphClientHelper.getGraphClient(botAuthorizationStoreService.load()));
    }
}
