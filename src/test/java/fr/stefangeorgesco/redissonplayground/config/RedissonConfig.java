package fr.stefangeorgesco.redissonplayground.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.Objects;

public class RedissonConfig {

    private RedissonClient redissonClient;

    public RedissonClient getClient() {
        if (Objects.isNull(redissonClient)) {
            Config config = new Config();
            config.useSingleServer()
                    .setAddress("redis://localhost:6379");
            redissonClient = Redisson.create(config);
        }
        return redissonClient;
    }
}
