package js3.calls;

import java.io.IOException;
import java.util.List;

public interface ListObjects extends AccessS3Config {

    default List<String> listObjects(final String bucketName) throws IOException {
        return listObjects(bucketName, null, null, 0);
    }

    default List<String> listObjects(final String bucketName, final String prefix) throws IOException {
        return listObjects(bucketName, prefix, null, 0);
    }

    default List<String> listObjects(final String bucketName, final String prefix, final String marker) throws IOException {
        return listObjects(bucketName, prefix, marker, 0);
    }

    default List<String> listObjects(final String bucketName, final String prefix, final String marker, final int max) throws IOException {
        return newS3Request().bucket(bucketName).query(prefix, marker, max).get().fetchKeys("key");
    }

}
