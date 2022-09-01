package com.ulalalab.api.common.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.boot.SpringApplication;

public class DiscardServerHandler extends SimpleChannelInboundHandler<Object> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("##@@ -> " + Thread.currentThread());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		ctx.close();
	}
}