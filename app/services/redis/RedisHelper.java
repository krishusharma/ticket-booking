package services.redis;

import com.google.inject.ImplementedBy;

import java.util.Set;
import java.util.concurrent.locks.Lock;

/**
 * Redis helper abstraction used ONLY for distributed locking.
 *
 * Redis is NOT the source of truth.
 * Database transactions remain authoritative.
 */
@ImplementedBy(RedisHelperImpl.class)
public interface RedisHelper {

    /**
     * Acquire a distributed lock for given keys.
     *
     * @param waitTimeSec   max time to wait for lock
     * @param leaseTimeSec  auto-release time
     * @param values        lock identifiers (e.g. eventId)
     * @param prefix        namespace prefix
     * @return Lock if acquired, else null
     */
    Lock getDistributedLock(
            long waitTimeSec,
            long leaseTimeSec,
            Set<String> values,
            String prefix
    );

    /**
     * Release previously acquired lock.
     */
    void releaseLock(Lock lock);

    /**
     * Whether Redis locking is enabled.
     */
    boolean isRedisEnabled();
}
