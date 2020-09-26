package js3.internal;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Instant;
import java.util.*;

import static java.lang.String.join;
import static js3.internal.Constants.clock;
import static js3.util.Coding.encodeHex;
import static js3.util.Crypto.hmacSha256;
import static js3.util.Crypto.sha256;
import static js3.util.HTTP.isSuccessful;
import static js3.util.Time.toIso8601;
import static js3.util.Time.toShortDate;

public final class S3Request {

    private final String region;
    private final String accessKey;
    private final String secretKey;
    private final String protocol;
    private final String host;
    private final int port;

    private String method = "GET";
    private String path;
    private String query;
    private Map<String, List<String>> headers = new HashMap<>();
    private byte[] body = new byte[0];

    public S3Request(final URI endpoint, final String region, final String accessKey, final String secretKey) {
        this.protocol = endpoint.getScheme();
        this.host = endpoint.getHost();
        this.port = endpoint.getPort();
        this.region = region;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public S3Request method(final String method) {
        this.method = method;
        return this;
    }
    public S3Request path(final String path) {
        this.path = path;
        return this;
    }
    public S3Request query(final String query) {
        this.query = query;
        return this;
    }
    public S3Request header(final String key, final String value) {
        if (value == null) return this;
        headers.put(key, Collections.singletonList(value));
        return this;
    }
    public S3Request body(final byte[] data) {
        this.body = data;
        return this;
    }

    public HttpURLConnection execute(final int connectTimeout, final int readTimeout) throws IOException {
        addSignatureHeader();
        final HttpURLConnection connection = (HttpURLConnection) newURL().openConnection();

        try {
            connection.setConnectTimeout(connectTimeout);
            connection.setReadTimeout(readTimeout);
            connection.setRequestMethod(method);
            remapHeaders(headers).forEach(connection::setRequestProperty);
            if (body != null && body.length > 0) {
                connection.setDoOutput(true);
                try (final OutputStream out = connection.getOutputStream()) {
                    out.write(body);
                }
            }

            if (!isSuccessful(connection))
                throw new IOException("Unexpected http code " + connection.getResponseCode());
        } catch (IOException e) {
            connection.disconnect();
            throw e;
        }

        return connection;
    }

    private URL newURL() throws MalformedURLException {
        final String portPart = port == -1 ? "" : ":" + port;
        final String url = protocol + "://" + host + portPart + path + (query == null ? "" : "?" + query);
        return new URL(url);
    }

    /**
     * Calculate the request signature.
     * https://docs.aws.amazon.com/general/latest/gr/sigv4-add-signature-to-request.html
     * https://docs.aws.amazon.com/AmazonS3/latest/API/sigv4-auth-using-authorization-header.html
     */
    private void addSignatureHeader() {
        final Instant now = clock.instant();
        final String date = toShortDate(now);
        final String dateTime = toIso8601(now);

        header("Host", newHostHeader());
        header("x-amz-date", dateTime);
        header("x-amz-content-sha256", encodeHex(sha256(body)));

        final String canonical = encodeHex(sha256(toCanonicalRequest()));
        final String signData = "AWS4-HMAC-SHA256" + "\n" + dateTime + "\n" + date + "/" + region + "/s3/aws4_request" + "\n" + canonical;
        final byte[] signKey = hmacSha256("aws4_request", hmacSha256("s3", hmacSha256(region, hmacSha256(date, "AWS4" + secretKey))));

        final String authHeaderContent = "AWS4-HMAC-SHA256 Credential=" + accessKey + "/" +
                date + "/" + region + "/s3/aws4_request,SignedHeaders=" + newSignedHeaders() +
                ",Signature=" + encodeHex(hmacSha256(signData, signKey));
        header("Authorization", authHeaderContent);
    }

    private Map<String, String> getCanonicalHeaders() {
        final var map = new TreeMap<String, String>();
        for (final var entry : headers.entrySet()) {
            final String value = join(";", entry.getValue()).replaceAll(" +", " ");
            map.put(entry.getKey().toLowerCase().trim(), value);
        }
        return map;
    }

    private static Map<String, String> remapHeaders(final Map<String, List<String>> headers) {
        final var map = new TreeMap<String, String>();
        for (final var header : headers.entrySet()) {
            map.put(header.getKey(), join(";", header.getValue()));
        }
        return map;
    }

    private String newHostHeader() {
        return port == -1 ? host : host + ":" + port;
    }

    private String newSignedHeaders() {
        return join(";", getCanonicalHeaders().keySet());
    }

    private String toCanonicalRequest() {
        final String queryPart = query != null ? query : "";
        final var canonicalHeaders = getCanonicalHeaders();
        return method + "\n" + path + "\n" + queryPart + "\n" +
            toCanonicalHeaderList(canonicalHeaders) + "\n" +
            join(";", canonicalHeaders.keySet()) + "\n" +
            encodeHex(sha256(body));
    }

    private static String toCanonicalHeaderList(final Map<String, String> headers) {
        final var builder = new StringBuilder();
        for (final var header : headers.entrySet()) {
            builder.append(header.getKey()).append(":").append(header.getValue()).append("\n");
        }
        return builder.toString();
    }

}
