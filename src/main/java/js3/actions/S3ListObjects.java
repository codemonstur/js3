package js3.actions;

import js3.S3ClientConfiguration;
import js3.S3Credentials;
import js3.model.S3ObjectMetaData;
import org.w3c.dom.Document;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import static js3.internal.S3ResponseParser.*;
import static js3.util.HTTP.encodeURI;
import static js3.util.HTTP.readFully;

public interface S3ListObjects extends S3ClientConfiguration, S3Credentials {

    default List<S3ObjectMetaData> listS3Objects(final String bucket) throws IOException {
        return internalListObjects(bucket, "");
    }

    default List<S3ObjectMetaData> listS3Objects(final String bucket, final String prefix) throws IOException {
        return internalListObjects(bucket, "&prefix="+encodeURI(prefix, true));
    }

    private List<S3ObjectMetaData> internalListObjects(final String bucket, final String prefixParam) throws IOException {
        final var objects = new ArrayList<S3ObjectMetaData>();

        String cursor = null; do {
            final String continuationParam = cursor == null ? "" : "continuation-token="+encodeURI(cursor) + "&";

            final HttpURLConnection connection = newS3Request()
                .method("GET").path("/" + bucket).query(continuationParam + "list-type=2" + prefixParam)
                .execute(getS3ConnectTimeout(), getS3ReadTimeout());
            try {
                final Document s3ListingDocument = toXmlDocument(readFully(connection.getInputStream()));

                objects.addAll(toObjectsList(s3ListingDocument));

                cursor = getNextContinuationToken(s3ListingDocument);
            } finally {
                connection.disconnect();
            }
        } while (cursor != null);

        return objects;
    }

}
