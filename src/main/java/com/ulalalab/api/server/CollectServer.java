//package com.ulalalab.api.server;
//
//import com.ulalalab.api.common.util.LocalDateUtil;
//import com.ulalalab.api.instance.GlobalInstance;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import java.io.IOException;
//import java.net.InetSocketAddress;
//import java.net.StandardSocketOptions;
//import java.nio.channels.SelectionKey;
//import java.nio.channels.Selector;
//import java.nio.channels.ServerSocketChannel;
//import java.nio.channels.SocketChannel;
//import java.util.Iterator;
//import java.util.Map;
//
////@Component
//public class CollectServer {
//	private static Logger logger = LogManager.getLogger(CollectServer.class);
//
//	@Value("${server.port}")
//	private int serverPort;
//	private boolean collectServerRunFlag = true;
//
//	private static Selector selector;
//	private static Map<Client, SelectionKey> clientMap;
//	private ServerSocketChannel serverSocketChannel;
//
//	public void init() throws IOException {
//
//		// 1. 서버 설정
//		clientMap = GlobalInstance.getClientMap();
//		selector = GlobalInstance.getSelector();
//		//Thread serverThread = GlobalInstance.getServerThread();
//
//		// 2. 수집 서버 실행
//		this.serverStart();
//	}
//
//	/**
//	 * 서버 시작
//	 */
//	public void serverStart() throws IOException {
//		logger.info(serverPort + "##" + Thread.currentThread() + " => Server Start => " + LocalDateUtil.getNowUTCLocalDateTime());
//
//		InetSocketAddress serverAddress = new InetSocketAddress(38080);
//
//		serverSocketChannel = ServerSocketChannel.open();
//		serverSocketChannel.configureBlocking(false);
//
//		serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
//		serverSocketChannel.bind(serverAddress);
//
//		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
//
//		while(true) {
//			System.out.println("##@@ " + "");
//			try {
//				selector.select();
//
//				Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
//
//				while (iterator.hasNext()) {
//					SelectionKey selectionKey = iterator.next();
//					iterator.remove();
//
//					if (selectionKey.isAcceptable()) {
//						this.accept(selectionKey);
//
//						// 수신 데이터
//					} else if(selectionKey.isReadable()) {
//						Client client = (Client) selectionKey.attachment();
//						client.receive(selectionKey);
//
//						// 수신 데이터 작성
//					} else if(selectionKey.isWritable()) {
//
//					}
//				}
//			} catch(Exception e) {
//				e.printStackTrace();
//
//				try {
//					if (serverSocketChannel != null && serverSocketChannel.isOpen()) {
//						serverSocketChannel.close();
//					}
//				} catch (IOException ex) {
//					ex.printStackTrace();
//				} catch (Exception e1) {
//					e1.printStackTrace();
//				}
//				//this.serverStart();
//			}
//		}
//	}
//
//	/**
//	 * 서버 중지
//	 */
//	public void serverStop() throws IOException {
//		collectServerRunFlag = false;
//
//		Iterator<Map.Entry<Client, SelectionKey>> iterator = clientMap.entrySet().iterator();
//
//		while(iterator.hasNext()) {
//			Map.Entry<Client, SelectionKey> entry = iterator.next();
//			Client client = entry.getKey();
//			SelectionKey selectionKey = entry.getValue();
//
//			if(client.socketChannel!=null) {
//				logger.info(client + " => close()");
//				client.socketChannel.close();
//			}
//			iterator.remove();
//		}
//
//		if (serverSocketChannel != null && serverSocketChannel.isOpen()) {
//			serverSocketChannel.close();
//		}
//
//		if (selector != null && selector.isOpen()) {
//			selector.close();
//		}
//	}
//
//	/**
//	 * 서버 재시작
//	 */
//	public void serverRestart() throws IOException {
//		this.serverStop();
//		this.serverStart();
//	}
//
//	// 채널 수신
//	private void accept(SelectionKey selectionKey) throws IOException {
//		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
//
//		try {
//			SocketChannel socketChannel = serverSocketChannel.accept();
//			Client client = new Client(socketChannel);
//			clientMap.put(client, selectionKey);
//			logger.info("클라이언트 접속 => 클라이언트 전체 갯수: " + clientMap.size() + " / " + socketChannel.getRemoteAddress());
//		} catch (Exception e) {
//			e.printStackTrace();
//			try {
//				if (serverSocketChannel != null && serverSocketChannel.isOpen()) {
//					serverSocketChannel.close();
//				}
//			} catch (IOException ex) {
//				ex.printStackTrace();
//			}
//		}
//	}
//}