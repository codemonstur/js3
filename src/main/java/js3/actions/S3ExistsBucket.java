package js3.actions;

import js3.S3ClientConfiguration;
import js3.S3Credentials;

import java.io.IOException;
import java.net.HttpURLConnection;

import static js3.util.HTTP.readFully;

public interface S3ExistsBucket extends S3ClientConfiguration, S3Credentials {

    default boolean existsS3Bucket(final String bucket) {
        try {
            final HttpURLConnection connection = newS3Request()
                .method("GET").path("/" + bucket)
                .execute(getS3ConnectTimeout(), getS3ReadTimeout());
            readFully(connection.getInputStream());
            connection.disconnect();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
