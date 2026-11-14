package js3;

import static js3.internal.Constants.DEFAULT_CONNECT_TIMEOUT;
import static js3.internal.Constants.DEFAULT_READ_TIMEOUT;
import static js3.util.HTTP.encodeURI;

/**
 * The {@code S3ClientConfiguration} interface.
 */
public interface S3ClientConfiguration {

    /**
     * Returns the s3 connect timeout.
     *
     * @return the result
     */
    default int getS3ConnectTimeout() {
        return DEFAULT_CONNECT_TIMEOUT;
    }
    /**
     * Returns the s3 read timeout.
     *
     * @return the result
     */
    default int getS3ReadTimeout() {
        return DEFAULT_READ_TIMEOUT;
    }

    /**
     * Returns the result of {@code newS3Path}.
     *
     * @param bucket the bucket
     * @param key the key
     * @return the result
     */
    default String newS3Path(final String bucket, final String key) {
        return key == null ? "/" + bucket : "/" + bucket + "/" + encodeURI(key, false);
    }

}
