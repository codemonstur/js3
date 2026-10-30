package js3;

import js3.model.PutObjectOptions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PutObjectOptionsTest {
    @Test
    void defaults() {
        var opts = new PutObjectOptions.Builder().build();
        assertEquals("application/octet-stream", opts.getContentType());
        assertNull(opts.getServerSideEncryption());
    }

    @Test
    void builderSetsFields() {
        var opts = new PutObjectOptions.Builder()
                .withContentType("text/plain")
                .withServerSideEncryption(PutObjectOptions.Builder.SERVER_SIDE_ENCRYPTION_S3)
                .build();
        assertEquals("text/plain", opts.getContentType());
        assertEquals("AES256", opts.getServerSideEncryption());
    }

    @Test
    void kmsKeyId() {
        var opts = new PutObjectOptions.Builder()
                .withServerSideEncryption(PutObjectOptions.Builder.SERVER_SIDE_ENCRYPTION_KMS)
                .withServerSideEncryptionKeyId("key-1")
                .build();
        assertEquals("aws:kms", opts.getServerSideEncryption());
        assertEquals("key-1", opts.getServerSideEncryptionKeyId());
    }
}
