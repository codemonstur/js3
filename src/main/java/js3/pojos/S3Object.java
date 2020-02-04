package js3.pojos;

import java.util.List;
import java.util.Map;

public final class S3Object {

    public final byte[] data;
    public final Map<String, List<String>> headers;
  
    public S3Object(final byte[] data, final Map<String, List<String>> headers) {
        this.data = data;
        this.headers = headers;
    }

}
