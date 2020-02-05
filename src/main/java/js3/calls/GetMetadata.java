package js3.calls;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface GetMetadata extends AccessS3Config {

    default Map<String, List<String>> getMetadata(final String bucketName, final String key
            , final Map<String, List<String>> defaultValue) throws IOException {
        return newS3Request().bucket(bucketName).objectId(key).head().fetchMetadata(defaultValue);
    }

}
