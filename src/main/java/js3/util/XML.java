package js3.util;

import js3.pojos.error.InvalidXml;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static js3.util.Constants.*;
import static js3.util.IO.*;

public enum XML {;

    public interface EventParser {
        void startNode(final String name, final Map<String, String> attrs);
        void endNode();
        void someText(final String txt);
    }

    public static void toXmlStream(final InputStreamReader in, final EventParser parser) throws IOException {
        String str; while ((str = readLine(in, XML_TAG_START)) != null) {
            if (!str.isEmpty()) parser.someText(unescapeXml(str.trim()));

            str = trim(readLine(in, XML_TAG_END));
            if (str.isEmpty()) throw new InvalidXml("Unclosed tag");
            if (str.startsWith(XML_START_COMMENT)) {
                if (str.endsWith(XML_END_COMMENT))
                    continue;
                readUntil(in, XML_END_COMMENT+">");
                continue;
            }

            if (str.charAt(0) == XML_PROLOG) continue;
            if (str.charAt(0) == XML_SELF_CLOSING) parser.endNode();
            else {
                final String name = getNameOfTag(str);
                if (str.length() == name.length()) {
                    parser.startNode(str, new HashMap<>());
                    continue;
                }

                final int beginAttr = name.length();
                final int end = str.length();
                if (str.endsWith(FORWARD_SLASH)) {
                    parser.startNode(name, xmlToAttributes(str.substring(beginAttr, end-1)));
                    parser.endNode();
                } else {
                    parser.startNode(name, xmlToAttributes(str.substring(beginAttr+1, end)));
                }
            }
        }
    }

    private static HashMap<String, String> xmlToAttributes(String input) {
        final HashMap<String, String> attributes = new HashMap<>();

        while (!input.isEmpty()) {
            int startName = indexOfNonWhitespaceChar(input, 0);
            if (startName == -1) break;
            int equals = input.indexOf(CHAR_EQUALS, startName+1);
            if (equals == -1) break;

            final String name = input.substring(startName, equals).trim();
            input = input.substring(equals+1);

            int startValue = indexOfNonWhitespaceChar(input, 0);
            if (startValue == -1) break;

            int endValue; final String value;
            if (input.charAt(startValue) == CHAR_DOUBLE_QUOTE) {
                startValue++;
                endValue = input.indexOf(CHAR_DOUBLE_QUOTE, startValue);
                if (endValue == -1) endValue = input.length()-1;
                value = input.substring(startValue, endValue).trim();
            } else {
                endValue = indexOfWhitespaceChar(input, startValue+1);
                if (endValue == -1) endValue = input.length()-1;
                value = input.substring(startValue, endValue+1).trim();
            }

            input = input.substring(endValue+1);

            attributes.put(name, unescapeXml(value));
        }

        return attributes;
    }

    private static String getNameOfTag(final String tag) {
        int offset = 0;
        for (; offset < tag.length(); offset++) {
            if (tag.charAt(offset) == CHAR_SPACE || tag.charAt(offset) == CHAR_FORWARD_SLASH)
                break;
        }
        return tag.substring(0, offset);
    }

    private static String unescapeXml(final String text) {
        StringBuilder result = new StringBuilder(text.length());
        int i = 0;
        int n = text.length();
        while (i < n) {
            char charAt = text.charAt(i);
            if (charAt != CHAR_AMPERSAND) {
                result.append(charAt);
                i++;
            } else {
                if (text.startsWith(ENCODED_AMPERSAND, i)) {
                    result.append(CHAR_AMPERSAND);
                    i += 5;
                } else if (text.startsWith(ENCODED_SINGLE_QUOTE, i)) {
                    result.append(CHAR_SINGLE_QUOTE);
                    i += 6;
                } else if (text.startsWith(ENCODED_DOUBLE_QUOTE, i)) {
                    result.append(CHAR_DOUBLE_QUOTE);
                    i += 6;
                } else if (text.startsWith(ENCODED_LESS_THAN, i)) {
                    result.append(CHAR_LESS_THAN);
                    i += 4;
                } else if (text.startsWith(ENCODED_GREATER_THAN, i)) {
                    result.append(CHAR_GREATER_THAN);
                    i += 4;
                } else if (text.startsWith(ENCODED_UTF8, i)) {
                    final int index = text.indexOf(';', i);
                    result.append(toChar(text.substring(i+2, index)));
                    i = index+1;
                }
                else i++;
            }
        }
        return result.toString();
    }

}
