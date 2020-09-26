package js3.actions;

import js3.S3ClientConfiguration;
import js3.S3Credentials;

import java.io.IOException;

public interface S3MakeBucket extends S3ClientConfiguration, S3Credentials {

    default void makeBucket(final String bucket) throws IOException {
        newS3Request()
            .method("PUT").path("/" + bucket)
            .execute(getS3ConnectTimeout(), getS3ReadTimeout());
    }

}
