package com.ulalalab.snipe.infra.handler;

import com.ulalalab.snipe.infra.constant.ProtocolEnum;
import com.ulalalab.snipe.infra.manage.ChannelManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
<<<<<<< HEAD
=======
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
>>>>>>> e9804688e6756cd7b5dabf9696af8a119a5a5914
import java.util.Set;

@Slf4j(topic = "TCP.DefaultHandler")
public class DefaultHandler extends ChannelInboundHandlerAdapter {

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
			log.info("{} 연결 !! / 연결 갯수 : {}", ctx.channel().remoteAddress(), channelGroup.size());
		} else if(protocolEnum==ProtocolEnum.HTTP) {
<<<<<<< HEAD
			log.info("{} Http 연결 !!", ctx.channel().remoteAddress());
=======
			log.info(ctx.channel().remoteAddress() + " Http 연결 !!");
>>>>>>> e9804688e6756cd7b5dabf9696af8a119a5a5914
		}
	}

	// 클라이언트 연결 해제
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		if(protocolEnum==ProtocolEnum.TCP) {
			Channel channel = ctx.channel();
			channelGroup.remove(channel);
			log.info("{} 연결 해제 !! / 연결 갯수 : {}", ctx.channel().remoteAddress(), channelGroup.size());
		} else {
<<<<<<< HEAD
			log.info("{} Http 연결 해제 !!", ctx.channel().remoteAddress());
=======
			log.info("{} -> Http 연결 해제 !!", ctx.channel().remoteAddress());
>>>>>>> e9804688e6756cd7b5dabf9696af8a119a5a5914
		}
		ctx.close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.error(cause.getMessage());
		ctx.close();
	}
}