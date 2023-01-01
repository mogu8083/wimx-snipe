package com.ulalalab.snipe.infra.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.password}")
    private String password;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(port);
        redisStandaloneConfiguration.setPassword(password);

        LettuceConnectionFactory lettuceConnectionFactory
                = new LettuceConnectionFactory(redisStandaloneConfiguration);

        // false 일 경우 commons-pool을 사용
        //lettuceConnectionFactory.setShareNativeConnection(false);

        return lettuceConnectionFactory;
    }

    //@Bean
//    public LettuceConnectionFactory reactiveConnectionFactory() {
//        LettuceClientConfiguration lettuceClientConfiguration = LettuceClientConfiguration.builder()
//                .commandTimeout(Duration.ofMinutes(1))
//                .shutdownTimeout(Duration.ZERO)
//                .build();
//
//        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(host, port);
//        redisStandaloneConfiguration.setPassword(password);
//        return new LettuceConnectionFactory(redisStandaloneConfiguration, lettuceClientConfiguration);
//    }


//    @Bean
//    public RedisConnection redisConnection() {
//        return redisConnectionFactory().getConnection();
//    }

    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        //redisTemplate.setEnableTransactionSupport(true);
        return redisTemplate;
    }

//    @Bean
//    public ReactiveRedisTemplate<String, String> reactiveRedisTemplate() {
//        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
//
//        RedisSerializationContext<String, String> serializationContext = RedisSerializationContext.<String, String>newSerializationContext(stringRedisSerializer)
//                .value(stringRedisSerializer)
//                .build();
//        return new ReactiveRedisTemplate<>(reactiveConnectionFactory(), serializationContext);
//    }

//    @Bean
//    public RedisConnectionFactory redisConnectionFactory() {
//        return new LettuceConnectionFactory(host, port);
//    }
//
//    @Bean
//    public RedisTemplate<?, ?> redisTemplate() {
//        RedisTemplate<byte[], byte[]> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(redisConnectionFactory());
//        return redisTemplate;
//    }
}

