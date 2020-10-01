package js3.actions;

import js3.S3ClientConfiguration;
import js3.S3Credentials;
import js3.model.S3ObjectMetaData;

import java.io.IOException;
import java.net.HttpURLConnection;

import static js3.util.HTTP.getFirstFrom;

public interface S3GetMetaData extends S3ClientConfiguration, S3Credentials {

    default S3ObjectMetaData getS3ObjectMetaData(final String bucket, final String key) throws IOException {
        final HttpURLConnection connection = newS3Request()
            .method("HEAD").path(newS3Path(bucket, key))
            .execute(getS3ConnectTimeout(), getS3ReadTimeout());

        try {
            final var headers = connection.getHeaderFields();

            final String etag = getFirstFrom(headers, "ETag");
            final Long size = Long.valueOf(getFirstFrom(headers, "Content-Length"));
            final String lastModified = getFirstFrom(headers, "Last-Modified");
            final String contentType = getFirstFrom(headers, "Content-Type");
            final String serverSideEncryption = getFirstFrom(headers, "x-amz-server-side-encryption");

            return new S3ObjectMetaData(key, etag, size, lastModified, contentType, serverSideEncryption);
        } finally {
            connection.disconnect();
        }
    }

}
