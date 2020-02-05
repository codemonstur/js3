package js3.util;

public enum Constants {;

    public static final char
        CHAR_FORWARD_SLASH = '/',
        CHAR_SPACE = ' ',
        CHAR_EQUALS = '=',
        CHAR_LESS_THAN = '<',
        CHAR_GREATER_THAN = '>',
        CHAR_QUESTION_MARK = '?',
        CHAR_SINGLE_QUOTE = '\'',
        CHAR_DOUBLE_QUOTE = '"',
        CHAR_AMPERSAND = '&';

    public static final char
        XML_TAG_START = CHAR_LESS_THAN,
        XML_TAG_END = CHAR_GREATER_THAN,
        XML_SELF_CLOSING = CHAR_FORWARD_SLASH,
        XML_PROLOG = CHAR_QUESTION_MARK;

    public static final String
        XML_START_COMMENT = "!--",
        XML_END_COMMENT = "--";

    public static final String
        EMPTY = "",
        FORWARD_SLASH = "/";

    public static final String
        ENCODED_LESS_THAN = "&lt;",
        ENCODED_GREATER_THAN = "&gt;",
        ENCODED_SINGLE_QUOTE = "&apos;",
        ENCODED_DOUBLE_QUOTE = "&quot;",
        ENCODED_AMPERSAND = "&amp;",
        ENCODED_UTF8 = "&#";

}
