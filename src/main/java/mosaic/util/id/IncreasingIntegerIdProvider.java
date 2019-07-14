package mosaic.util.id;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A thread safe, unique, increasing id provider
 */
public class IncreasingIntegerIdProvider implements IdProvider {
    private final AtomicInteger nextAvailableId;

    public IncreasingIntegerIdProvider(int firstId) {
        nextAvailableId = new AtomicInteger(firstId);
    }

    @Override
    public String provide() {
        return String.valueOf(nextAvailableId.getAndIncrement());
    }
}
