package js3.actions;

import js3.S3ClientConfiguration;
import js3.S3Credentials;

import java.io.IOException;

/**
 * The {@code S3RemoveObject} interface.
 */
public interface S3RemoveObject extends S3ClientConfiguration, S3Credentials {

    /**
     * Performs the {@code removeS3Object} operation.
     *
     * @param bucket the bucket
     * @param key the key
     * @throws IOException if the operation fails
     */
    default void removeS3Object(final String bucket, final String key) throws IOException {
        final var connection = newS3Request()
            .method("DELETE").path(newS3Path(bucket, key))
            .execute(getS3ConnectTimeout(), getS3ReadTimeout());
        connection.disconnect();
    }

}
