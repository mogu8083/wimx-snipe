package com.ulalalab.snipe.infra.handler;

import com.ulalalab.snipe.device.model.ChannelInfo;
import com.ulalalab.snipe.device.model.Device;
import com.ulalalab.snipe.infra.constant.CommonEnum;
import com.ulalalab.snipe.infra.manage.ChannelManager;
import com.ulalalab.snipe.infra.manage.InfluxDBManager;
import com.ulalalab.snipe.infra.util.BeansUtils;
import com.ulalalab.snipe.infra.util.DevUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.jdbc.batch.spi.Batch;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.influxdb.InfluxDBTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

//@Component
//@ChannelHandler.Sharable
@Slf4j(topic = "TCP.ResultHandler")
public class ResultHandler extends ChannelInboundHandlerAdapter {

	private ChannelManager channelManager = ChannelManager.getInstance();
	private boolean deviceSetFlag = false;

	private final RedisTemplate<String, Object> redisTemplate;
	private final InfluxDBTemplate<Point> influxDBTemplate;
	private final InfluxDBManager influxDBManager;

	JSONObject redisObject = new JSONObject();

	public ResultHandler() {
		this.redisTemplate = (RedisTemplate<String, Object>) BeansUtils.getBean("redisTemplate");
		this.influxDBManager = (InfluxDBManager) BeansUtils.getBean("influxDBManager");
		this.influxDBTemplate = (InfluxDBTemplate<Point>) BeansUtils.getBean("influxDBTemplate");

		//this.jdbcTemplate = (JdbcTemplate) BeansUtils.getBean("jdbcTemplate");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object obj) {
		Device device = (Device) obj;

		try {
			String deviceId = device.getDeviceId();

			// 1. InfluxDB Insert
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

			// 2. Redis Insert
			ValueOperations<String, Object> vop = redisTemplate.opsForValue();
			redisObject.put("ch1", device.getCh1());
			redisObject.put("ch2", device.getCh2());
			redisObject.put("ch3", device.getCh3());
			redisObject.put("ch4", device.getCh4());
			redisObject.put("ch5", device.getCh5());
			redisObject.put("time", device.getTime());
//
			Device finalDevice = device;
			ctx.channel().eventLoop().execute(() -> {
				long startTime = System.nanoTime();

				// InfluxDB
				influxDBManager.udpWrite(p);
				//influxDBTemplate.write(p);

				// Redis
				//vop.set(deviceId, redisObject.toString());

				long endTime = System.nanoTime();
				double diffTIme = (endTime - startTime)/1000000.0;

				if(DevUtils.isPrint(deviceId)) {
					log.info("Receive [{}, Count : {}, {}ms] => {} / Redis : {} , Influx : {}"
							, finalDevice.getCvtTime(), channelManager.channelSize(), diffTIme, finalDevice.getDeviceId(), CommonEnum.SEND.getCode(), CommonEnum.SEND.getCode());
				}
			});
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