package com.ulalalab.snipe.infra.handler;

import com.ulalalab.snipe.infra.constant.ProtocolEnum;
import com.ulalalab.snipe.infra.manage.ChannelManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Set;

public class DefaultHandler extends ChannelInboundHandlerAdapter {

	private final Logger logger = LoggerFactory.getLogger("TCP.DefaultHandler");
	private static Set<Channel> channelGroup = ChannelManager.getInstance();
	private final ProtocolEnum protocolEnum;

	public DefaultHandler(ProtocolEnum protocolEnum) {
		this.protocolEnum = protocolEnum;
	}

	// 클라이언트 연결
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		if(protocolEnum==ProtocolEnum.TCP) {
			channelGroup.add(ctx.channel());
			logger.info(ctx.channel().remoteAddress() + " 연결 !! / 연결 갯수 : " + channelGroup.size());
		} else if(protocolEnum==ProtocolEnum.HTTP) {
			logger.info(ctx.channel().remoteAddress() + " Http 연결 !!");
		}
	}

	// 클라이언트 연결 해제
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		if(protocolEnum==ProtocolEnum.TCP) {
			Channel channel = ctx.channel();
			channelGroup.remove(channel);
			logger.info(ctx.channel().remoteAddress() + " 연결 해제 !! / 연결 갯수 : " + channelGroup.size());
		} else {
			logger.info(ctx.channel().remoteAddress() + " Http 연결 해제 !!");
		}
		ctx.close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error(cause.getMessage());
		ctx.close();
	}
}