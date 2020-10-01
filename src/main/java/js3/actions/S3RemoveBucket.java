package js3.actions;

import js3.S3ClientConfiguration;
import js3.S3Credentials;

import java.io.IOException;

public interface S3RemoveBucket extends S3ClientConfiguration, S3Credentials {

    default void removeS3Bucket(final String bucket) throws IOException {
        newS3Request()
            .method("DELETE").path("/" + bucket)
            .execute(getS3ConnectTimeout(), getS3ReadTimeout());
    }

}
