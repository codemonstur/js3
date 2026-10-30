package js3;

import js3.util.Coding;
import js3.util.Crypto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CryptoTest {
    @Test
    void sha256KnownVector() {
        assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",
                Coding.encodeHex(Crypto.sha256("abc")));
    }

    @Test
    void hmacSha256KnownVector() {
        // RFC 4231 test case 2
        byte[] result = Crypto.hmacSha256("what do ya want for nothing?", "Jefe");
        assertEquals("5bdcc146bf60754e6a042426089575c7", Coding.encodeHex(result).substring(0, 32));
    }
}
