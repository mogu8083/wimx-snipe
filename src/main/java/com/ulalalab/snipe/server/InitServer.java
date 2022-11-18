package com.ulalalab.snipe.server;

import com.ulalalab.snipe.infra.util.BeansUtils;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

@Slf4j
@Component
@Profile({"local-server", "dev-server"})
public class InitServer {

	private MainServer mainServer;

	@PostConstruct
	public void init() throws Exception {
		log.info("Init Server");

		boolean isLinux = System.getProperty("os.name").contains("Linux");

		if(isLinux) {
			mainServer = (MainServer<EpollSocketChannel,  EpollServerSocketChannel>) BeansUtils.getBean("mainServer", EpollServerSocketChannel.class);
		} else {
			mainServer = (MainServer<NioSocketChannel, NioServerSocketChannel>) BeansUtils.getBean("mainServer", NioServerSocketChannel.class);
		}

		// Main Server
		mainServer.start();

		// Http Server
		//httpServer.start();
	}
}