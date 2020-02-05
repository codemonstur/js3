package js3.calls;

import java.io.IOException;

public interface RemoveObject extends AccessS3Config {

    default boolean removeObject(final String bucketName, final String id) throws IOException {
        return newS3Request().bucket(bucketName).objectId(id).delete().execute();
    }

}
