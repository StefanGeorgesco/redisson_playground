package fr.stefangeorgesco.redissonplayground;

import org.junit.jupiter.api.Test;
import org.redisson.api.RBatchReactive;
import org.redisson.api.RListReactive;
import org.redisson.api.RSetReactive;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class Lec13BatchTests extends BaseTests {

    @Test // 9 sec
    void batchTest() {
        RBatchReactive batch = client.createBatch();
        RListReactive<Long> list = batch.getList("numbers-list", LongCodec.INSTANCE);
        RSetReactive<Long> set = batch.getSet("numbers-set", LongCodec.INSTANCE);
        for (long i = 0; i < 500_000; i++) {
            list.add(i).subscribe();
            set.add(i).subscribe();
        }
        StepVerifier.create(batch.execute().then())
                .verifyComplete();
    }

    @Test // 42 sec
    void noBatchTest() {
        RListReactive<Long> list = client.getList("numbers-list", LongCodec.INSTANCE);
        RSetReactive<Long> set = client.getSet("numbers-set", LongCodec.INSTANCE);
        Mono<Void> mono = Flux.range(0, 500_000)
                .map(Integer::longValue)
                .flatMap(i -> list.add(i).then(set.add(i)))
                .then();
        StepVerifier.create(mono)
                .verifyComplete();
    }
}
