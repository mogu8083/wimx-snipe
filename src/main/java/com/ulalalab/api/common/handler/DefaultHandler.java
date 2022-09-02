package com.ulalalab.api.common.handler;

import com.ulalalab.api.common.model.Device;
import com.ulalalab.api.common.repository.DeviceRepository;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.time.LocalDateTime;

@Component
@ChannelHandler.Sharable
public class DefaultHandler extends ChannelInboundHandlerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(DefaultHandler.class);
	private static Long receive = 0L;

	@Autowired
	private DeviceRepository deviceRepository;

	private static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	// 수신 데이터 처리
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object packet) {

		String readMessage = ((ByteBuf) packet).toString(Charset.defaultCharset());

		logger.info("Receive : " + readMessage + " / 클라이언트 Count : " + channelGroup.size() + " / 받은 갯수 : " + receive++);

		ByteBuf buf = ((ByteBuf) packet);

//		while(buf.isReadable()) {
//
//			System.out.println("##@@ " + buf.getByte(0));
//		}

//		byte[] result = new byte[buf.readableBytes()];
//
//
//		byte[] byteArray4 = new byte[4];
//
//		byteArray4[0] = buf.readByte();
//		byteArray4[1] = buf.readByte();
//		byteArray4[2] = buf.readByte();
//		byteArray4[3] = buf.readByte();

		int deviceSize = buf.readInt();
		String deviceId = buf.toString(deviceSize, deviceSize, Charset.defaultCharset());

		Double ch1 = buf.readDouble();
		Double ch2 = buf.readDouble();
		Double ch3 = buf.readDouble();
		Double ch4 = buf.readDouble();
		Double ch5 = buf.readDouble();
		Double ch6 = buf.readDouble();
		Double ch7 = buf.readDouble();
		Double ch8 = buf.readDouble();
		Double ch9 = buf.readDouble();
		Double ch10 = buf.readDouble();

		Device device = new Device();
		device.setTime(LocalDateTime.now());
		device.setDeviceId(deviceId);
		device.setCh1(ch1);
		device.setCh2(ch2);
		device.setCh3(ch3);
		device.setCh4(ch4);
		device.setCh5(ch5);
		device.setCh6(ch6);
		device.setCh7(ch7);
		device.setCh8(ch8);
		device.setCh9(ch9);
		device.setCh10(ch10);

		System.out.println("##@@ " + device.toString());

		deviceRepository.save(device);
	}

	// 수신 데이터 처리 완료
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	// 장비가 연결 되었을 경우..
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		channelGroup.add(channel);

		System.out.println("##@@ " + ctx.channel().toString() + " 연결 / 연결 갯수 : " + channelGroup.size());
	}

	// 장비가 연결 해제 되었을 경우..
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		channelGroup.remove(channel);

		System.out.println("##@@ " + ctx.channel().toString() + " 연결 해제 / 연결 갯수 : " + channelGroup.size());
	}
}