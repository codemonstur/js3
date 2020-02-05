package js3.calls;

import java.io.IOException;

public interface CopyObject extends AccessS3Config {

    default boolean copyObject(final String bucketName, final String fromId, final String toId, final String acl) throws IOException {
        final String fullDst = String.format("%s/%s", bucketName, fromId);
        return newS3Request().bucket(bucketName).objectId(toId).put().acl(acl).header("x-amz-copy-source", fullDst).execute();
    }

}
