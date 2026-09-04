package fr.stefangeorgesco.redissonplayground;

import fr.stefangeorgesco.redissonplayground.dto.Student;
import org.junit.jupiter.api.Test;
import org.redisson.api.RMapCacheReactive;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Lec07MapCacheTests extends BaseTests {

    protected RMapCacheReactive<Integer, Student> mapCache;

    @Test
    void mapCacheTest() {
        TypedJsonJacksonCodec codec = new TypedJsonJacksonCodec(Integer.class, Student.class);
        mapCache = client.getMapCache("users:cache", codec);

        Student student1 = new Student("Sam", 30, "Atlanta", List.of(90, 80, 70));
        Student student2 = new Student("Jake", 41, "Toronto", List.of(90, 80));
        Student student3 = new Student("Jill", 21, "Atlanta", List.of(90, 80));

        Mono<Student> putStudent1 = mapCache.put(1, student1, 1000, TimeUnit.MILLISECONDS);
        Mono<Student> putStudent2 = mapCache.put(2, student2, 2000, TimeUnit.MILLISECONDS);
        Mono<Student> putStudent3 = mapCache.put(3, student3);

        // Put 3 students in mapCache
        StepVerifier.create(
                        putStudent1
                                .then(putStudent2)
                                .then(putStudent3)
                                .then()
                                .doOnSuccess(unused ->
                                        log.info("Put 3 students in mapCache {}", mapCache.getName()))
                )
                .verifyComplete();

        // After 500ms, students 1 and 2 should still be in mapCache
        StepVerifier.create(
                        delay(500)
                                .then(get(1))

                )
                .consumeNextWith(student ->
                        assertEquals("Sam", student.getName()))
                .verifyComplete();

        StepVerifier.create(get(2))
                .consumeNextWith(student ->
                        assertEquals("Jake", student.getName()))
                .verifyComplete();

        // After another 600ms, student 1 should be expired student 2 should still be in mapCache
        StepVerifier.create(
                        delay(600)
                                .then(get(1))
                )
                .expectNextCount(0)
                .verifyComplete();

        StepVerifier.create(get(2))
                .consumeNextWith(student ->
                        assertEquals("Jake", student.getName()))
                .verifyComplete();

        // After another 1000ms, student 2 should be expired and student 3 should still be in mapCache
        StepVerifier.create(
                        delay(1000)
                                .then(get(2))
                )
                .expectNextCount(0)
                .verifyComplete();

        StepVerifier.create(
                        delay(1000)
                                .then(get(3))
                )
                .consumeNextWith(student ->
                        assertEquals("Jill", student.getName()))
                .verifyComplete();
    }

    protected Mono<Student> get(Integer k) {
        return mapCache.get(k)
                .doOnNext(v -> log.info("Read {}[{}]: {}", mapCache.getName(), k, v));
    }
}
