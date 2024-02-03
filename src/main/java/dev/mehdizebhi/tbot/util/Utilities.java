package dev.mehdizebhi.tbot.util;

import javax.annotation.Nonnull;

public class Utilities {

    @Nonnull
    public static <T> T ensureNonNull(T object) {
        if (object != null) {
            return object;
        }

        throw new NullPointerException();
    }
}
