package js3;

import js3.actions.*;

import java.net.URI;

/**
 * The {@code S3Client} interface.
 */
public interface S3Client extends S3ListBuckets, S3ListObjects, S3GetMetaData, S3GetObject, S3PutObject,
        S3MakeBucket, S3RemoveBucket, S3RemoveObject, S3CopyObject, S3ExistsBucket {

    /**
     * Returns the result of {@code newS3Client}.
     *
     * @param endpoint the endpoint
     * @param region the region
     * @param accessKey the access key
     * @param secretKey the secret key
     * @return the result
     */
    public static S3Client newS3Client(final String endpoint, final String region, final String accessKey, final String secretKey) {
        final URI endpointUri = URI.create(endpoint);
        return new S3Client() {
            public URI getS3Endpoint() {
                return endpointUri;
            }
            public String getS3Region() {
                return region;
            }
            public String getS3AccessKey() {
                return accessKey;
            }
            public String getS3SecretKey() {
                return secretKey;
            }
        };
    }

}
