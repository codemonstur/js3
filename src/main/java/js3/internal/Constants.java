package js3.internal;

import java.time.Clock;

public enum Constants {;

    public static final Clock clock = Clock.systemUTC();

    public static final int DEFAULT_CONNECT_TIMEOUT = 60_000;
    public static final int DEFAULT_READ_TIMEOUT = 60_000;
    public static final int BLOCK_SIZE = 16 * 1024;

}
