package com.ulalalab.snipe.infra.handler;

import com.ulalalab.snipe.device.model.ChannelInfo;
import com.ulalalab.snipe.infra.channel.SpChannelGroup;
import com.ulalalab.snipe.infra.manage.EventManager;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@ChannelHandler.Sharable
@Slf4j(topic = "TCP.DefaultHandler")
public class DefaultHandler extends ChannelInboundHandlerAdapter {

	private SpChannelGroup spChannelGroup = EventManager.getInstance().getSpChannelGroup();
	private String deviceId = null;

	// 클라이언트 연결
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();

		// 연결 채널 추가
		spChannelGroup.add(channel);
		spChannelGroup.addChannelInfo(channel.id(), new ChannelInfo());

		log.info("채널 ID : {} / {} 연결 / 연결 갯수 : {}"
				, channel.id()
				, channel.remoteAddress()
				, spChannelGroup.size());

		ctx.fireChannelActive();
	}

	// 클라이언트 연결 해제
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
//ㄱ
//		// 1. 연결 채널 해제
		spChannelGroup.remove(channel.id());

		log.warn("채널 ID : {} / {} 연결 해제 / 연결 갯수 : {}"
				, channel.id()
				, channel.remoteAddress()
				, spChannelGroup.size());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.error("{} -> {}", (StringUtils.hasText(deviceId) ? deviceId : "NoDevice"), cause.getMessage());
		ctx.channel().alloc().buffer().clear();
		ctx.channel().close();
		cause.printStackTrace();
	}
}