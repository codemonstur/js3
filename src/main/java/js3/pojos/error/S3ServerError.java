package js3.pojos.error;

import java.io.IOException;

public final class S3ServerError extends IOException {
    public S3ServerError(final String message) {
        super(message);
    }
}
