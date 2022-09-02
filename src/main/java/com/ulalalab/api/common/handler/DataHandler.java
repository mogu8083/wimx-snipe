package com.ulalalab.api.common.handler;

import com.ulalalab.api.common.model.Device;
import com.ulalalab.api.common.repository.DeviceRepository;
import com.ulalalab.api.common.service.DeviceService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.AbstractEventExecutor;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.SingleThreadEventExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;

@Component
public class DataHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object packet) {
		String readMessage = ((ByteBuf) packet).toString(Charset.defaultCharset());

		try {
			ByteBuf buf = ((ByteBuf) packet);

			int deviceSize = buf.readInt();
			String deviceId = buf.toString(deviceSize, deviceSize, Charset.defaultCharset());
			//
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

			ctx.executor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 수신 데이터 처리 완료
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		System.out.println("##@@ " + "data Handler!!");
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		super.exceptionCaught(ctx, cause);
		ctx.close();
	}
}