package js3.actions;

import js3.S3ClientConfiguration;
import js3.S3Credentials;

import java.io.IOException;

/**
 * The {@code S3RemoveBucket} interface.
 */
public interface S3RemoveBucket extends S3ClientConfiguration, S3Credentials {

    /**
     * Performs the {@code removeS3Bucket} operation.
     *
     * @param bucket the bucket
     * @throws IOException if the operation fails
     */
    default void removeS3Bucket(final String bucket) throws IOException {
        final var connection = newS3Request()
            .method("DELETE").path("/" + bucket)
            .execute(getS3ConnectTimeout(), getS3ReadTimeout());
        connection.disconnect();
    }

}
