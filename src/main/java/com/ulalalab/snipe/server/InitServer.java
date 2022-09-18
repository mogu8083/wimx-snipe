package com.ulalalab.snipe.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Profile("local")
public class InitServer {
	private static final Logger logger = LoggerFactory.getLogger(InitServer.class);

	@Autowired
	private TcpServer tcpServer;

	@Autowired
	private HttpServer httpServer;

//	@Autowired
//	private EventServerChecker eventServerChecker;

	@PostConstruct
	public void init() throws Exception {
		logger.info("Init Server");

		// TCP Server
		tcpServer.start();

		// Http Server
		//httpServer.start();
	}
}