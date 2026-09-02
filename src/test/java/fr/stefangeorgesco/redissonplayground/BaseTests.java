package fr.stefangeorgesco.redissonplayground;

import fr.stefangeorgesco.redissonplayground.config.RedissonConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.redisson.api.RBucketReactive;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.time.Duration;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class BaseTests<V> {

    private final RedissonConfig redissonConfig = new RedissonConfig();
    private RedissonClient redissonClient;

    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected RedissonReactiveClient client;
    protected RBucketReactive<V> bucket;
    protected Mono<Void> set;

    @BeforeAll
    void setup() {
        redissonClient = redissonConfig.getClient();
        client = redissonClient.reactive();
    }

    @AfterAll
    void tearDown() {
        if (redissonClient != null) {
            redissonClient.shutdown();
        }
    }

    /*
        Helper methods
     */

    protected Mono<V> get() {
        return bucket.get()
                .doOnNext(s -> log.info("Value: {}", s));
    }

    protected Mono<Long> ttl() {
        return bucket.remainTimeToLive()
                .doOnNext(aLong -> log.info("Remaining TTL: {}", aLong));
    }

    protected static Duration duration(long millis) {
        return Duration.ofMillis(millis);
    }

    protected static Mono<Long> delay(long millis) {
        return Mono.delay(duration(millis));
    }
}
