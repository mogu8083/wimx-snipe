package com.ulalalab.snipe.infra.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ulalalab.snipe.device.model.Device;
import com.ulalalab.snipe.infra.manager.InfluxDBManager;
import com.ulalalab.snipe.infra.manager.RedisManager;
import com.ulalalab.snipe.infra.util.DevUtils;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.reactive.RedisStringReactiveCommands;
import io.netty.channel.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.influxdb.InfluxDBTemplate;
import org.springframework.stereotype.Component;
import java.lang.ref.WeakReference;
import java.time.Instant;
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
	private final InfluxDBTemplate influxDBTemplate;
	private final RedisManager redisManager;

	private InfluxDB influxDB;

	@Value("${spring.influxdb.udp-port}")
	private int UDP_PORT;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		redisConnection = redisManager.getRedisConnection();
		reactiveCommands = redisConnection.reactive();

		influxDB = influxDBTemplate.getConnection();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object obj) throws Exception{
		Device device = (Device) obj;
		WeakReference<ObjectMapper> refMapper = new WeakReference<>(new ObjectMapper());
		ObjectMapper mapper = refMapper.get();

		if(DevUtils.isPrint(device.getDeviceIndex())) {
			log.info("PrevDevice : {}", mapper.writeValueAsString(device));
		}

		try {
			String deviceIndex = String.valueOf(device.getDeviceIndex());

			try {
				// 1. InfluxDB
				Device finalDevice = device;

				Point.Builder builder = Point.measurement(deviceIndex)
						.time(finalDevice.getTimestamp(), TimeUnit.SECONDS);

				for(int i = 0; i < device.getChannelDataList().size(); i++) {
					Float channelData = device.getChannelDataList().get(i);
					builder.addField("ch" + (i+1), channelData);
				}
				builder.addField("insert_time", (int) Instant.now().getEpochSecond());

				Point p = builder.build();

				ctx.executor().execute(() -> {
					try {
						influxDB.write(UDP_PORT, p);
					} catch (Exception e) {
						log.error(e.getMessage());
					}
				});

				String deviceJson = mapper.writeValueAsString(device);
				// 2. Redis
				ctx.executor().execute(() -> {
					try {
						reactiveCommands.set(deviceIndex, deviceJson).flux().subscribe();
					} catch(Exception e) {
						log.error(e.getMessage());
					}
//					if (DevUtils.isPrint(device.getDeviceIndex())) {
//						log.info("Redis Execute -> " + device.getDeviceIndex());
//					}
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
}