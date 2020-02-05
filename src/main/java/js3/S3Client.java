package js3;

import js3.calls.*;
import js3.pojos.data.S3Config;

import java.net.http.HttpClient;

import static java.net.http.HttpClient.newHttpClient;

public final class S3Client implements CopyObject, MakeBucket, RemoveBucket, RemoveObject, GetObject,
        GetMetadata, ListBuckets, ListObjects, PutObject {

    private final S3Config config;
    private final HttpClient httpClient;
    public S3Client(final String host, final int port, final String username, final String password) {
        this.config = new S3Config(host, port, username, password);
        this.httpClient = newHttpClient();
    }

    public S3Config getS3Config() {
        return config;
    }
    public HttpClient getHttpClient() {
        return httpClient;
    }

}

