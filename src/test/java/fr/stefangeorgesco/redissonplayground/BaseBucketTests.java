package fr.stefangeorgesco.redissonplayground;

import org.redisson.api.RBucketReactive;
import reactor.core.publisher.Mono;

import java.time.Duration;


abstract class BaseBucketTests<V> extends BaseTests {

    protected RBucketReactive<V> bucket;

    protected Mono<V> get() {
        return bucket.get()
                .doOnNext(s -> log.info("Read {} value: {}", bucket.getName(), s));
    }

    protected Mono<Long> ttl() {
        return bucket.remainTimeToLive()
                .doOnNext(aLong -> log.info("Remaining TTL on bucket {}: {}", bucket.getName(), aLong));
    }

    protected static Duration duration(long millis) {
        return Duration.ofMillis(millis);
    }

    protected static Mono<Long> delay(long millis) {
        return Mono.delay(duration(millis));
    }
}
