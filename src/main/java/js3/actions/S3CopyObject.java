package js3.actions;

import js3.S3ClientConfiguration;
import js3.S3Credentials;

import java.io.IOException;

/**
 * The {@code S3CopyObject} interface.
 */
public interface S3CopyObject extends S3ClientConfiguration, S3Credentials {

    /**
     * Performs the {@code copyS3Object} operation.
     *
     * @param bucket the bucket
     * @param fromKey the from key
     * @param toKey the to key
     * @throws IOException if the operation fails
     */
    default void copyS3Object(final String bucket, final String fromKey, final String toKey) throws IOException {
        copyS3Object(bucket, fromKey, bucket, toKey);
    }

    /**
     * Performs the {@code copyS3Object} operation.
     *
     * @param fromBucket the from bucket
     * @param fromKey the from key
     * @param toBucket the to bucket
     * @param toKey the to key
     * @throws IOException if the operation fails
     */
    default void copyS3Object(final String fromBucket, final String fromKey, final String toBucket, final String toKey) throws IOException {
        final String pathFromKey = newS3Path(fromBucket, fromKey);
        final var connection = newS3Request()
            .method("PUT").path(newS3Path(toBucket, toKey))
            .header("x-amz-copy-source", pathFromKey)
            .execute(getS3ConnectTimeout(), getS3ReadTimeout());
        connection.disconnect();
    }

}
