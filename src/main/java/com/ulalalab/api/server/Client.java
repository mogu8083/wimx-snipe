package com.ulalalab.api.server;

import com.ulalalab.api.common.util.LocalDateUtil;
import com.ulalalab.api.instance.GlobalInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class Client {
	private static Logger logger = LogManager.getLogger(Client.class);

	SocketChannel socketChannel;
	Selector selector = GlobalInstance.getSelector();
	Map<Client, SelectionKey> clientMap = GlobalInstance.getClientMap();
	String time = "";
	String connectionIp = "";

	Client(SocketChannel socketChannel) throws IOException {
		socketChannel.configureBlocking(false);
		SelectionKey selectionKey = socketChannel.register(selector, SelectionKey.OP_READ);

		// 접속 정보
		connectionIp = socketChannel.getRemoteAddress().toString();
		time = LocalDateUtil.getLocalDateTimeString(LocalDateUtil.getNowUTCLocalDateTime(), LocalDateUtil.DATE_TIME_FORMAT);

		selectionKey.attach(this);
	}

	/**
	 * 클라이언트 데이터 수신
	 * @param selectionKey
	 */
	public void receive(SelectionKey selectionKey) {
		SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

		try {
			ByteBuffer receiveBuffer = ByteBuffer.allocate(1024);
			Charset charset = StandardCharsets.UTF_8;

			int receiveByteCount = socketChannel.read(receiveBuffer);

			if(receiveByteCount==-1) {
				throw new Exception();
			}
			receiveBuffer.flip();
			String receiveMessage = charset.decode(receiveBuffer).toString().trim();

			logger.info(Thread.currentThread() + " / 클라이언트 IP : " + connectionIp + " / 클라이언트 수신 데이터 : " + receiveMessage + " / : " + connectionIp + " / Receive Message : " + receiveMessage + " / 클라이언트 전체 갯수  : " +  clientMap.size());
		} catch (Exception e) {
			e.printStackTrace();
			this.disconnect(selectionKey);
		}
	}

	/**
	 * 클라이언트 연결 제거
	 * @param selectionKey
	 */
	public void disconnect(SelectionKey selectionKey) {

		try {
			Client client = (Client) selectionKey.attachment();

			logger.info("클라이언트 접속 해제 => " + client.connectionIp);

			// 셀렉션 키 취소
			selectionKey.cancel();

			// 클라이언트 ClientMap Remove
			clientMap.remove(client);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}