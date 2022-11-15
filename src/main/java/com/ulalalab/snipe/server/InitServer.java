package com.ulalalab.snipe.server;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Slf4j
@Profile({"local-server", "dev-server"})
public class InitServer {

	//@Autowired
	//private TcpServer tcpServer;

	//@Autowired
	//private HttpServer httpServer;

	@Autowired
	private MainServer mainServer;

//	@Autowired
//	private MainServerLocal mainServer;

//	@Autowired
//	private EventServerChecker eventServerChecker;

	@PostConstruct
	public void init() throws Exception {
		log.info("Init Server");

		// Main Server
		mainServer.start();

		// Http Server
		//httpServer.start();
	}
}