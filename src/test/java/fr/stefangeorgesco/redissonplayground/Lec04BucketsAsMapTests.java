package fr.stefangeorgesco.redissonplayground;

import org.junit.jupiter.api.Test;
import org.redisson.client.codec.StringCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class Lec04BucketsAsMapTests extends BaseTests {

    @Test
    void bucketsAsMap() {
        client.getBucket("user:1:name", StringCodec.INSTANCE).set("Sam").block();
        client.getBucket("user:2:name", StringCodec.INSTANCE).set("John").block();
        client.getBucket("user:3:name", StringCodec.INSTANCE).set("Jane").block();

        Mono<Void> mono = client.getBuckets(StringCodec.INSTANCE)
                // the result will not have a "user:4:name" key if it does not exist in Redis
                .get("user:1:name", "user:2:name", "user:3:name", "user:4:name")
                .map(Object::toString)
                .doOnNext(log::info)
                .then();

        StepVerifier.create(mono)
                .verifyComplete();
    }
}
