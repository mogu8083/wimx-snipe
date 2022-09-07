package com.ulalalab.api.common.service;

import com.ulalalab.api.common.handler.DefaultHandler;
import com.ulalalab.api.common.model.Device;
import com.ulalalab.api.common.repository.DeviceRepository;
import com.ulalalab.api.server.EventServer;
import com.ulalalab.api.server.EventServerChecker;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Random;

@Service
@Profile("local")
public class InitService {
	private static final Logger logger = LoggerFactory.getLogger(InitService.class);

	@Autowired
	private EventServer eventServer;

	@Autowired
	private EventServerChecker eventServerChecker;

	@PostConstruct
	public void init() throws Exception {
		logger.info("Init Service");

		eventServerChecker.start();
		eventServer.start();
	}
}