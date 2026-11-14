package js3.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * The {@code Time} enum.
 */
public enum Time {;

    private static final DateTimeFormatter
        yyyyMMdd = DateTimeFormatter.ofPattern("yyyyMMdd"),
        iso8601 = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");

    private static final ZoneId utc = ZoneId.of("UTC");

    /**
     * Returns the result of {@code toShortDate}.
     *
     * @param instant the instant
     * @return the result
     */
    public static String toShortDate(final Instant instant) {
        return instant.atZone(utc).format(yyyyMMdd);
    }
    /**
     * Returns the result of {@code toIso8601}.
     *
     * @param instant the instant
     * @return the result
     */
    public static String toIso8601(final Instant instant) {
        return instant.atZone(utc).format(iso8601);
    }

}
