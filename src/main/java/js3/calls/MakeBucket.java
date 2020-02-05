package js3.calls;

import java.io.IOException;

public interface MakeBucket extends AccessS3Config {

    default boolean makeBucket(final String bucketName) throws IOException {
        return newS3Request().bucket(bucketName).put().execute();
    }

}
