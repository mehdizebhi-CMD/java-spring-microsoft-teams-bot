package dev.mehdizebhi.tbot.event;

import dev.mehdizebhi.tbot.core.MicrosoftTeamsGraphApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class SendMessageListener {

    @Autowired
    private MicrosoftTeamsGraphApi graphApi;

    @Async
    @EventListener(SendMessageEvent.class)
    public void handleSendingMessage(SendMessageEvent event) {
        graphApi.sendChannelMessage(event.getContent(), event.getTeamId(), event.getChannelId());
    }
}
