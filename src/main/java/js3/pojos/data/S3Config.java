package js3.pojos.data;

public final class S3Config {

    public final String endpoint;
    public final String username;
    public final String password;

    public S3Config(final String host, final int port, final String username, final String password) {
        this.endpoint = "http://" + host + ":" + port;
        this.username = username;
        this.password = password;
    }

}
