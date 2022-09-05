package com.ulalalab.api;

import com.ulalalab.api.common.util.ByteUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class ClientServer {

	public static void main(String[] args) {

		try {
			EventLoopGroup group = new NioEventLoopGroup();
			Channel channel;
			ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

			for(int x=1; x<1000; x++) {
				Bootstrap bootstrap = new Bootstrap();
				bootstrap.group(group)
						.channel(NioSocketChannel.class)
						.handler(new ChannelInboundHandlerAdapter() {
							@Override
							public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

							}
						});
				int mod = x % 10;
				System.out.println("##@@ " + x);
				System.out.println("##@@ " + mod);
				channel = bootstrap.connect("127.0.0.1", Integer.parseInt("3808"+mod)).sync().channel();
				channelGroup.add(channel);
			}

			while(true) {
				Thread.sleep(1000);

				Random random = new Random();
				int s = random.nextInt();

				String msg = s+"#"+System.currentTimeMillis()+"#"+System.currentTimeMillis()+"#"+System.currentTimeMillis();



				//int s = random.nextInt();
				//ByteBuf buf = Unpooled.buffer(msg.getBytes().length);
				//Unpooled.copyInt(s);

				//Unpooled.copiedBuffer(msg.getBytes());

				//buf.release();

				int i = 1;
				for(Channel ch : channelGroup) {
					Thread.sleep(10);
					ByteBuf buf = Unpooled.buffer();

					String device = ("WX-")+i++;
					buf.writeBytes(ByteUtil.convertIntToByteArray(device.getBytes(Charset.defaultCharset()).length));
					buf.writeBytes(device.getBytes(Charset.defaultCharset()));

					double d = Math.round(Math.random()*100*10)/10.0;
					buf.writeBytes(ByteUtil.convertDoubleToByteArray(d));

					d = Math.round(Math.random()*100*10)/10.0;
					buf.writeBytes(ByteUtil.convertDoubleToByteArray(d));

					d = Math.round(Math.random()*100*10)/10.0;
					buf.writeBytes(ByteUtil.convertDoubleToByteArray(d));

					d = Math.round(Math.random()*100*10)/10.0;
					buf.writeBytes(ByteUtil.convertDoubleToByteArray(d));

					d = Math.round(Math.random()*100*10)/10.0;
					buf.writeBytes(ByteUtil.convertDoubleToByteArray(d));
					ch.writeAndFlush(buf);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}