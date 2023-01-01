package com.ulalalab.snipe.infra.manage;

import com.ulalalab.snipe.infra.channel.SpChannelGroup;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.support.ConnectionPoolSupport;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisManager {

    @Value("${spring.redis.host}")
    private String REDIS_HOST;

    @Value("${spring.redis.port}")
    private int REDIS_PORT;

    @Value("${spring.redis.password}")
    private String REDIS_PASSWORD;

    @Value("${spring.redis.lettuce.pool.max-active}")
    private int CONNECTION_COUNT;

    private GenericObjectPool<StatefulRedisConnection<String, String>> pool = null;
    private Map<StatefulRedisConnection<String, String>, Integer> redisConnectionMap = new HashMap<>();
    private Map<RedisAsyncCommands<String, String>, Integer> redisCommandsMap = new HashMap<>();

    @PostConstruct
    private void init() {
        //this.initRedisPool();
        this.initRedisConnectionMap();
        //this.initRedisCommandsMap();
    }

    private GenericObjectPool<StatefulRedisConnection<String, String>> nonClusterPoolUsage() {
        RedisClient client = RedisClient.create(
                RedisURI.builder()
                        .withHost(REDIS_HOST)
                        .withPort(REDIS_PORT)
                        .withPassword(REDIS_PASSWORD)
                        .build());

        client.setOptions(ClientOptions.builder().autoReconnect(true).build());

        return ConnectionPoolSupport.createGenericObjectPool(() -> client.connect(), createPoolConfig());
    }

    private GenericObjectPoolConfig createPoolConfig() {

        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();

        poolConfig.setMaxTotal(100);
        poolConfig.setMaxIdle(50);
        poolConfig.setBlockWhenExhausted(true);
        poolConfig.setMaxWaitMillis(-1);
        poolConfig.setMinIdle(20);

        return poolConfig;
    }

    private void initRedisPool() {
        pool = nonClusterPoolUsage();
    }

    public GenericObjectPool<StatefulRedisConnection<String, String>> getRedisPool() {
        return pool;
    }

//    public StatefulRedisConnection<String, String> getConnection() throws Exception {
//        return pool.borrowObject();
//    }
    private void initRedisConnectionMap() {
        for(int i=0; i<CONNECTION_COUNT; i++) {
            RedisClient redisClient = RedisClient.create(
                    RedisURI.builder()
                            .withHost(REDIS_HOST)
                            .withPort(REDIS_PORT)
                            .withPassword(REDIS_PASSWORD)
                            .build());

            StatefulRedisConnection<String, String> redisConnection = redisClient.connect();
            //redisConnection.setAutoFlushCommands(true);

            redisConnectionMap.put(redisConnection, 0);
        }
    }

    private void initRedisCommandsMap() {
        for(int i=0; i<CONNECTION_COUNT; i++) {
            RedisClient redisClient = RedisClient.create(
                    RedisURI.builder()
                            .withHost(REDIS_HOST)
                            .withPort(REDIS_PORT)
                            .withPassword(REDIS_PASSWORD)
                            .build());

            StatefulRedisConnection<String, String> redisConnection = redisClient.connect();
            RedisAsyncCommands<String, String> commands = redisConnection.async();
            //redisConnection.setAutoFlushCommands(true);

            redisCommandsMap.put(commands, 0);
        }
    }

    public Map<StatefulRedisConnection<String, String>, Integer> getRedisConnectionMap() {
        return redisConnectionMap;
    }

    public Map<RedisAsyncCommands<String, String>, Integer> getRedisCommandsMap() {
        return redisCommandsMap;
    }

    public StatefulRedisConnection<String, String> getRedisConnection() {
        Map.Entry<StatefulRedisConnection<String, String>, Integer> minEntry = null;
        for(Map.Entry<StatefulRedisConnection<String, String>, Integer> entry : this.redisConnectionMap.entrySet()) {
            if(minEntry==null || entry.getValue().compareTo(minEntry.getValue()) < 0) {
                minEntry = entry;
            }
        }
        int count = minEntry.getValue() + 1;
        minEntry.setValue(count);
        StatefulRedisConnection<String, String> redisConnection = minEntry.getKey();

        return redisConnection;
    }

    public RedisAsyncCommands<String, String> getRedisCommands() {
        Map.Entry<StatefulRedisConnection<String, String>, Integer> minEntry = null;
        for(Map.Entry<StatefulRedisConnection<String, String>, Integer> entry : this.redisConnectionMap.entrySet()) {
            if(minEntry==null || entry.getValue().compareTo(minEntry.getValue()) < 0) {
                minEntry = entry;
            }
        }
        int count = minEntry.getValue() + 1;
        minEntry.setValue(count);
        StatefulRedisConnection<String, String> redisConnection = minEntry.getKey();

        return redisConnection.async();
    }
}