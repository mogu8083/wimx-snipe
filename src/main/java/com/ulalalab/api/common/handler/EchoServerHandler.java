package com.ulalalab.api.common.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.Charset;

public class EchoServerHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		String readMessage = ((ByteBuf) msg).toString(Charset.defaultCharset());

		System.out.println("##@@ 수신 문자 : " + readMessage + " / " + Thread.currentThread().getId() + " / " + Thread.currentThread().getName());

		ctx.write(msg);
		ctx.fireChannelRead(msg);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
//		System.out.println("##@@ channelReadComplete");
//		ctx.flush();
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) {
//		System.out.println("##@@ channelReadComplete");
//		ctx.flush();
		System.out.println("##@@ " + ctx.name());
		ctx.co
	}
}