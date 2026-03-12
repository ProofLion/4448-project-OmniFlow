package util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Central id generator for demo entities.
 * register() lets loaded files preserve ids without collisions.
 */
public final class Ids {
    private static final AtomicLong NEXT = new AtomicLong(1);

    private Ids() {
    }

    public static long next() {
        return NEXT.getAndIncrement();
    }

    public static void register(long id) {
        NEXT.updateAndGet(current -> Math.max(current, id + 1));
    }
}
