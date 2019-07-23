package mosaic.util.id;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Guarantees key uniqueness for a single machine and multiple machines with a unique prefix
 */
public class AlphanumericIdProvider implements IdProvider {
    private static final int timeStringRadix = 36;

    // Added to time to avoid same-time collisions
    private final AtomicLong noise;

    // Prefixed to the output string to avoid collisions on different machines
    private final String prefix;

    public AlphanumericIdProvider() {
        this("");
    }

    public AlphanumericIdProvider(String prefix) {
        this.prefix = prefix;
        this.noise = new AtomicLong();
    }

    @Override
    public String provide() {
        long time = System.currentTimeMillis() + noise.getAndIncrement();
        return String.format("%s%s", prefix, Long.toString(time, timeStringRadix));
    }
}
