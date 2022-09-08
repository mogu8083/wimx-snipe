package com.ulalalab.snipe.common.handler;

import com.ulalalab.snipe.common.util.ByteUtil;
import com.ulalalab.snipe.device.model.Device;
import com.ulalalab.snipe.server.EventServer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.time.LocalDateTime;

@Component
@ChannelHandler.Sharable
public class DataHandler extends ChannelInboundHandlerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(EventServer.class);

	private int DATA_LENGTH = 1024;
	private ByteBuf buff;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	// 핸들러가 생성
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) {
		buff = ctx.alloc().buffer(DATA_LENGTH);
	}

	// 핸들러가 제거될 때 호출되는 메소드
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) {
		buff = null;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object packet) {

		try {
			ByteBuf buf = ((ByteBuf) packet);

			StringBuffer sb = new StringBuffer();
			for(int i=0; i<buf.readableBytes(); i++) {
				sb.append(ByteUtil.byteToHexString(buf.getByte(i)) + " ");
			}
			logger.info("Receive HEX : " + sb.toString());

			LocalDateTime now = LocalDateTime.now();
			int deviceSize = buf.readInt();

			try {
				String deviceId = buf.toString(4, deviceSize, Charset.defaultCharset());
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
				device.setTime(now);
				device.setDeviceId(deviceId);
				device.setCh1(ch1);
				device.setCh2(ch2);
				device.setCh3(ch3);
				device.setCh4(ch4);
				device.setCh5(ch5);

				// 1. Redis Update
				String key = "";
				ValueOperations<String, Object> vop = redisTemplate.opsForValue();
				JSONObject redisObject = new JSONObject();
				redisObject.put("ch1", ch1);
				redisObject.put("ch2", ch2);
				redisObject.put("ch3", ch3);
				redisObject.put("ch4", ch4);
				redisObject.put("ch5", ch5);
				vop.set(deviceId, redisObject.toString());

				// 2. TimscaleDB Update
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
				buf.release();
			} catch(Exception e) {
				logger.error(this.getClass() + " / " + e.getMessage());
				buf.release();
			}
		} catch (Exception e) {
			logger.error(this.getClass() + " / " + e.getMessage());
			e.printStackTrace();
		}
	}

	// 수신 데이터 처리 완료
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.writeAndFlush(Unpooled.EMPTY_BUFFER) // 대기중인 메시지를 플러시하고 채널을 닫음
			.addListener(ChannelFutureListener.CLOSE);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error(this.getClass() + " / " +  cause.getCause());
		cause.printStackTrace();
		ctx.close();
	}
}