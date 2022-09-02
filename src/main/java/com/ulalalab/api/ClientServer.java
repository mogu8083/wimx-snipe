package com.ulalalab.api;

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

			for(int x=0; x<1; x++) {
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

//			while(true) {
//				Thread.sleep(1000);

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
					ByteBuf buf = Unpooled.buffer();

					String device = ("WX-")+i++;
					buf.writeBytes(convertIntToByteArray(device.getBytes().length));
					buf.writeBytes(device.getBytes());
					buf.writeBytes(convertDoubleToByteArray(41.1));
					buf.writeBytes(convertDoubleToByteArray(42.1));
					buf.writeBytes(convertDoubleToByteArray(43.1));
					buf.writeBytes(convertDoubleToByteArray(44.1));
					buf.writeBytes(convertDoubleToByteArray(45.1));
					buf.writeBytes(convertDoubleToByteArray(46.1));
					buf.writeBytes(convertDoubleToByteArray(47.1));
					buf.writeBytes(convertDoubleToByteArray(48.1));
					buf.writeBytes(convertDoubleToByteArray(49.1));
					buf.writeBytes(convertDoubleToByteArray(50.1));
					ch.writeAndFlush(buf);
					//buf.clear();
				}
//			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private static byte[] convertDoubleToByteArray(double number) {
		ByteBuffer byteBuffer = ByteBuffer.allocate(Double.BYTES);
		byteBuffer.putDouble(number);
		return byteBuffer.array();
	}

	private static byte[] convertIntToByteArray(int number) {
		ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.BYTES);
		byteBuffer.putInt(number);
		return byteBuffer.array();
	}
}