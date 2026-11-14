package js3.actions;

import js3.S3ClientConfiguration;
import js3.S3Credentials;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import static java.nio.charset.StandardCharsets.UTF_8;
import static js3.util.HTTP.readFully;

/**
 * The {@code S3GetObject} interface.
 */
public interface S3GetObject extends S3Credentials, S3ClientConfiguration {

    /**
     * Returns the result of {@code getS3ObjectDataAsInputStream}.
     *
     * @param bucket the bucket
     * @param key the key
     * @return the result
     * @throws IOException if the operation fails
     */
    default InputStream getS3ObjectDataAsInputStream(final String bucket, final String key) throws IOException {
        return new ByteArrayInputStream(getS3ObjectData(bucket, key));
    }

    /**
     * Returns the result of {@code getS3ObjectDataAsString}.
     *
     * @param bucket the bucket
     * @param key the key
     * @return the result
     * @throws IOException if the operation fails
     */
    default String getS3ObjectDataAsString(final String bucket, final String key) throws IOException {
        return new String(getS3ObjectData(bucket, key), UTF_8);
    }

    /**
     * Returns the result of {@code getS3ObjectData}.
     *
     * @param bucket the bucket
     * @param key the key
     * @return the result
     * @throws IOException if the operation fails
     */
    default byte[] getS3ObjectData(final String bucket, final String key) throws IOException {
        final HttpURLConnection connection = newS3Request()
            .method("GET").path(newS3Path(bucket, key))
            .execute(getS3ConnectTimeout(), getS3ReadTimeout());

        try {
            return readFully(connection.getInputStream());
        } finally {
            connection.disconnect();
        }
    }

}
