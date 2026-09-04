package fr.stefangeorgesco.redissonplayground;

import org.redisson.api.RBucketReactive;
import reactor.core.publisher.Mono;

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
}
