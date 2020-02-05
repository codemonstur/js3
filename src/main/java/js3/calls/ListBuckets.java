package js3.calls;

import java.io.IOException;
import java.util.List;

public interface ListBuckets extends AccessS3Config {

    default List<String> listBuckets() throws IOException {
        return newS3Request().get().fetchKeys("name");
    }

}
