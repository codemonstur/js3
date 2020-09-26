package js3.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.nio.charset.StandardCharsets.UTF_8;

public enum Crypto {;

    public static byte[] hmacSha256(final String data, final byte[] key) {
        return hmacSha256(data.getBytes(UTF_8), key);
    }

    public static byte[] hmacSha256(final String data, final String key) {
        return hmacSha256(data.getBytes(UTF_8), key.getBytes(UTF_8));
    }

    public static byte[] hmacSha256(final byte[] data, final byte[] key) {
        try {
            final Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            final SecretKeySpec secret_key = new SecretKeySpec(key, "HmacSHA256");
            sha256_HMAC.init(secret_key);
            return sha256_HMAC.doFinal(data);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Platform not sane, missing sha256 or failing to construct key", e);
        }
    }

    public static byte[] sha256(final String data) {
        return sha256(data.getBytes(UTF_8));
    }
    public static byte[] sha256(final byte[] data) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(data);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Platform doesn't support SHA-256", e);
        }
    }

}
