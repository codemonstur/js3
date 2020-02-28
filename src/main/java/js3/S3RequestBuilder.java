package js3;

import js3.pojos.data.S3Acl;
import js3.pojos.data.S3Config;
import js3.pojos.data.S3Object;
import js3.pojos.error.S3ClientError;
import js3.pojos.error.S3ServerError;
import xmlparser.XmlStreamReader;
import xmlparser.utils.Trimming.NativeTrimmer;
import xmlparser.utils.Trimming.Trim;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.join;
import static java.net.http.HttpRequest.BodyPublishers.noBody;
import static java.net.http.HttpRequest.BodyPublishers.ofByteArray;
import static java.net.http.HttpResponse.BodyHandlers.ofByteArray;
import static js3.util.Functions.*;

public final class S3RequestBuilder {

    private final HttpClient http;
    private final S3Config config;
    private String method;
    private String bucketName;
    private String objectId;
    private String queryParams = "";
    private Map<String, List<String>> headers = new HashMap<>();
    private byte[] data;

    private HttpResponse<byte[]> response;

    public S3RequestBuilder(final S3Config config, final HttpClient http) {
        this.config = config;
        this.http = http;
    }

    public S3RequestBuilder bucket(final String bucketName) {
        this.bucketName = bucketName;
        return this;
    }
    public S3RequestBuilder objectId(final String objectId) {
        this.objectId = objectId;
        return this;
    }
    public S3RequestBuilder query(final String prefix, final String marker, final int max) {
        final var params = new ArrayList<String>();
        if (!isNullOrEmpty(prefix)) params.add("prefix="+encodeUrl(prefix));
        if (!isNullOrEmpty(marker)) params.add("marker="+encodeUrl(marker));
        if (max != 0) params.add("max-keys="+max);
        this.queryParams = params.isEmpty() ? "" : "?" + String.join("&", params);
        return this;
    }

    public S3RequestBuilder header(final String name, final String value) {
        if (isNullOrEmpty(name) || isNullOrEmpty(value)) return this;
        this.headers.computeIfAbsent(name, s -> new ArrayList<>()).add(value);
        return this;
    }
    public S3RequestBuilder headers(final Map<String, List<String>> headers) {
        if (headers == null) return this;
        for (final var entry : headers.entrySet()) {
            this.headers.computeIfAbsent(entry.getKey(), s -> new ArrayList<>()).addAll(entry.getValue());
        }
        return this;
    }
    public S3RequestBuilder acl(final String acl) {
        return header("x-amz-acl", acl);
    }
    public S3RequestBuilder acl(final S3Acl acl) {
        if (acl == null) return this;
        return header("x-amz-acl", acl.toString());
    }

    public S3RequestBuilder head() {
        this.method = "HEAD";
        return this;
    }
    public S3RequestBuilder delete() {
        this.method = "DELETE";
        return this;
    }
    public S3RequestBuilder get() {
        this.method = "GET";
        return this;
    }
    public S3RequestBuilder put() {
        this.method = "PUT";
        return this;
    }
    public S3RequestBuilder data(final byte[] data) {
        this.data = data;
        return this;
    }

    public boolean execute() throws IOException {
        addAuthorization();

        final HttpRequest.Builder request = HttpRequest
            .newBuilder()
            .method(method, data == null ? noBody() : ofByteArray(data))
            .uri(URI.create(config.endpoint + newPath() + queryParams));
        for (final var entry : headers.entrySet()) {
            for (final var item : entry.getValue()) {
                request.header(entry.getKey(), item);
            }
        }

        try {
            this.response = http.send(request.build(), ofByteArray());
        } catch (InterruptedException e) {
            throw new IOException("Interrupted while executing request", e);
        }

        final int statusCode = response.statusCode();
        if (statusCode == 404) return false;
        if (statusCode >= 400 && statusCode <= 499)
            throw new S3ClientError("S3 service returned client error code " + statusCode);
        if (statusCode >= 500 && statusCode <= 599)
            throw new S3ServerError("S3 service returned error code " + statusCode);
        if (statusCode < 200 || statusCode > 299) {
            throw new S3ServerError("S3 service returned unexpected status code " + statusCode);
        }

        return true;
    }

    private static final Trim trimmer = new NativeTrimmer();
    public List<String> fetchKeys(final String tagname) throws IOException {
        if (response == null) execute();

        final S3ResponseParser parser = new S3ResponseParser(tagname);
        try (final InputStreamReader responseData = new InputStreamReader(new ByteArrayInputStream(response.body()))) {
            XmlStreamReader.toXmlStream(responseData, parser, trimmer);
        }
        return parser.getList();
    }

    private String newPath() {
        String path = "/";
        if (!isNullOrEmpty(bucketName)) {
            path += encodeUrl(bucketName);
            if (!isNullOrEmpty(objectId)) {
                path += "/" + encodeUrl(objectId);
            }
        }
        return path;
    }

    private static final String SIGNATURE_ALGORITHM = "HmacSHA1";
    // Standard Date header, must make myself so I can use it in the Authorization header
    // https://tools.ietf.org/html/rfc2616#section-3.3.1
    private static final DateTimeFormatter RFC_2616 = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss O");

    private void addAuthorization() {
        final List<String> headerValues = headers.get("Content-Type");
        final String contentType = headerValues == null ? "" : headerValues.get(0);
        final String contentMD5 = data != null ? encodeBase64(md5(data)) : "";
        final String date = RFC_2616.format(ZonedDateTime.now());
        final String headers = extractS3CustomHeaders();

        final String reducedRequest = method + "\n" + contentMD5 + "\n" + contentType + "\n" + date + "\n" + headers + newPath();
        final String auth = encodeBase64(newMAC(reducedRequest.getBytes(), config.password, SIGNATURE_ALGORITHM));

        header("Date", date);
        header("Authorization", "AWS " + config.username + ":" + auth);
        if (data != null) header("Content-MD5", contentMD5);
    }

    private String extractS3CustomHeaders() {
        final StringBuilder buf = new StringBuilder();

        for (final String k : toSortedList(headers.keySet())) {
            final String lowercaseName = k.toLowerCase().trim();
            if (!lowercaseName.startsWith("x-amz-")) continue;

            final List<String> list = headers.get(k);
            if (list.isEmpty()) continue;

            buf.append(lowercaseName).append(":").append(join(",", list)).append("\n");
        }

        return buf.toString();
    }

    public S3Object fetchObject(final S3Object defaultValue) throws IOException {
        if (response == null && !execute()) return defaultValue;
        return new S3Object(response.body(), response.headers().map());
    }

    public Map<String, List<String>> fetchMetadata(final Map<String, List<String>> defaultValue) throws IOException {
        if (response == null && !execute()) return defaultValue;
        return response.headers().map();
    }

}
