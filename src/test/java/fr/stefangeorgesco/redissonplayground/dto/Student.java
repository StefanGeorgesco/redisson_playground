package fr.stefangeorgesco.redissonplayground.dto;

import java.util.List;

/* Must be a class to work with Redisson's JsonJacksonCodec.INSTANCE codec,
   but can be a record to work with Redisson's TypedJsonJacksonCodec codec.
* */
public class Student {

    private String name;
    private int age;
    private String city;
    private List<Integer> marks;

    @SuppressWarnings("unused")
    public Student() {
        // In case of a class, necessary for Redisson to deserialize the object
    }

    public Student(String name, int age, String city, List<Integer> marks) {
        this.name = name;
        this.age = age;
        this.city = city;
        this.marks = marks;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getCity() {
        return city;
    }

    public List<Integer> getMarks() {
        return marks;
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", city='" + city + '\'' +
                ", marks=" + marks +
                '}';
    }
}
