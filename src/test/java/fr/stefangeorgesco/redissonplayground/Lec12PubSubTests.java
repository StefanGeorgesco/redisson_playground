package fr.stefangeorgesco.redissonplayground;

import org.junit.jupiter.api.Test;
import org.redisson.api.RPatternTopicReactive;
import org.redisson.api.RTopicReactive;
import org.redisson.client.codec.StringCodec;

import static org.junit.jupiter.api.Assertions.assertTrue;

class Lec12PubSubTests extends BaseTests {

    @Test
    void subscriber1() {
        RTopicReactive topic = client.getTopic("slack-room-1", StringCodec.INSTANCE);
        topic.getMessages(String.class)
                .doOnError(throwable -> log.info("Subscriber 1 error: {}", throwable.getMessage()))
                .doOnNext(message -> log.info("Subscriber 1 received: {}", message))
                .subscribe();
        sleep(600_000);
        assertTrue(true);
    }

    @Test
    void subscriber2() {
        RPatternTopicReactive patternTopic = client.getPatternTopic("slack-room-*", StringCodec.INSTANCE);
        patternTopic.addListener(String.class, (pattern, topic, message) ->
                        log.info("Subscriber 2 received from topic {} (pattern {}): {}", topic, pattern, message))
                .subscribe();
        sleep(600_000);
        assertTrue(true);
    }
}
