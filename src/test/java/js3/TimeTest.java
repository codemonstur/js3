package js3;

import js3.util.Time;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TimeTest {
    @Test
    void toShortDate() {
        assertEquals("20200102", Time.toShortDate(Instant.parse("2020-01-02T03:04:05Z")));
    }

    @Test
    void toIso8601() {
        assertEquals("20200102T030405Z", Time.toIso8601(Instant.parse("2020-01-02T03:04:05Z")));
    }
}
