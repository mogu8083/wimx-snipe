package com.ulalalab.snipe.infra.handler;

import com.ulalalab.snipe.infra.constant.ProtocolEnum;
import com.ulalalab.snipe.infra.manage.ChannelManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Set;

@Slf4j(topic = "TCP.DefaultHandler")
public class DefaultHandler extends ChannelInboundHandlerAdapter {

	//private static Set<Channel> channelGroup = ChannelManager.getInstance();
	private ChannelManager channelManager = ChannelManager.getInstance();
	private final ProtocolEnum protocolEnum;

	public String deviceId;

	public DefaultHandler(ProtocolEnum protocolEnum) {
		this.protocolEnum = protocolEnum;
	}


	// 클라이언트 연결
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		if(protocolEnum==ProtocolEnum.TCP) {
			//channelGroup.add(ctx.channel());
			//channelManager.addChannel(ctx.channel());
			log.info("{} 연결 !! / 연결 갯수 : {}", ctx.channel().remoteAddress(), channelManager.channelSize());
		} else if(protocolEnum==ProtocolEnum.HTTP) {
			log.info("{} Http 연결 !!", ctx.channel().remoteAddress());
		}
	}

	// 클라이언트 연결 해제
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//		if(protocolEnum==ProtocolEnum.TCP) {
//			Channel channel = ctx.channel();
//			channelManager.removeChannel(channel);
//
//			log.info("{} 연결 해제 !! / 연결 갯수 : {}",
//					StringUtils.hasText(deviceId) ? deviceId : "NoDevice" , channelManager.channelSize());
//		} else {
//			log.info("{} Http 연결 해제 !!", ctx.channel().remoteAddress());
//		}
//		ctx.close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.warn("{} -> {}", (StringUtils.hasText(deviceId) ? deviceId : "NoDevice"), cause.getMessage());
		//ctx.close();
	}
}