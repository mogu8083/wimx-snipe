package com.ulalalab.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Random;
import java.util.Set;

@SpringBootTest
@Profile("local")
class ApiApplicationTests {

	@Autowired
	RedisTemplate<String, String> redisTemplate;


	void contextLoads() {
		String key = "";

		// Get
		ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
		key = "test-key";

		valueOperations.set(key, "tttttt");

		// Set
		key = "test-key";
		SetOperations<String, String> setOperations = redisTemplate.opsForSet();
		setOperations.add(key, String.valueOf(new Random().doubles()));

		System.out.println("##@@ " +key);
	}
}
