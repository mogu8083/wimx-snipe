package com.ulalalab.snipe.server;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

@Slf4j
@Component
@Profile({"local-server", "dev-server"})
@RequiredArgsConstructor
public class InitServer {

	private final TcpServer tcpServer;
	private final HttpServer httpServer;

	@PostConstruct
	public void init() throws Exception {
		log.info("## Init Server ##");

		// Tcp Server
		tcpServer.start();

		// Http Server
		httpServer.start();
	}
}