package js3.actions;

import js3.S3ClientConfiguration;
import js3.S3Credentials;

import java.io.IOException;

public interface S3CopyObject extends S3ClientConfiguration, S3Credentials {

    default void copyS3Object(final String bucket, final String fromKey, final String toKey) throws IOException {
        copyS3Object(bucket, fromKey, bucket, toKey);
    }

    default void copyS3Object(final String fromBucket, final String fromKey, final String toBucket, final String toKey) throws IOException {
        final String pathFromKey = newS3Path(fromBucket, fromKey);
        newS3Request()
            .method("PUT").path(newS3Path(toBucket, toKey))
            .header("x-amz-copy-source", pathFromKey)
            .execute(getS3ConnectTimeout(), getS3ReadTimeout());
    }

}
