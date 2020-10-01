package js3.actions;

import js3.S3ClientConfiguration;
import js3.S3Credentials;
import js3.internal.S3ResponseParser;
import org.w3c.dom.Document;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import static js3.internal.S3ResponseParser.getNextContinuationToken;
import static js3.internal.S3ResponseParser.toXmlDocument;
import static js3.util.HTTP.encodeURI;
import static js3.util.HTTP.readFully;

public interface S3ListBuckets extends S3ClientConfiguration, S3Credentials {

    default List<String> listS3Buckets() throws IOException {
        final var bucketNames = new ArrayList<String>();

        String cursor = null; do {
            final HttpURLConnection connection = newS3Request()
                .method("GET").path("/").query(newRequestParams(cursor, "list-type=2"))
                .execute(getS3ConnectTimeout(), getS3ReadTimeout());
            try {
                final Document s3ListingDocument = toXmlDocument(readFully(connection.getInputStream()));

                bucketNames.addAll(S3ResponseParser.toBucketsList(s3ListingDocument));

                cursor = getNextContinuationToken(s3ListingDocument);
            } finally {
                connection.disconnect();
            }
        } while (cursor != null);

        return bucketNames;
    }

    private String newRequestParams(final String continuation, final String fixedParams) {
        return continuation == null ? fixedParams : "continuation-token=" + encodeURI(continuation) + "&" + fixedParams;
    }

}
