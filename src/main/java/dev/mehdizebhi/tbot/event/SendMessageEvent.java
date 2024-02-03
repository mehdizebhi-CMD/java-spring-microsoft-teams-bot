package dev.mehdizebhi.tbot.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class SendMessageEvent extends ApplicationEvent {

    private String content;
    private String teamId;
    private String channelId;

    public SendMessageEvent(Object source, String content) {
        super(source);
        this.content = content;
    }
}
