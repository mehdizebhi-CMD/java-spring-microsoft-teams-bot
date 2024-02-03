package dev.mehdizebhi.tbot.core;

import com.microsoft.graph.logger.DefaultLogger;
import com.microsoft.graph.serializer.DefaultSerializer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class ResourcesSubscriptionGraphApiImplTest {

    private @Autowired ResourcesSubscriptionGraphApi graphApi;
    public DefaultSerializer serializer;
    private final static String TEAM_ID = "";
    private final static String CHANNEL_ID = "";

    @BeforeEach
    public void setUp() {
        serializer = new DefaultSerializer(new DefaultLogger());
    }

    @Test
    void getSubscriptions() {
        var subscriptionsOp = graphApi.getSubscriptions();
        assertTrue(subscriptionsOp.isPresent());

        log.info(serializer.serializeObject(subscriptionsOp.get()));
    }

    @Test
    void createSubscription() {
    }

    @Test
    void getSubscription() {
    }

    @Test
    void updateSubscription() {
    }

    @Test
    void deleteSubscription() {
    }

    @Test
    void subscriptionReauthorize() {
    }
}