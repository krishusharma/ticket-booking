package services.redis;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.redisson.Redisson;
import org.redisson.RedissonMultiLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import play.Configuration;
import play.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * RedisHelper implementation using Redisson.
 *
 * Uses simple MultiLock (NOT RedLock).
 * Suitable for single Redis instance deployments.
 */
@Singleton
public class RedisHelperImpl implements RedisHelper {

    private static final String TAG = RedisHelperImpl.class.getSimpleName();

    @Inject
    Configuration configuration;

    private volatile RedissonClient redissonClient;

    // ----------------------------
    // Redis Client Initialization
    // ----------------------------
    private RedissonClient getClient() {

        if (!isRedisEnabled()) {
            return null;
        }

        if (redissonClient == null) {
            synchronized (this) {
                if (redissonClient == null) {
//                    String host = configuration.getString("redis.host", "127.0.0.1");
//                    int port = configuration.getInt("redis.port", 6379);
//
//                    Config config = new Config();
//                    config.useSingleServer()
//                            .setAddress("redis://" + host + ":" + port);

                    String host = configuration.getString("redis.host", "127.0.0.1");
                    int port = configuration.getInt("redis.port", 6379);
                    String password = configuration.getString("redis.password");

                    Config config = new Config();
                    config.useSingleServer()
                            .setAddress("redis://" + host + ":" + port)
                            .setConnectionMinimumIdleSize(2)
                            .setConnectionPoolSize(10)
                            .setConnectTimeout(10000)
                            .setRetryAttempts(3)
                            .setRetryInterval(1500);

                    if (password != null && !password.isEmpty()) {
                        config.useSingleServer().setPassword(password);
                    }

                    redissonClient = Redisson.create(config);
                    Logger.info("{}: Redis client initialized at {}:{}", TAG, host, port);
                }
            }
        }
        return redissonClient;
    }

    // ----------------------------
    // Distributed Lock
    // ----------------------------
    @Override
    public Lock getDistributedLock(
            long waitTimeSec,
            long leaseTimeSec,
            Set<String> values,
            String prefix
    ) {

        // Redis disabled → fallback (DEV only)
        if (!isRedisEnabled() || getClient() == null) {
            ReentrantLock lock = new ReentrantLock();
            try {
                if (lock.tryLock(waitTimeSec, TimeUnit.SECONDS)) {
                    return lock;
                }
            } catch (InterruptedException ignored) {}
            return null;
        }

        List<RLock> locks = new ArrayList<>();

        for (String value : values) {
            if (value != null && !value.isEmpty()) {
                locks.add(getClient().getLock(prefix + ":" + value));
            }
        }

        if (locks.isEmpty()) {
            return null;
        }

        RedissonMultiLock multiLock =
                new RedissonMultiLock(locks.toArray(new RLock[0]));

        try {
            if (multiLock.tryLock(waitTimeSec, leaseTimeSec, TimeUnit.SECONDS)) {
                return multiLock;
            }
        } catch (Exception e) {
            Logger.error("{}: Failed to acquire redis lock", TAG, e);
        }

        return null;
    }

    // ----------------------------
    // Release Lock
    // ----------------------------
    @Override
    public void releaseLock(Lock lock) {
        try {
            if (lock != null) {
                lock.unlock();
            }
        } catch (Exception e) {
            Logger.warn("{}: Failed to release lock", TAG, e);
        }
    }

    // ----------------------------
    // Config Flag
    // ----------------------------
    @Override
    public boolean isRedisEnabled() {
        return configuration.getBoolean("redis.enabled", true);
    }
}
