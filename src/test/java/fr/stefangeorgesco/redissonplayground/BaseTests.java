package fr.stefangeorgesco.redissonplayground;

import fr.stefangeorgesco.redissonplayground.config.RedissonConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class BaseTests {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private final RedissonConfig redissonConfig = new RedissonConfig();
    private RedissonClient redissonClient;
    protected RedissonReactiveClient client;

    @BeforeAll
    void setup() {
        redissonClient = redissonConfig.getClient();
        client = redissonClient.reactive();
    }

    @AfterAll
    void tearDown() {
        if (redissonClient != null) {
            redissonClient.shutdown();
        }
    }
}
