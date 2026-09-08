package fr.stefangeorgesco.redissonplayground;

import org.junit.jupiter.api.Test;
import org.redisson.api.RScoredSortedSetReactive;
import org.redisson.client.codec.StringCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.Function;

class Lec15SortedSetTests extends BaseTests {

    @Test
    void sortedSet() {
        RScoredSortedSetReactive<String> sortedSet = client.getScoredSortedSet("student:score", StringCodec.INSTANCE);

        Mono<Void> addEntries = sortedSet
                .addScore("sam", 12.25)// increment score of sam by 12.25
                .then(sortedSet.add(23.25, "mike")) // add (or replace) mike with score 23.25
                .then(sortedSet.addScore("jake", 7.15)) // increment score of jake by 7.15
                .then();

        StepVerifier.create(addEntries)
                .verifyComplete();

        Mono<Void> readEntries = sortedSet.entryRangeReversed(0, -1) // get all entries in reverse order
                .flatMapIterable(Function.identity()) // transform mono of collection into a flux of entries
                .map(se -> se.getValue() + ": " + se.getScore())
                .doOnNext(log::info)
                .then();

        StepVerifier.create(readEntries)
                .verifyComplete();
    }
}
