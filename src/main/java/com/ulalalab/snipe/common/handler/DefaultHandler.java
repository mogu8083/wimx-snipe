package com.ulalalab.snipe.common.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class DefaultHandler extends ChannelInboundHandlerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(DefaultHandler.class);
	private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	// 수신 데이터 처리
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object packet) {
		//logger.info("Receive : 클라이언트 Count : " + channelGroup.size() + " / 받은 갯수 : " + receive++);
		//logger.info("Receive Sting : " + packet.toString());
		ctx.fireChannelRead(packet);
	}

	// 장비가 연결 되었을 경우..
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ctx.fireChannelActive();
		//logger.info(ctx.channel().toString() + " 연결 / 연결 갯수 : " + channelGroup.size());
	}

	// 장비가 연결 해제 되었을 경우..
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		channelGroup.remove(channel);
		ctx.close();

		logger.info(ctx.channel().toString() + " 연결 해제 / 연결 갯수 : " + channelGroup.size());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.info(this.getClass() + " / " + cause.getMessage());
		//cause.printStackTrace();
		ctx.close();
	}
}