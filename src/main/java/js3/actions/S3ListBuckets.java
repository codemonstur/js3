package js3.actions;

import js3.S3ClientConfiguration;
import js3.S3Credentials;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

import static js3.internal.S3ResponseParser.toBucketsList;
import static js3.internal.S3ResponseParser.toXmlDocument;
import static js3.util.HTTP.readFully;

public interface S3ListBuckets extends S3ClientConfiguration, S3Credentials {

    // ListAllMyBuckets has no pagination: the service returns every bucket in one response
    default List<String> listS3Buckets() throws IOException {
        final HttpURLConnection connection = newS3Request()
            .method("GET").path("/")
            .execute(getS3ConnectTimeout(), getS3ReadTimeout());
        try {
            return toBucketsList(toXmlDocument(readFully(connection.getInputStream())));
        } finally {
            connection.disconnect();
        }
    }

}
