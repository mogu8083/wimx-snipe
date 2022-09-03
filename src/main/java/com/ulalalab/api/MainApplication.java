package com.ulalalab.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class MainApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(MainApplication.class, args);
//		ConfigurableApplicationContext ctx = SpringApplication.run(MainApplication.class, args);
//
//		ctx.getBean(NettyServer.class).init();
	}
}