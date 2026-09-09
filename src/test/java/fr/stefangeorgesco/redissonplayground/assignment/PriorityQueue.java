package fr.stefangeorgesco.redissonplayground.assignment;

import org.redisson.api.RScoredSortedSetReactive;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class PriorityQueue {

    private final RScoredSortedSetReactive<UserOrder> queue;

    public PriorityQueue(RScoredSortedSetReactive<UserOrder> queue) {
        this.queue = queue;
    }

    public Mono<Void> add(UserOrder order) {
        return queue.add(
                getScore(order),
                order
        ).then();
    }

    public Flux<UserOrder> take() {
        return queue.takeFirstElements()
                .limitRate(1);
    }

    private double getScore(UserOrder order) {
        return order.category().ordinal() + Double.parseDouble("0." + System.nanoTime());
    }
}
