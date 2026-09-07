package fr.stefangeorgesco.redissonplayground;

import fr.stefangeorgesco.redissonplayground.dto.Student;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.options.LocalCachedMapOptions;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class Lec08LocalCachedMapTests extends BaseTests {

    private RLocalCachedMap<Integer, Student> studentsMap;

    @BeforeAll
    void setupStudentsMap() {
        LocalCachedMapOptions<Integer, Student> mapOptions =
                LocalCachedMapOptions.<Integer, Student>name("students")
                        .codec(new TypedJsonJacksonCodec(Integer.class, Student.class))
                        .syncStrategy(LocalCachedMapOptions.SyncStrategy.UPDATE)
                        .reconnectionStrategy(LocalCachedMapOptions.ReconnectionStrategy.CLEAR);

        studentsMap = redissonClient.getLocalCachedMap(mapOptions);
    }

    @Test
    void appServer1() {
        Student student1 = new Student("Sam", 30, "Atlanta", List.of(90, 80, 70));
        Student student2 = new Student("Jake", 41, "Toronto", List.of(90, 80));
        studentsMap.put(1, student1);
        studentsMap.put(2, student2);

        Flux.interval(Duration.ofSeconds(1))
                .doOnNext(i -> log.info("{}/ AppServer1 - Student 1: {}", i, studentsMap.get(1)))
                .subscribe();

        sleep(600_000);
        assertTrue(true);
    }

    @Test
    void appServer2() {
        Student student1 = new Student("Sam-updated", 30, "Atlanta", List.of(90, 80, 70));
        studentsMap.put(1, student1);
        assertTrue(true);
    }
}
