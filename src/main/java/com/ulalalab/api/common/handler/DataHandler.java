package com.ulalalab.api.common.handler;

import com.ulalalab.api.common.model.Device;
import com.ulalalab.api.common.repository.DeviceRepository;
import com.ulalalab.api.common.service.DeviceService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.AbstractEventExecutor;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.SingleThreadEventExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;

@Component
@ChannelHandler.Sharable
public class DataHandler extends ChannelInboundHandlerAdapter {

	@Autowired
	private DeviceService deviceService;

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object packet) {
		String readMessage = ((ByteBuf) packet).toString(Charset.defaultCharset());

		try {
			ByteBuf buf = ((ByteBuf) packet);

			int deviceSize = buf.readInt();
			String deviceId = buf.toString(deviceSize, deviceSize, Charset.defaultCharset());
			//
			buf.readBytes(deviceSize);
			Double ch1 = buf.readDouble();
			Double ch2 = buf.readDouble();
			Double ch3 = buf.readDouble();
			Double ch4 = buf.readDouble();
			Double ch5 = buf.readDouble();
			//Double ch6 = buf.readDouble();
			//Double ch7 = buf.readDouble();
			//Double ch8 = buf.readDouble();
			//Double ch9 = buf.readDouble();
			//Double ch10 = buf.readDouble();

			Device device = new Device();
			device.setTime(LocalDateTime.now());
			device.setDeviceId(deviceId);
			device.setCh1(ch1);
			device.setCh2(ch2);
			device.setCh3(ch3);
			device.setCh4(ch4);
			device.setCh5(ch5);

			jdbcTemplate.update("insert into ulalalab_a(time, device_id, ch1, ch2, ch3, ch4, ch5) values(?, ?, ?, ?, ?, ?, ?)"
					, device.getTime()
					, device.getDeviceId()
					, device.getCh1()
					, device.getCh2()
					, device.getCh3()
					, device.getCh4()
					, device.getCh5()
			);

			jdbcTemplate.update("insert into ulalalab_b(time, device_id, ch1, ch2, ch3, ch4, ch5) values(?, ?, ?, ?, ?, ?, ?)"
					, device.getTime()
					, device.getDeviceId()
					, device.getCh1()
					, device.getCh2()
					, device.getCh3()
					, device.getCh4()
					, device.getCh5()
			);
			//System.out.println("##@@ " + device.toString());

			//deviceService.insert(device);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 수신 데이터 처리 완료
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.writeAndFlush(Unpooled.EMPTY_BUFFER) // 대기중인 메시지를 플러시하고 채널을 닫음
				.addListener(ChannelFutureListener.CLOSE);
		System.out.println("##@@ " + "data Handler!!");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}