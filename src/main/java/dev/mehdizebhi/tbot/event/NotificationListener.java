package dev.mehdizebhi.tbot.event;

import dev.mehdizebhi.tbot.service.BotCommandsProcessService;

import dev.mehdizebhi.tbot.util.CommandExtractHelper;
import com.microsoft.graph.models.ChangeNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

    @Autowired
    private BotCommandsProcessService botCommandsProcessService;

    @Value("${spring.application.name}")
    public String applicationName;

    @Async
    @EventListener(MessageNotificationEvent.class)
    public void handleNotificationMessage(MessageNotificationEvent event) {
    }

    @Async
    @EventListener(ChangeNotification.class)
    public void handleNotification(ChangeNotification notification) {
        var notificationOp = botCommandsProcessService.processNewChannelMessageNotification(notification);
        if (notificationOp.isPresent()){
            String[] commandAndText = CommandExtractHelper.extractCommandFromMessage(notificationOp.get().body, applicationName);
            if (commandAndText.length == 2) {
                botCommandsProcessService.executeCommand(commandAndText[0], commandAndText[1]);
            }
        }
    }
}
