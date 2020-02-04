package js3;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public enum Functions {;

    public static String encodeBase64(final byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    public static byte[] md5(final byte[] data) {
        try {
            return MessageDigest.getInstance("MD5").digest(data);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("MD5 is not available");
        }
    }

    public static String newDateHeaderValue() {
        // all requests must include a Date header to prevent replay of
        // requests.  this format is defined by RFC 2616 in reference to
        // RFC 1123 and RFC 822
        final SimpleDateFormat format = new SimpleDateFormat( "EEE, dd MMM yyyy HH:mm:ss ", Locale.US );
        format.setTimeZone( TimeZone.getTimeZone( "GMT" ) );
        return format.format( new Date() ) + "GMT";
    }

}
