package js3.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;
import static js3.util.Constants.EMPTY;

public enum Functions {;

    public static boolean isNullOrEmpty(final String value) {
        return value == null || value.isEmpty();
    }

    public static List<String> toSortedList(final Set<String> set) {
        final List<String> list = new ArrayList<>(set);
        Collections.sort(list);
        return list;
    }

    public static String encodeBase64(final byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    public static String encodeUrl(final String value) {
        return value != null ? URLEncoder.encode(value, UTF_8) : EMPTY;
    }

    public static byte[] md5(final byte[] data) {
        try {
            return MessageDigest.getInstance("MD5").digest(data);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("MD5 is not available");
        }
    }

    public static byte[] newMAC(final byte[] data, final String password, final String signatureAlgorithm) {
        try {
            final Mac mac = Mac.getInstance(signatureAlgorithm);
            mac.init(new SecretKeySpec(password.getBytes(), signatureAlgorithm));
            return mac.doFinal(data);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalArgumentException("Missing MAC algorithm " + signatureAlgorithm, e);
        }
    }

}
