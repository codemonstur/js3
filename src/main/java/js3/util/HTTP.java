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
            if (Character.isHighSurrogate(ch) && i + 1 < input.length()
                    && Character.isLowSurrogate(input.charAt(i + 1))) {
                // surrogate pairs must be encoded as one code point, encoding each half
                // separately would produce the wrong (and colliding) bytes
                result.append(toUrlHexUTF8(ch, input.charAt(i + 1)));
                i++;
            } else if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z')
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
        return toUrlHexUTF8(("" + ch).getBytes(UTF_8));
    }

    private static String toUrlHexUTF8(final char high, final char low) {
        return toUrlHexUTF8(new String(new char[]{high, low}).getBytes(UTF_8));
    }

    private static String toUrlHexUTF8(final byte[] raw) {
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
        // getHeaderFields() preserves the casing the server sent, so lookup must be case-insensitive
        for (final var entry : headers.entrySet()) {
            if (entry.getKey() != null && entry.getKey().equalsIgnoreCase(name)) {
                final var values = entry.getValue();
                return values == null || values.isEmpty() ? null : values.get(0);
            }
        }
        return null;
    }
}
