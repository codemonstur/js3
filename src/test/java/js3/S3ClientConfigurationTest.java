package js3;

import js3.S3ClientConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class S3ClientConfigurationTest {
    @Test
    void newS3Path() {
        var cfg = new S3ClientConfiguration() {};
        assertEquals("/bucket", cfg.newS3Path("bucket", null));
        assertEquals("/bucket/key", cfg.newS3Path("bucket", "key"));
    }
}
