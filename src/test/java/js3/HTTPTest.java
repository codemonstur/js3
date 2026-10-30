package js3;

import js3.util.HTTP;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HTTPTest {
    @Test
    void encodeUriBasic() {
        assertEquals("a%20b", HTTP.encodeURI("a b"));
        assertEquals("a/b", HTTP.encodeURI("a/b", false));
        assertEquals("a%2Fb", HTTP.encodeURI("a/b", true));
    }

    @Test
    void encodeUriSurrogatePair() {
        String result = HTTP.encodeURI("\uD83D\uDE00");
        assertTrue(result.startsWith("%"));
    }

    @Test
    void getFirstFromCaseInsensitive() {
        Map<String, List<String>> headers = Map.of("Content-Type", List.of("text/plain"));
        assertEquals("text/plain", HTTP.getFirstFrom(headers, "content-type"));
        assertNull(HTTP.getFirstFrom(headers, "X-Missing"));
        assertNull(HTTP.getFirstFrom(Map.of("X", List.of()), "X"));
    }
}
