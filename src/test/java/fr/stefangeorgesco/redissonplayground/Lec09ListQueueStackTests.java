package fr.stefangeorgesco.redissonplayground;

import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.redisson.api.RDequeReactive;
import org.redisson.api.RListReactive;
import org.redisson.api.RQueueReactive;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.stream.LongStream;

@TestMethodOrder(OrderAnnotation.class)
class Lec09ListQueueStackTests extends BaseTests {

    @Test
    @Order(1)
    void listTest() {
        RListReactive<Long> list = client.getList("number-input", LongCodec.INSTANCE);

        int initialSize = list.size().blockOptional().orElse(0);

        List<Long> longList = LongStream.rangeClosed(1, 10)
                .boxed()
                .toList();

        Mono<Void> addAllToList = list.addAll(longList)
                .then();

        StepVerifier.create(addAllToList)
                .verifyComplete();

        StepVerifier.create(list.size())
                .expectNext(initialSize + 10)
                .verifyComplete();
    }

    @Test
    @Order(2)
    void queueTest() {
        RQueueReactive<Long> queue = client.getQueue("number-input", LongCodec.INSTANCE);

        int initialSize = queue.size().blockOptional().orElse(0);

        Mono<Void> poll = queue.poll()
                .repeat(3)
                .map(String::valueOf)
                .doOnNext(log::info)
                .then();

        StepVerifier.create(poll)
                .verifyComplete();

        StepVerifier.create(queue.size().doOnNext(integer -> log.info("Queue size: {}", integer)))
                .expectNext(Math.max(0, initialSize - 4))
                .verifyComplete();
    }

    @Test
    @Order(3)
    void stackTest() { // Deque
        RDequeReactive<Long> deque = client.getDeque("number-input", LongCodec.INSTANCE);

        int initialSize = deque.size().blockOptional().orElse(0);

        Mono<Void> poll = deque.pollLast()
                .repeat(5)
                .map(String::valueOf)
                .doOnNext(log::info)
                .then();

        StepVerifier.create(poll)
                .verifyComplete();

        StepVerifier.create(deque.size().doOnNext(integer -> log.info("Stack size: {}", integer)))
                .expectNext(Math.max(0, initialSize - 6))
                .verifyComplete();
    }
}
