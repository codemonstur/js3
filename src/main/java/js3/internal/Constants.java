package js3.internal;

import java.time.Clock;

/**
 * The {@code Constants} enum.
 */
public enum Constants {;

    /**
     * The {@code clock} constant.
     */
    public static final Clock clock = Clock.systemUTC();

    /**
     * The {@code DEFAULT_CONNECT_TIMEOUT} constant.
     */
    public static final int DEFAULT_CONNECT_TIMEOUT = 60_000;
    /**
     * The {@code DEFAULT_READ_TIMEOUT} constant.
     */
    public static final int DEFAULT_READ_TIMEOUT = 60_000;
    /**
     * The {@code BLOCK_SIZE} constant.
     */
    public static final int BLOCK_SIZE = 16 * 1024;

}
