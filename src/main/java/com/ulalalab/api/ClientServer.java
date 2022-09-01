package com.ulalalab.api;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import java.nio.charset.Charset;
import java.util.Random;

public class ClientServer {

	public static void main(String[] args) {

		try {
			EventLoopGroup group = new NioEventLoopGroup(30);
			Channel channel;
			ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

			for(int x=0; x<11; x++) {
				Bootstrap bootstrap = new Bootstrap();
				bootstrap.group(group)
						.channel(NioSocketChannel.class)
						.handler(new ChannelInboundHandlerAdapter() {
							@Override
							public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

							}
						});

				channel = bootstrap.connect("127.0.0.1", 38080).sync().channel();
				channelGroup.add(channel);
			}

			while(true) {
				Thread.sleep(1000);

				Random random = new Random();


				//ByteBuf buf = Unpooled.copiedBuffer(s+"#"+System.currentTimeMillis() , Charset.defaultCharset());

				int s = random.nextInt();
				//ByteBuf buf = Unpooled.buffer(4);
				//Unpooled.copyInt(s);

				for(Channel ch : channelGroup) {
					ch.writeAndFlush(Unpooled.copyInt(s));
					//buf.clear();
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}