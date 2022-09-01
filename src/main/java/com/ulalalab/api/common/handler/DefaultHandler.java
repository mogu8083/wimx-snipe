package com.ulalalab.api.common.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

public class DefaultHandler extends ChannelInboundHandlerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(DefaultHandler.class);
	private static Long receive = 0L;

	private static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	// 수신 데이터 처리
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {

		String readMessage = ((ByteBuf) msg).toString(Charset.defaultCharset());

		logger.info("Receive : " + readMessage + " / 받은 갯수 : " + receive++);

		//Channel channel = channelGroup.stream().filter(c -> c==ctx.channel()).findFirst().orElseThrow(ChannelException::new);

		//ByteBuf buf = Unpooled.copiedBuffer("WX-1234#AAAAAAAA", Charset.defaultCharset());
		//channel.write(buf);
	}

	// 수신 데이터 처리 완료
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
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
}