package fr.stefangeorgesco.redissonplayground;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.client.codec.StringCodec;
import reactor.test.StepVerifier;

@SuppressWarnings("LoggingSimilarMessage")
class Lec01KeyValueTests extends BaseTests<String> {

    @BeforeEach
    void setUpEach() {
        bucket = client.getBucket("user:1:name", StringCodec.INSTANCE);
    }

    @Test
    void keyValueAccessTest() {
        set = bucket.set("sam");
        StepVerifier.create(set.then(ttl()).then(get()))
                .expectNext("sam")
                .verifyComplete();
    }

    @Test
    void keyValueExpiryTest() {
        set = bucket.set("sam", duration(1000));

        StepVerifier.create(set.then(ttl()).then(get()))
                .expectNext("sam")
                .verifyComplete();

        StepVerifier.create(delay(1000).then(ttl()).then(get()))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void keyValueExtendExpiryTest() {
        set = bucket.set("sam", duration(1000));

        StepVerifier.create(set.then(ttl()).then(get()))
                .expectNext("sam")
                .verifyComplete();

        StepVerifier.create(
                        delay(500)
                                .then(bucket.expire(duration(1000)))
                                .then(delay(800).then(ttl()).then(get()))
                )
                .expectNext("sam")
                .verifyComplete();

        StepVerifier.create(delay(200).then(ttl()).then(get()))
                .expectNextCount(0)
                .verifyComplete();
    }
}
