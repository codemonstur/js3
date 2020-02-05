package js3.calls;

import js3.S3RequestBuilder;
import js3.pojos.data.S3Config;

import java.net.http.HttpClient;

public interface AccessS3Config {

    S3Config getS3Config();
    HttpClient getHttpClient();

    default S3RequestBuilder newS3Request() {
        return new S3RequestBuilder(getS3Config(), getHttpClient());
    }

}
