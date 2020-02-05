package js3.calls;

import js3.pojos.data.S3Acl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface PutObject extends AccessS3Config {

    default boolean putObject(final String bucketName, final String id, final byte[] data) throws IOException {
        return newS3Request().bucket(bucketName).objectId(id).put().data(data).execute();
    }
    default boolean putObject(final String bucketName, final String id, final byte[] data, final S3Acl acl) throws IOException {
        return newS3Request().bucket(bucketName).objectId(id).acl(acl).put().data(data).execute();
    }
    default boolean putObject(final String bucketName, final String id, final byte[] data, final String acl, final Map<String, List<String>> headers) throws IOException {
        return newS3Request().bucket(bucketName).objectId(id).headers(headers).acl(acl).put().data(data).execute();
    }
    default boolean putObject(final String bucketName, final String id, final byte[] data, final Map<String, List<String>> headers) throws IOException {
        return newS3Request().bucket(bucketName).objectId(id).headers(headers).put().data(data).execute();
    }

}
