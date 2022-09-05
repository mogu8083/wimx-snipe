//package com.ulalalab.api.service;
//
//import com.ulalalab.api.server.CollectServer;
//import com.ulalalab.api.server.ServerChecker;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.springframework.stereotype.Component;
//import javax.annotation.PostConstruct;
//import java.io.IOException;
//
//@Component
//public class InitService {
//	private static Logger logger = LogManager.getLogger(InitService.class);
//
//	//@PostConstruct
//	private void init() throws IOException {
//		logger.info("## 서비스 Init ##");
//
//		new Thread(() -> {
//			try {
//				new CollectServer().init();
//			} catch (Exception e) {
//				throw new RuntimeException(e);
//			}
//		}).start();
//
//		new Thread(() -> {
//			try {
//				new ServerChecker().init();
//			} catch (Exception e) {
//				throw new RuntimeException(e);
//			}
//		}).start();
//	}
//}