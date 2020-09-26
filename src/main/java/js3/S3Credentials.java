package js3;

import js3.internal.S3Request;

import java.net.URI;

public interface S3Credentials {

    URI getS3Endpoint();
    String getS3Region();
    String getS3AccessKey();
    String getS3SecretKey();

    default S3Request newS3Request() {
        return new S3Request(getS3Endpoint(), getS3Region(), getS3AccessKey(), getS3SecretKey());
    }

}
