package js3.pojos.data;

public interface S3Acl {

    public static S3Acl newS3Acl(final String name) {
        return new S3Acl() {
            public String toString() {
                return name;
            }
        };
    }

    // There are more, don't feel like listing them all, user can create more if they like
    // https://docs.aws.amazon.com/AmazonS3/latest/dev/acl-overview.html
    public static final S3Acl
        PUBLIC_READ = newS3Acl("public-read"),
        PUBLIC_WRITE = newS3Acl("public-write"),
        AUTHENTICATED_READ = newS3Acl("authenticated-read"),
        PRIVATE = newS3Acl("private");

}



