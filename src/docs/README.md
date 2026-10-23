# js3

A tiny, dependency-free client for S3-compatible object storage (AWS S3, MinIO,
Ceph RGW, ...).

The official AWS SDK for Java is roughly 10 MB with a large dependency tree. This
library is a heavily refactored descendant of [pico-s3](https://github.com/br02/pico-s3)
that implements only the core S3 API surface on top of the JDK's
`java.net.http.HttpClient` — **no runtime dependencies at all**.

## Usage

```java
S3Client s3 = newS3Client(
    "https://s3.eu-west-1.amazonaws.com",
    "eu-west-1",
    accessKey,
    secretKey);

s3.makeBucket("my-bucket");
s3.putS3Object("my-bucket", "path/hello.txt", bytes, "text/plain");
S3ObjectMetaData meta = s3.getMetaData("my-bucket", "path/hello.txt");
```

All operations are modelled as interface default methods on `S3Client`, composed
from one interface per action in `js3.actions`:

| Interface | Operations |
| --- | --- |
| `S3ListBuckets` | List buckets in the account. |
| `S3ListObjects` | List objects under a prefix. |
| `S3GetObject` / `S3PutObject` | Download / upload objects (SSE-KMS headers supported). |
| `S3GetMetaData` | HEAD-style object metadata. |
| `S3MakeBucket` / `S3RemoveBucket` | Bucket lifecycle. |
| `S3RemoveObject` / `S3CopyObject` | Object lifecycle. |
| `S3ExistsBucket` | Existence probe (added in 1.1.0). |

## Internals

- `util.Crypto` — AWS **SigV4** request signing.
- `util.Coding`, `util.Time` — Base64, URI encoding, timestamps.
- `internal.S3Request` / `S3ResponseParser` — request building and XML response
  parsing.
- `S3ClientConfiguration` — timeouts and path-style vs virtual-hosted-style
  addressing.
- `model.PutObjectOptions` — builder for cache-control, ACL, encryption options.

## Dependency

```xml
<dependency>
  <groupId>com.github.codemonstur</groupId>
  <artifactId>js3</artifactId>
  <version>1.1.0</version>
</dependency>
```

## Building

```sh
make build
make test
```

## License

MIT
