package fr.stefangeorgesco.redissonplayground;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBucketReactive;
import org.redisson.api.RTransactionReactive;
import org.redisson.api.TransactionOptions;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SuppressWarnings("LoggingSimilarMessage")
class Lec14TransactionTests extends BaseTests {

    private RBucketReactive<Long> user1Balance;
    private RBucketReactive<Long> user2Balance;

    @BeforeAll
    void accountSetup() {
        user1Balance = client.getBucket("user:1:balance", LongCodec.INSTANCE);
        user2Balance = client.getBucket("user:2:balance", LongCodec.INSTANCE);
        Mono<Void> mono = user1Balance.set(100L)
                .then(user2Balance.set(0L))
                .then();
        StepVerifier.create(mono)
                .verifyComplete();
    }

    @AfterAll
    void accountBalanceStatus() {
        Mono<Void> mono = Flux.zip(user1Balance.get(), user2Balance.get())
                .doOnNext(t -> log.info("User 1 balance: {}, User 2 balance: {}",
                        t.getT1(), t.getT2()))
                .then();
        StepVerifier.create(mono)
                .verifyComplete();
    }

    @Test
    void nonTransactionTest() {
        Mono<Void> mono = transfer(user1Balance, user2Balance, 50)
                .thenReturn(0)
                .map(i -> (5 / i)) // to simulate some error
                .doOnError(e -> log.error("Error during transfer"))
                .then();
        StepVerifier.create(mono)
                .expectError(ArithmeticException.class)
                .verify();
    }

    @Test
    void transactionTest() {
        RTransactionReactive transaction = client.createTransaction(TransactionOptions.defaults());
        RBucketReactive<Long> user1TBalance = transaction.getBucket("user:1:balance", LongCodec.INSTANCE);
        RBucketReactive<Long> user2TBalance = transaction.getBucket("user:2:balance", LongCodec.INSTANCE);
        Mono<Void> mono = transfer(user1TBalance, user2TBalance, 50)
                .thenReturn(0)
                .map(i -> (5 / i)) // to simulate some error
                .then(transaction.commit())
                .onErrorResume(e ->
                        transaction.rollback()
                                .then(Mono.error(e)))
                .doOnError(e -> log.error("Error during transfer"))
                .then();
        StepVerifier.create(mono)
                .expectError(ArithmeticException.class)
                .verify();
    }

    @SuppressWarnings("SameParameterValue")
    private Mono<Void> transfer(RBucketReactive<Long> from, RBucketReactive<Long> to, int amount) {
        return Flux.zip(from.get(), to.get())
                .filter(t -> t.getT1() >= amount)
                .flatMap(t -> from.set(t.getT1() - amount).then(to.set(t.getT2() + amount)))
                .then();
    }
}
