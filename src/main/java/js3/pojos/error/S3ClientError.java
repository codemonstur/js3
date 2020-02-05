package js3.pojos.error;

public final class S3ClientError extends RuntimeException {
    public S3ClientError(final String message) {
        super(message);
    }
}
