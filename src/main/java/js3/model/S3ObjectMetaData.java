package js3.model;

/**
 * The {@code S3ObjectMetaData} class.
 */
public final class S3ObjectMetaData {

    /**
     * The {@code key} field.
     */
    public final String key;
    /**
     * The {@code etag} field.
     */
    public final String etag;
    /**
     * The {@code size} field.
     */
    public final Long size;
    /**
     * The {@code lastModified} field.
     */
    public final String lastModified;
    /**
     * The {@code contentType} field.
     */
    public final String contentType;
    /**
     * The {@code serverSideEncryption} field.
     */
    public final String serverSideEncryption;

    /**
     * Creates a new instance.
     *
     * @param key the key
     * @param etag the etag
     * @param size the size
     * @param lastModified the last modified
     * @param contentType the content type
     * @param serverSideEncryption the server side encryption
     */
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
