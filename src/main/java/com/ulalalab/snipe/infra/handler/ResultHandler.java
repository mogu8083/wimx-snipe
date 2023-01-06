package com.ulalalab.snipe.infra.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ulalalab.snipe.device.model.Device;
import com.ulalalab.snipe.infra.manage.InfluxDBManager;
import com.ulalalab.snipe.infra.manage.RedisManager;
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
		//BatchOptions options = BatchOptions.DEFAULTS;
		//options.flushDuration(5000);
		//influxDB.enableBatch(options);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {

	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object obj) {
		Device device = (Device) obj;

		try {
			String deviceIndex = String.valueOf(device.getDeviceIndex());

			try {
				WeakReference<ObjectMapper> refMapper = new WeakReference<>(new ObjectMapper());
				ObjectMapper mapper = refMapper.get();
				String jsonString = mapper.writeValueAsString(device);

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
					influxDB.write(UDP_PORT, p);

					if (DevUtils.isPrint2(device.getDeviceIndex())) {
						log.info("InfluxDB Execute -> " + finalDevice);
					}
				});

				// 2. Redis
				ctx.executor().execute(() -> {
					reactiveCommands.set(deviceIndex, device.toString()).flux().subscribe();

					if (DevUtils.isPrint2(device.getDeviceIndex())) {
						log.info("Redis Execute -> " + finalDevice);
					}

//					CompletableFuture<Boolean> redisResult = this.saveRedisData(deviceId, jsonString);
//					redisResult.thenAccept(result -> {
//						if (DevUtils.isPrint(deviceId)) {
//							log.info("Redis Execute -> " + finalDevice);
//						}
//					});
				});

//				CompletableFuture<Boolean> redisResult = this.saveRedisData(deviceId, jsonString);
//				redisResult.thenAccept(result -> {
//					if (DevUtils.isPrint(deviceId)) {
//						log.info("Redis Execute22 -> " + finalDevice);
//					}
//				});
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
		//ctx.flush();
	}

//	private CompletableFuture<Boolean> saveInfluxData(String p) {
//		influxDB.write(UDP_PORT, p);
//		//influxDB.flush();
//		return CompletableFuture.completedFuture(true);
//	}
//
//	private CompletableFuture<Boolean> saveRedisData(String key, String value) {
//		reactiveCommands.set(key, value).flux().subscribe();
//		return CompletableFuture.completedFuture(true);
//	}

//	public static void main(String[] args) {
//		ByteBuf buf = Unpooled.buffer();
//		buf.writeByte(0x16);
//		buf.writeByte(0x0c);
//
//		buf.readerIndex(0);
//
//		System.out.println("##@@ " + buf.getByte(0));
//	}

//	public static void main(String[] args) {
//		byte[] b = new byte[4];
//
//		b[0] = (byte) 0x02;
//		b[1] = (byte) 0xF7;
//		b[2] = (byte) 0xF2;
//		b[3] = (byte) 0x02;
//
//		log.info("##@@ " + crc(b));
//	}
//
//	// https://www.scadacore.com/tools/programming-calculators/online-checksum-calculator/
//	private static int crc(byte[] bytes) {
//		int checksum = 0;
//
//		for(byte b : bytes) {
//			checksum += b;
//		}
//		checksum = 256 - checksum;
//		checksum &= 0xFF;
//
//		return checksum;
//	}
}