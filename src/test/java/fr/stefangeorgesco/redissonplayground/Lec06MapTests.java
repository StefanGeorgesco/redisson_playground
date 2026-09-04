package fr.stefangeorgesco.redissonplayground;

import fr.stefangeorgesco.redissonplayground.dto.Student;
import org.junit.jupiter.api.Test;
import org.redisson.api.RMapReactive;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;

class Lec06MapTests extends BaseTests {

    private RMapReactive<String, String> stringStringRMap;

    @Test
    void mapTestPutStringString() {
        stringStringRMap = client.getMap("user:1", StringCodec.INSTANCE);
        Mono<String> putName = stringStringRMap.put("name", "Sam");
        Mono<String> putAge = stringStringRMap.put("age", "30");
        Mono<String> putCity = stringStringRMap.put("city", "Atlanta");

        StepVerifier.create(
                        putName
                                .then(putAge)
                                .then(putCity)
                                .then()
                )
                .verifyComplete();
    }

    @Test
    void mapTestPutAllStringString() {
        stringStringRMap = client.getMap("user:2", StringCodec.INSTANCE);
        var studentMap = Map.of(
                "name", "Jake",
                "age", "41",
                "city", "Toronto"
        );
        Mono<Void> putAll = stringStringRMap.putAll(studentMap);

        StepVerifier.create(putAll)
                .verifyComplete();
    }

    @Test
    void mapTestPutIntegerStudent() {
        TypedJsonJacksonCodec codec = new TypedJsonJacksonCodec(Integer.class, Student.class);
        RMapReactive<Integer, Student> integerStudentMap = client.getMap("users", codec);
        Student student1 = new Student("Sam", 30, "Atlanta", List.of(90, 80, 70));
        Student student2 = new Student("Jake", 41, "Toronto", List.of(90, 80));
        Student student3 = new Student("Sam", 21, "Atlanta", List.of(90, 80));
        Mono<Student> putStudent1 = integerStudentMap.put(1, student1);
        Mono<Student> putStudent2 = integerStudentMap.put(2, student2);
        Mono<Student> putStudent3 = integerStudentMap.put(3, student3);

        StepVerifier.create(
                        putStudent1
                                .then(putStudent2)
                                .then(putStudent3)
                                .then()
                )
                .verifyComplete();
    }

    @Test
    void mapTestPutAllIntegerStudent() {
        TypedJsonJacksonCodec codec = new TypedJsonJacksonCodec(Integer.class, Student.class);
        RMapReactive<Integer, Student> integerStudentMap = client.getMap("users", codec);
        var studentsMap = Map.of(
                1, new Student("Sam", 30, "Atlanta", List.of(90, 80, 70)),
                2, new Student("Jake", 41, "Toronto",  List.of(100, 90, 80)),
                3, new Student("Jill", 25, "New York", List.of(85, 75, 65))
        );
        Mono<Void> putAll = integerStudentMap.putAll(studentsMap);

        StepVerifier.create(putAll)
                .verifyComplete();
    }
}
