package manual;

import js3.S3Client;
import js3.model.S3ObjectMetaData;

import java.io.IOException;
import java.util.List;

import static js3.S3Client.newS3Client;

public enum TryPicoS3 {;

    public static void main(final String... args) throws IOException {
        final S3Client s3Client = newS3Client("http://localhost:9000", "minio", "minioadmin", "minioadmin");
//        listBuckets(s3Client);
//        listObjects(s3Client, "test");

//        s3Client.makeBucket("boe");
//        s3Client.removeBucket("boe");
        s3Client.copyObject("test", "test.html", "test2.html");
//        S3ObjectMetaData test = s3Client.getMetaData("test", "test.html");
//        System.out.println(test.contentType);
//        System.out.println(test.etag);
//        System.out.println(test.key);
//        System.out.println(test.lastModified);
//        System.out.println(test.serverSideEncryption);
//        System.out.println(test.size);
//        byte[] file = s3Client.getObjectData("test", "test.html");
//        s3Client.putObject("test", "test2.html", file, "text/html");
    }

    private static void listBuckets(final S3Client pClient) throws IOException {
        final List<String> buckets = pClient.listBuckets();
        for (final String bucket : buckets) {
            System.out.println(bucket);
        }
    }

    private static void listObjects(final S3Client pClient, final String bucketName) throws IOException {
        final List<S3ObjectMetaData> downloads = pClient.listObjects(bucketName);
        for (final S3ObjectMetaData item : downloads) {
            System.out.println(item.key);
        }
    }

}
