package js3.calls;

import js3.pojos.data.S3Object;

import java.io.IOException;

public interface GetObject extends AccessS3Config {

    default byte[] getObjectByteArray(final String bucketName, final String id, final byte[] defaultValue)
            throws IOException {
        final var object = getObject(bucketName, id, null);
        return object == null ? defaultValue : object.data;
    }

    default S3Object getObject(final String bucketName, final String id, final S3Object defaultValue)
            throws IOException {
        return newS3Request().bucket(bucketName).objectId(id).get().fetchObject(defaultValue);
    }

}
