package js3;

import js3.util.Coding;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CodingTest {
    @Test
    void encodeHex() {
        assertEquals("48656c6c6f", Coding.encodeHex("Hello".getBytes()));
        assertEquals("", Coding.encodeHex(new byte[0]));
    }
}
