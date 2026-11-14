package js3;

import js3.internal.S3Request;

import java.net.URI;

/**
 * The {@code S3Credentials} interface.
 */
public interface S3Credentials {

    /**
     * Returns the s3 endpoint.
     *
     * @return the result
     */
    URI getS3Endpoint();
    /**
     * Returns the s3 region.
     *
     * @return the result
     */
    String getS3Region();
    /**
     * Returns the s3 access key.
     *
     * @return the result
     */
    String getS3AccessKey();
    /**
     * Returns the s3 secret key.
     *
     * @return the result
     */
    String getS3SecretKey();

    /**
     * Returns the result of {@code newS3Request}.
     *
     * @return the result
     */
    default S3Request newS3Request() {
        return new S3Request(getS3Endpoint(), getS3Region(), getS3AccessKey(), getS3SecretKey());
    }

}
