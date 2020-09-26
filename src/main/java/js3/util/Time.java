package js3.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public enum Time {;

    private static final DateTimeFormatter
        yyyyMMdd = DateTimeFormatter.ofPattern("yyyyMMdd"),
        iso8601 = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");

    private static final ZoneId utc = ZoneId.of("UTC");

    public static String toShortDate(final Instant instant) {
        return instant.atZone(utc).format(yyyyMMdd);
    }
    public static String toIso8601(final Instant instant) {
        return instant.atZone(utc).format(iso8601);
    }

}
