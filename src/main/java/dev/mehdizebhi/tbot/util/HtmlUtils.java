package dev.mehdizebhi.tbot.util;

import java.util.regex.Pattern;

public class HtmlUtils {

    private static final Pattern HTML_TAGS_PATTERN = Pattern.compile("<[^>]*>");

    public static String stripHtmlTags(String input) {
        return HTML_TAGS_PATTERN.matcher(input).replaceAll("");
    }
}
