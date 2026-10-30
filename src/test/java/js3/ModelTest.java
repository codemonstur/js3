package js3;

import js3.model.S3Bucket;
import js3.model.S3ObjectMetaData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ModelTest {
    @Test
    void s3Bucket() {
        var b = new S3Bucket("name", "date");
        assertEquals("name", b.name);
        assertEquals("date", b.createdDate);
    }

    @Test
    void s3ObjectMetaData() {
        var m = new S3ObjectMetaData("k", "e", 5L, "lm", "ct", "sse");
        assertEquals("k", m.key);
        assertEquals(5L, m.size);
    }
}
