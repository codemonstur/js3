package js3.util;

import java.net.HttpURLConnection;
import java.util.*;

import static java.lang.String.join;

public enum HTTP {;

    public static String toXAmzHeaders(final HttpURLConnection conn) {
        final StringBuilder buf = new StringBuilder();

        final Map<String, List<String>> props = conn.getRequestProperties();
        for (final String k : toSortedList(props.keySet())) {
            final String lowercaseName = k.toLowerCase().trim();
            if (!lowercaseName.startsWith("x-amz-")) continue;

            final List<String> vals = props.get(k);
            if (vals.isEmpty()) continue;

            buf.append(lowercaseName).append(":").append(join(",", vals)).append("\n");
        }

        return buf.toString();
    }

    private static List<String> toSortedList(final Set<String> set) {
        final List<String> list = new ArrayList<>(set);
        Collections.sort(list);
        return list;
    }

}
