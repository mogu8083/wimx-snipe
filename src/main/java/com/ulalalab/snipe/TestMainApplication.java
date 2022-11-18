package com.ulalalab.snipe;

import com.ulalalab.snipe.server.ClientServer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

@Profile("local-client")
@SpringBootApplication
public class TestMainApplication {

	public static void main(String[] args) throws Exception {
		ClientServer clientServer = new ClientServer();
		clientServer.run();
	}
}