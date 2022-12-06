package com.ulalalab.snipe.infra.handler;

import com.ulalalab.snipe.device.model.Device;
import com.ulalalab.snipe.infra.manage.InfluxDBManager;
import com.ulalalab.snipe.infra.manage.RedisManager;
import com.ulalalab.snipe.infra.util.DevUtils;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.reactive.RedisStringReactiveCommands;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.dto.Point;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Component;
import java.util.concurrent.*;

@Component
@Slf4j(topic = "TCP.ResultHandler")
@ChannelHandler.Sharable
@RequiredArgsConstructor
public class ResultHandler extends ChannelInboundHandlerAdapter {

	private StatefulRedisConnection<String, String> redisConnection;
	private RedisStringReactiveCommands<String, String> reactiveCommands;

	// Bean
	private final InfluxDBManager influxDBManager;
	private final RedisManager redisManager;

	private JSONObject redisObject;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		redisConnection = redisManager.getRedisConnection();
		reactiveCommands = redisConnection.reactive();
		redisObject = new JSONObject();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {

	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object obj) {
		Device device = (Device) obj;

		try {
			String deviceId = device.getDeviceId();;;

			try {
				// 1. InfluxDB
				Device finalDevice = device;

				Point p = Point.measurement(deviceId)
						.time(finalDevice.getTime(), TimeUnit.MILLISECONDS)
						//.tag("test", "default")
						.addField("ch1", finalDevice.getCh1())
						.addField("ch2", finalDevice.getCh2())
						.addField("ch3", finalDevice.getCh3())
						.addField("ch4", finalDevice.getCh4())
						.addField("ch5", finalDevice.getCh5())
						.addField("insert_time", System.currentTimeMillis())
						.build();

				ctx.channel().eventLoop().execute(() -> {
					influxDBManager.udpWrite(p);

					if (DevUtils.isPrint(deviceId)) {
						log.info("InfluxDB Execute11 -> " + finalDevice.toString());
					}
				});

				// 2. Redis
				redisObject.put("ch1", device.getCh1());
				redisObject.put("ch2", device.getCh2());
				redisObject.put("ch3", device.getCh3());
				redisObject.put("ch4", device.getCh4());
				redisObject.put("ch5", device.getCh5());
				redisObject.put("time", device.getTime());
				redisObject.put("cvtTime", device.getCvtTime());

				ctx.channel().eventLoop().execute(() -> {
					reactiveCommands.set(deviceId, redisObject.toString()).flux().subscribe();

					if (DevUtils.isPrint(deviceId)) {
						log.info("Redis Execute11 -> " + finalDevice.toString());
					}
				});
			} catch(Exception e) {
				e.printStackTrace();
				log.error(e.getMessage());
			}
		} catch(Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

	// 수신 데이터 처리 완료
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {

	}

	public static void main(String[] args) {
		ByteBuf buf = Unpooled.buffer();
		buf.writeByte(0x16);
		buf.writeByte(0x0c);

		buf.readerIndex(0);

		System.out.println("##@@ " + buf.getByte(0));
	}
}