package fr.stefangeorgesco.redissonplayground;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBlockingDequeReactive;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

class Lec10MessageQueueTests extends BaseTests {

    private RBlockingDequeReactive<Long> msgQueue;

    @BeforeAll
    void setupQueue() {
        msgQueue = client.getBlockingDeque("message-queue", LongCodec.INSTANCE);
    }

    @Test
    void consumer1() {
        msgQueue.takeElements()
                .doOnNext(aLong -> log.info("Consumer 1 - Received message: {}", aLong))
                .doOnError(throwable -> log.error("Consumer 1 - Error: {}", throwable.getMessage()))
                .subscribe();

        sleep(600_000);
        assertTrue(true);
    }

    @Test
    void consumer2() {
        msgQueue.takeElements()
                .doOnNext(aLong -> log.info("Consumer 2 - Received message: {}", aLong))
                .doOnError(throwable -> log.error("Consumer 2 - Error: {}", throwable.getMessage()))
                .subscribe();

        sleep(600_000);
        assertTrue(true);
    }

    @Test
    void producer() {
        Mono<Void> addMono = Flux.range(1, 100)
                .delayElements(Duration.ofMillis(500))
                .doOnNext(i -> log.info("Producer - Sending message: {}", i))
                .flatMap(i -> msgQueue.add(i.longValue()))
                .then();

        StepVerifier.create(addMono)
                .verifyComplete();
    }
}
