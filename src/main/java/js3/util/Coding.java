package js3.util;

public enum Coding {;

    public static String encodeHex(final byte[] data) {
        final StringBuilder hexString = new StringBuilder();
        for (final byte rawByte : data) {
            hexString.append(String.format("%02x", rawByte & 0XFF));
        }
        return hexString.toString().toLowerCase();
    }

}
