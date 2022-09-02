package com.ulalalab.api.common.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultHandler extends ChannelInboundHandlerAdapter {

//	public static DeviceRepository deviceRepository;
//	public static EntityManager entityManager;

//	@Autowired
//	public void setDeviceRepository(DeviceRepository deviceRepository) {
//		DefaultHandler.deviceRepository = deviceRepository;
//	}
//
//	@Autowired
//	public void setEntityManager(EntityManager entityManager) {
//		DefaultHandler.entityManager = entityManager;
//	}
	private static final Logger logger = LoggerFactory.getLogger(DefaultHandler.class);
	private static Long receive = 0L;
	private static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	// 수신 데이터 처리
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object packet) {
		logger.info("Receive : 클라이언트 Count : " + channelGroup.size() + " / 받은 갯수 : " + receive++);
		ctx.fireChannelRead(packet);
	}

	// 수신 데이터 처리 완료
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		System.out.println("##@@ " + "complete!!");
		ctx.flush();
	}

	// 장비가 연결 되었을 경우..
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		channelGroup.add(channel);

		System.out.println("##@@ " + ctx.channel().toString() + " 연결 / 연결 갯수 : " + channelGroup.size());
	}

	// 장비가 연결 해제 되었을 경우..
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		channelGroup.remove(channel);

		System.out.println("##@@ " + ctx.channel().toString() + " 연결 해제 / 연결 갯수 : " + channelGroup.size());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		super.exceptionCaught(ctx, cause);
		ctx.close();
	}
}