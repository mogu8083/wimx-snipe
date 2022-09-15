package com.ulalalab.snipe.common.service;

import com.ulalalab.snipe.server.EventServer;
import com.ulalalab.snipe.server.EventServerChecker;
import com.ulalalab.snipe.server.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

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