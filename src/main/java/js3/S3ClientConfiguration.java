package js3;

import static js3.internal.Constants.DEFAULT_CONNECT_TIMEOUT;
import static js3.internal.Constants.DEFAULT_READ_TIMEOUT;
import static js3.util.HTTP.encodeURI;

public interface S3ClientConfiguration {

    default int getS3ConnectTimeout() {
        return DEFAULT_CONNECT_TIMEOUT;
    }
    default int getS3ReadTimeout() {
        return DEFAULT_READ_TIMEOUT;
    }

    default String newS3Path(final String bucket, final String key) {
        return key == null ? "/" + bucket : "/" + bucket + "/" + encodeURI(key, false);
    }

}
