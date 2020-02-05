package unit;

import js3.S3Client;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;

import java.time.Duration;

public class TestListBuckets {

    private static final String ADMIN_ACCESS_KEY = "admin";
    private static final String ADMIN_SECRET_KEY = "12345678";

    private S3Client store;

    @Rule
    public GenericContainer minioServer = new GenericContainer<>("minio/minio")
        .withEnv("MINIO_ACCESS_KEY", ADMIN_ACCESS_KEY)
        .withEnv("MINIO_SECRET_KEY", ADMIN_SECRET_KEY)
        .withCommand("server /data")
        .withExposedPorts(9000)
        .waitingFor(new HttpWaitStrategy()
            .forPath("/minio/health/ready")
            .forPort(9000)
            .withStartupTimeout(Duration.ofSeconds(10)));

    @Before
    public void setUp() {
        this.store = new S3Client(minioServer.getContainerIpAddress(), minioServer.getFirstMappedPort(), ADMIN_ACCESS_KEY, ADMIN_SECRET_KEY);
    }

    @Test
    public void canCreateBucketWithAdminUser() throws Exception {
//        final MinioClient minioClient = new MinioClient("http://"+minioServer.getContainerIpAddress(), minioServer.getFirstMappedPort(), ADMIN_ACCESS_KEY, ADMIN_SECRET_KEY);
//        minioClient.makeBucket("foo");
//        System.out.println(minioClient.listBuckets());

        store.makeBucket("foo");
        System.out.println(store.listBuckets());

    }

}
