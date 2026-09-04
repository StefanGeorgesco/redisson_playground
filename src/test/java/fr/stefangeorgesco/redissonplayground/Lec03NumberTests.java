package fr.stefangeorgesco.redissonplayground;

import org.junit.jupiter.api.Test;
import org.redisson.api.RAtomicLongReactive;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

class Lec03NumberTests extends BaseTests {

    private RAtomicLongReactive atomicLong;

    @Test
    void keyValueIncreaseTest() {
        atomicLong = client.getAtomicLong("user:1:visits");
        Mono<Void> mono = Flux.range(1, 30)
                .delayElements(Duration.ofMillis(200))
                .flatMap(i -> incrementAndGet())
                .then();
        StepVerifier.create(mono)
                .verifyComplete();
    }

    /*
        Helper method
     */

    private Mono<Long> incrementAndGet() {
        return atomicLong.incrementAndGet()
                .doOnNext(aLong -> log.info("Incremented value: {}", aLong));
    }
}
