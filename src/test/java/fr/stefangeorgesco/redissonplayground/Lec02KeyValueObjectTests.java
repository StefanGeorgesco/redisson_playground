package fr.stefangeorgesco.redissonplayground;

import fr.stefangeorgesco.redissonplayground.dto.Student;
import org.junit.jupiter.api.Test;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Lec02KeyValueObjectTests extends BaseBucketTests<Student> {

    @Test
    void keyValueObjectDefaultCodecTest() {
        Student student = new Student("Marshal", 20, "Atlanta", List.of(90, 85, 92));
        bucket = client.getBucket("student:1");
        verify(student);
    }

    @Test
    void keyValueObjectJsonJacksonCodecTest() {
        Student student = new Student("Marshal", 20, "Atlanta", List.of(90, 85, 92));
        bucket = client.getBucket("student:1", JsonJacksonCodec.INSTANCE);
        verify(student);
    }

    @Test
    void keyValueObjectTypedJsonJacksonCodecTest() {
        Student student = new Student("Marshal", 20, "Atlanta", List.of(90, 85, 92));
        bucket = client.getBucket("student:1", new TypedJsonJacksonCodec(Student.class));
        verify(student);
    }

    /*
        Helper method
     */

    private void verify(Student student) {
        set = bucket.set(student);
        StepVerifier.create(set.then(ttl()).then(get()))
                .consumeNextWith(s -> {
                    assertEquals(student.getName(), s.getName());
                    assertEquals(student.getAge(), s.getAge());
                    assertEquals(student.getCity(), s.getCity());
                    assertArrayEquals(student.getMarks().toArray(), s.getMarks().toArray());
                })
                .verifyComplete();
    }
}
