package fr.stefangeorgesco.redissonplayground;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.api.DeletedObjectListener;
import org.redisson.api.ExpiredObjectListener;
import org.redisson.api.ObjectListener;
import org.redisson.client.codec.StringCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/*
    Type this command in the redis-cli before running the tests
    to enable event notifications and see the events:
    'config set notify-keyspace-events AKE'
 */

class Lec05EventListenerTests extends BaseBucketTests<String> {

    @BeforeEach
    void setUpEach() {
        bucket = client.getBucket("user:1:name", StringCodec.INSTANCE);
    }

    @Test
    void expiredEventTest() {
        Mono<Integer> addListener = addListener((ExpiredObjectListener) key
                -> log.info("Expired event received for key: {}", key));

        long ttlValue = 1000L;

        set = bucket.set("sam", duration(ttlValue))
                .doOnSuccess(v ->
                        log.info("Set {}={}, TTL={}", bucket.getName(), "sam", ttlValue));

        StepVerifier.create(
                        addListener
                                .then(set)
                                .then(ttl())
                                .then(get())
                                .then(delay(1200))
                                .then()
                )
                .verifyComplete();
    }

    @Test
    void deletedEventTest() {
        Mono<Integer> addListener = addListener((DeletedObjectListener) key
                -> log.info("Deleted event received for key: {}", key));

        set = bucket.set("sam")
                .doOnSuccess(v ->
                        log.info("Set {}={}", bucket.getName(), "sam"));

        StepVerifier.create(
                        addListener
                                .then(set)
                                .then(ttl())
                                .then(get())
                                .then(delete())
                                .then(delay(200))
                                .then()
                )
                .verifyComplete();
    }

    /*
        Helper method
     */

    private Mono<Integer> addListener(ObjectListener listener) {
        String listenerType = listener instanceof ExpiredObjectListener ? "Expired" : "Deleted";
        return bucket.addListener(listener)
                .doOnNext(id -> log.info("{} listener added on bucket {}, id={}", listenerType, bucket.getName(),
                        id));
    }

    private Mono<Boolean> delete() {
        return bucket.delete()
                .doOnSuccess(v -> log.info("Deleted bucket {}, success={}", bucket.getName(), v));
    }
}
