package js3.model;

public final class S3ObjectMetaData {

    public final String key;
    public final String etag;
    public final Long size;
    public final String lastModified;
    public final String contentType;
    public final String serverSideEncryption;

    public S3ObjectMetaData(final String key, final String etag, final Long size, final String lastModified,
                            final String contentType, final String serverSideEncryption) {
        this.key = key;
        this.etag = etag;
        this.size = size;
        this.lastModified = lastModified;
        this.contentType = contentType;
        this.serverSideEncryption = serverSideEncryption;
    }

}
