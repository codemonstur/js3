package js3.actions;

import js3.S3ClientConfiguration;
import js3.S3Credentials;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import static java.nio.charset.StandardCharsets.UTF_8;
import static js3.util.HTTP.readFully;

public interface S3GetObject extends S3Credentials, S3ClientConfiguration {

    default InputStream getObjectDataAsInputStream(final String bucket, final String key) throws IOException {
        return new ByteArrayInputStream(getObjectData(bucket, key));
    }

    default String getObjectDataAsString(final String bucket, final String key) throws IOException {
        return new String(getObjectData(bucket, key), UTF_8);
    }

    default byte[] getObjectData(final String bucket, final String key) throws IOException {
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
