package fr.stefangeorgesco.redissonplayground;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.client.codec.StringCodec;
import reactor.test.StepVerifier;

class Lec01KeyValueTests extends BaseBucketTests<String> {

    @BeforeEach
    void setUpEach() {
        bucket = client.getBucket("user:1:name", StringCodec.INSTANCE);
    }

    @Test
    void keyValueAccessTest() {
        set = bucket.set("Sam");
        StepVerifier.create(set.then(ttl()).then(get()))
                .expectNext("Sam")
                .verifyComplete();
    }

    @Test
    void keyValueExpiryTest() {
        set = bucket.set("Sam", duration(1000));

        StepVerifier.create(set.then(ttl()).then(get()))
                .expectNext("Sam")
                .verifyComplete();

        StepVerifier.create(delay(1000).then(ttl()).then(get()))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void keyValueExtendExpiryTest() {
        set = bucket.set("Sam", duration(1000));

        StepVerifier.create(set.then(ttl()).then(get()))
                .expectNext("Sam")
                .verifyComplete();

        StepVerifier.create(
                        delay(500)
                                .then(bucket.expire(duration(1000)))
                                .then(delay(800).then(ttl()).then(get()))
                )
                .expectNext("Sam")
                .verifyComplete();

        StepVerifier.create(delay(200).then(ttl()).then(get()))
                .expectNextCount(0)
                .verifyComplete();
    }
}
