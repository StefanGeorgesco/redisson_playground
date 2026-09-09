package fr.stefangeorgesco.redissonplayground;

import fr.stefangeorgesco.redissonplayground.assignment.Category;
import fr.stefangeorgesco.redissonplayground.assignment.PriorityQueue;
import fr.stefangeorgesco.redissonplayground.assignment.UserOrder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.redisson.api.RAtomicLongReactive;
import org.redisson.api.RScoredSortedSetReactive;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Flux;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

class Lec16PriorityQueueTests extends BaseTests {

    private RAtomicLongReactive atomicLong;
    private PriorityQueue priorityQueue;

    @BeforeAll
    void setupQueue() {
        atomicLong = client.getAtomicLong("order:id");
        RScoredSortedSetReactive<UserOrder> sortedSet =
                client.getScoredSortedSet("user:orders", new TypedJsonJacksonCodec(UserOrder.class));
        priorityQueue = new PriorityQueue(sortedSet);
    }

    @Test
    void producer() {
        Flux.interval(Duration.ofMillis(200))
                .flatMap(l -> atomicLong.incrementAndGet())
                .map(Long::intValue)
                .map(id -> new UserOrder(id, Category.getRandomCategory()))
                .doOnNext(order -> log.info("Producing order: {}", order))
                .flatMap(priorityQueue::add)
                .subscribe();

        sleep(60_000);
        assertTrue(true);
    }

    @Test
    void consumer() {
        priorityQueue.take()
                .delayElements(Duration.ofMillis(500))
                .doOnNext(order -> log.info("Processing order: {}", order))
                .subscribe();

        sleep(600_000);
        assertTrue(true);
    }
}
