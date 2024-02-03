package dev.mehdizebhi.tbot.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommandExtractHelper {

    public static String[] extractCommandFromMessage(String message, String botName) {
        // Assuming the message format is "@BotName commandName additionalText"

        message = HtmlUtils.stripHtmlTags(message);
        log.info("messages {} received from bot {}", message, botName);
        // Split into three parts: bot name, command name, additional text
        String[] tokens = message.split(" ", 3);
        if (tokens.length == 3 && tokens[0].equalsIgnoreCase(botName)) {
            // Return the command name in lowercase and additional text
            return new String[]{tokens[1].toLowerCase(), tokens[2]};
        } else if (tokens.length == 2 && tokens[0].equalsIgnoreCase(botName)) {
            return new String[]{tokens[1].toLowerCase(), ""};
        }
        // Return null if the message doesn't match the expected format
        return new String[]{};
    }
}
