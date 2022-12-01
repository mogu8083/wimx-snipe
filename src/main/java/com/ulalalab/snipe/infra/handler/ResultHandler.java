package com.ulalalab.snipe.infra.handler;

import com.ulalalab.snipe.device.model.Device;
import com.ulalalab.snipe.infra.manage.InfluxDBManager;
import com.ulalalab.snipe.infra.manage.RedisManager;
import com.ulalalab.snipe.infra.util.DevUtils;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.netty.channel.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.dto.Point;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.influxdb.InfluxDBTemplate;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.lang.ref.WeakReference;
import java.util.concurrent.*;


@Component
//@Scope("prototype")
@Slf4j(topic = "TCP.ResultHandler")
@ChannelHandler.Sharable
@RequiredArgsConstructor
public class ResultHandler extends ChannelInboundHandlerAdapter {

	private StatefulRedisConnection<String, String> redisConnection;

	// Bean
	private final RedisTemplate<String, Object> redisTemplate;
	private final InfluxDBTemplate<Point> influxDBTemplate;
	private final InfluxDBManager influxDBManager;
	private final RedisManager redisManager;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		redisConnection = redisManager.getRedisConnection();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		redisConnection.close();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object obj) {
		Device device = (Device) obj;

		try {
			String deviceId = device.getDeviceId();

			try {
				// 1. InfluxDB
				Point p = Point.measurement(device.getDeviceId())
						.time(device.getTime(), TimeUnit.MILLISECONDS)
						//.tag("test", "default")
						.addField("ch1", device.getCh1())
						.addField("ch2", device.getCh2())
						.addField("ch3", device.getCh3())
						.addField("ch4", device.getCh4())
						.addField("ch5", device.getCh5())
						.addField("insert_time", System.currentTimeMillis())
						.build();

				Device finalDevice = device;
				ctx.channel().eventLoop().execute(() -> {
					influxDBManager.udpWrite(p.lineProtocol());

					if (DevUtils.isPrint(deviceId)) {
						log.info("InfluxDB Execute -> " + finalDevice.toString());
					}
				});

				// 2. Redis
				WeakReference<JSONObject> refRedisObject = new WeakReference<>(new JSONObject());
				JSONObject redisObject = refRedisObject.get();

				redisObject.put("ch1", device.getCh1());
				redisObject.put("ch2", device.getCh2());
				redisObject.put("ch3", device.getCh3());
				redisObject.put("ch4", device.getCh4());
				redisObject.put("ch5", device.getCh5());
				redisObject.put("time", device.getTime());
				redisObject.put("cvtTime", device.getCvtTime());

				RedisAsyncCommands<String, String> commands = redisManager.getRedisConnection().async();
				commands.set(deviceId, redisObject.toString());

				if(DevUtils.isPrint(deviceId)) {
					log.info("Redis Execute -> " + device.toString());
				}
			} catch(Exception e) {
				e.printStackTrace();
				log.error(e.getMessage());
			}
		} catch(Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {
			device = null;
			obj = null;
		}
	}

	// 수신 데이터 처리 완료
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {

	}
}