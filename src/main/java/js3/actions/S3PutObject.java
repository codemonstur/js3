package js3.actions;

import js3.S3ClientConfiguration;
import js3.S3Credentials;
import js3.model.PutObjectOptions;

import java.io.IOException;

/**
 * The {@code S3PutObject} interface.
 */
public interface S3PutObject extends S3ClientConfiguration, S3Credentials {

    /**
     * Performs the {@code putS3Object} operation.
     *
     * @param bucket the bucket
     * @param key the key
     * @param data the data
     * @param contentType the content type
     * @throws IOException if the operation fails
     */
    default void putS3Object(final String bucket, final String key, final byte[] data, final String contentType)
            throws IOException {
        putS3Object(bucket, key, data, new PutObjectOptions.Builder().withContentType(contentType).build());
    }

    /**
     * Performs the {@code putS3Object} operation.
     *
     * @param bucket the bucket
     * @param key the key
     * @param data the data
     * @param putObjectOptions the put object options
     * @throws IOException if the operation fails
     */
    default void putS3Object(final String bucket, final String key, final byte[] data,
                           final PutObjectOptions putObjectOptions) throws IOException {
        newS3Request()
            .method("PUT").path(newS3Path(bucket, key))
            .header("Content-Type", putObjectOptions.getContentType())
            .header("Content-Length", String.valueOf(data.length))
            .header("x-amz-server-side-encryption", putObjectOptions.getServerSideEncryption())
            .header("x-amz-server-side-encryption-aws-kms-key-id", putObjectOptions.getServerSideEncryptionKeyId())
            .body(data)
            .execute(getS3ConnectTimeout(), getS3ReadTimeout()).disconnect();
    }

}
