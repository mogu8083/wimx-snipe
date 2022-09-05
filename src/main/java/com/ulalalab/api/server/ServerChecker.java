//package com.ulalalab.api.server;
//
//import com.ulalalab.api.common.util.LocalDateUtil;
//import com.ulalalab.api.instance.GlobalInstance;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import java.io.IOException;
//import java.nio.channels.SelectionKey;
//import java.util.Map;
//
//public class ServerChecker {
//	private static Logger logger = LogManager.getLogger(ServerChecker.class);
//	private static Map<Client, SelectionKey> clientMap;
//	private static Thread serverThread;
//
//	public void init() throws IOException {
//		clientMap = GlobalInstance.getClientMap();
//
//		this.checkerStart();
//	}
//
//	public void checkerStart() throws IOException {
//		logger.info("##" + Thread.currentThread() + " => Server Checker => " + LocalDateUtil.getNowUTCLocalDateTime());
//
//		while (true) {
//			try {
//				Thread.sleep(5000);
//				logger.info(Thread.currentThread() + " / 현재 클라이언트 접속 : " + clientMap.size());
//
////				if (clientMap.size() == 3) {
////					logger.info(Thread.currentThread() + " / 클라이언트 접속 3개 이상 => 초기화");
////				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
//}