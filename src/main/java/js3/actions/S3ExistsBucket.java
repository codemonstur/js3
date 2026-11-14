package js3.actions;

import js3.S3ClientConfiguration;
import js3.S3Credentials;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * The {@code S3ExistsBucket} interface.
 */
public interface S3ExistsBucket extends S3ClientConfiguration, S3Credentials {

    /**
     * Returns the result of {@code existsS3Bucket}.
     *
     * @param bucket the bucket
     * @return whether the condition holds
     */
    default boolean existsS3Bucket(final String bucket) {
        try {
            // execute() throws for any non-successful status, including 404
            final HttpURLConnection connection = newS3Request()
                .method("GET").path("/" + bucket)
                .execute(getS3ConnectTimeout(), getS3ReadTimeout());
            connection.disconnect();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
