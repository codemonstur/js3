package js3.calls;

import java.io.IOException;

public interface RemoveBucket extends AccessS3Config {

    default boolean removeBucket(final String bucketName) throws IOException {
        return newS3Request().bucket(bucketName).delete().execute();
    }

}
