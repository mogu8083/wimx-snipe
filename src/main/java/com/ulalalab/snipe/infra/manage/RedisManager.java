package com.ulalalab.snipe.infra.manage;

import com.ulalalab.snipe.infra.channel.SpChannelGroup;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.support.ConnectionPoolSupport;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class RedisManager {

    private static RedisManager redisManager;

    private GenericObjectPool<StatefulRedisConnection<String, String>> pool = null;

    static {
        redisManager = new RedisManager();
    }

    private GenericObjectPool<StatefulRedisConnection<String, String>> nonClusterPoolUsage() {
        RedisClient client = RedisClient.create(
                RedisURI.builder()
                        .withHost("10.10.0.90")
                        .withPort(6379)
                        .withPassword("ulalalab12!@")
                        .build());

        client.setOptions(ClientOptions.builder().autoReconnect(true).build());

        return ConnectionPoolSupport.createGenericObjectPool(() -> client.connect(), createPoolConfig());
    }

    private GenericObjectPoolConfig createPoolConfig() {

        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();

        poolConfig.setMaxTotal(100);
        poolConfig.setMaxIdle(50);

        // "true" will result better behavior when unexpected load hits in production
        // "false" makes it easier to debug when your maxTotal/minIdle/etc settings need adjusting.
        poolConfig.setBlockWhenExhausted(true);
        poolConfig.setMaxWaitMillis(-1);
        poolConfig.setMinIdle(20);

        return poolConfig;
    }

    private RedisManager() {
        this.initRedisPool();
    }

    public static RedisManager getInstance() {
        return redisManager;
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
}