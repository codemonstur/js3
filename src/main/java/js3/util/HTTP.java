package js3.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static js3.internal.Constants.BLOCK_SIZE;

public enum HTTP {;

    public static String encodeURI(final CharSequence input) {
        return encodeURI(input, true);
    }

    public static String encodeURI(final CharSequence input, final boolean encodeSlash) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z')
                    || (ch >= '0' && ch <= '9') || ch == '_' || ch == '-' || ch == '~' || ch == '.') {
                result.append(ch);
            } else if (ch == '/') {
                result.append(encodeSlash ? "%2F" : ch);
            } else {
                result.append(toUrlHexUTF8(ch));
            }
        }
        return result.toString();
    }

    private static String toUrlHexUTF8(final char ch) {
        final byte[] raw = ("" + ch).getBytes(UTF_8);
        final StringBuilder hexString = new StringBuilder();
        for (final byte rawByte : raw) {
            hexString.append("%").append(format("%02X", rawByte & 0XFF));
        }
        return hexString.toString().toUpperCase();
    }

    public static boolean isSuccessful(final HttpURLConnection connection) throws IOException {
        final int responseCode = connection.getResponseCode();
        return responseCode >= 200 && responseCode <= 299;
    }

    public static byte[] readFully(final InputStream input) throws IOException {
        final byte[] buffer = new byte[BLOCK_SIZE];

        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            int read; while ((read = input.read(buffer)) != -1) {
                baos.write(buffer, 0 , read);
            }
            return baos.toByteArray();
        }
    }

    public static String getFirstFrom(final Map<String, List<String>> headers, final String name) {
        return headers.containsKey(name) ? headers.get(name).get(0) : null;
    }
}
