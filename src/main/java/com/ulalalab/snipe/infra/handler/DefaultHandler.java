package com.ulalalab.snipe.infra.handler;

import com.ulalalab.snipe.infra.constant.ProtocolEnum;
import com.ulalalab.snipe.infra.manage.ChannelManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

//@ChannelHandler.Sharable
@Slf4j(topic = "TCP.DefaultHandler")
public class DefaultHandler extends ChannelInboundHandlerAdapter {

	private static ChannelManager channelManager = ChannelManager.getInstance();
	public String deviceId = null;

	// 클라이언트 연결
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// TODO : 1. 인증 관련 추가
		//////////////////////////

		// 2. 연결 채널 추가
		Channel channel = ctx.channel();
		channelManager.addChannel(channel);

		log.info("{} 연결 !! / 연결 갯수 : {}", ctx.channel().remoteAddress(), channelManager.channelSize());
	}

	// 클라이언트 연결 해제
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		channelManager.removeChannel(channel);

		log.info("{} 연결 해제 !! / 연결 갯수 : {}",
				StringUtils.hasText(deviceId) ? deviceId : "NoDevice" , channelManager.channelSize());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.warn("{} -> {}", (StringUtils.hasText(deviceId) ? deviceId : "NoDevice"), cause.getMessage());
		ctx.channel().alloc().buffer().clear();
		ctx.channel().close();
		cause.printStackTrace();
	}
}