package js3.model;

/**
 * The {@code S3Bucket} class.
 */
public final class S3Bucket {

    /**
     * The {@code name} field.
     */
    public final String name;
    /**
     * The {@code createdDate} field.
     */
    public final String createdDate;

    /**
     * Creates a new instance.
     *
     * @param name the name
     * @param createdDate the created date
     */
    public S3Bucket(final String name, final String createdDate) {
        this.name = name;
        this.createdDate = createdDate;
    }

}
