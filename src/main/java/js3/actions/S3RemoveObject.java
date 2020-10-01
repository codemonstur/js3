package js3.actions;

import js3.S3ClientConfiguration;
import js3.S3Credentials;

import java.io.IOException;

public interface S3RemoveObject extends S3ClientConfiguration, S3Credentials {

    default void removeS3Object(final String bucket, final String key) throws IOException {
        newS3Request()
            .method("DELETE").path(newS3Path(bucket, key))
            .execute(getS3ConnectTimeout(), getS3ReadTimeout());
    }

}
