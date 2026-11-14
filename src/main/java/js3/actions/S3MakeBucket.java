package js3.actions;

import js3.S3ClientConfiguration;
import js3.S3Credentials;

import java.io.IOException;

/**
 * The {@code S3MakeBucket} interface.
 */
public interface S3MakeBucket extends S3ClientConfiguration, S3Credentials {

    /**
     * Performs the {@code makeS3Bucket} operation.
     *
     * @param bucket the bucket
     * @throws IOException if the operation fails
     */
    default void makeS3Bucket(final String bucket) throws IOException {
        final var connection = newS3Request()
            .method("PUT").path("/" + bucket)
            .execute(getS3ConnectTimeout(), getS3ReadTimeout());
        connection.disconnect();
    }

}
