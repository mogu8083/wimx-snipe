package com.ulalalab.snipe.infra.handler;

import com.ulalalab.snipe.device.model.ChannelInfo;
import com.ulalalab.snipe.device.model.Device;
import com.ulalalab.snipe.infra.constant.CommonEnum;
import com.ulalalab.snipe.infra.manage.ChannelManager;
import com.ulalalab.snipe.infra.manage.InfluxDBManager;
import com.ulalalab.snipe.infra.util.BeansUtils;
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
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

//@Component
//@ChannelHandler.Sharable
@Slf4j(topic = "TCP.ResultHandler")
public class ResultHandler extends ChannelInboundHandlerAdapter {

	private ChannelManager channelManager = ChannelManager.getInstance();
	private boolean deviceSetFlag = false;

	private RedisTemplate<String, Object> redisTemplate;
	private InfluxDBTemplate<Point> influxDBTemplate;
	private InfluxDBManager influxDBManager;
	private String deviceId = null;
	//private List<Point> pointList = new ArrayList<>();

	JSONObject redisObject = new JSONObject();

	public ResultHandler() {
		this.redisTemplate = (RedisTemplate<String, Object>) BeansUtils.getBean("redisTemplate");
		this.influxDBManager = (InfluxDBManager) BeansUtils.getBean("influxDBManager");
		this.influxDBTemplate = (InfluxDBTemplate<Point>) BeansUtils.getBean("influxDBTemplate");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object obj) {

		List<Device> list = (List<Device>) obj;

		//BatchPoints batchPoints = influxDBManager.getBatchPoints();

		try {
			for(Device device : list) {
				ChannelInfo channelInfo = channelManager.getChannelInfo(ctx.channel());

				if(!deviceSetFlag) {
					Channel channel = ctx.channel();

					channelInfo.setDeviceId(device.getDeviceId());
					channelInfo.setRemoteAddress(channel.remoteAddress().toString());
					channelInfo.setConnectTime(LocalDateTime.now());
					channelInfo.setLocalAddress(channel.localAddress().toString());
					channelInfo.setHandlerList(channel.pipeline().names()
							.stream().filter(c -> !c.contains("TailContext")).collect(Collectors.toList()));

					this.deviceSetFlag = true;
				} else {
					channelInfo.setLastPacketTime(LocalDateTime.now());
				}
				String deviceId = device.getDeviceId();

				if(!StringUtils.hasText(deviceId)) {
					continue;
				}

				if(deviceId==null) {
					DefaultHandler defaultHandler = (DefaultHandler) ctx.channel().pipeline().get("TCP.DefaultHandler");

					defaultHandler.deviceId = device.getDeviceId();
					this.deviceId = device.getDeviceId();
				}

				if("WX-1Z".equals(device.getDeviceId()) || "WX-1A".equals(device.getDeviceId())) {
					log.info(device.toString());
				}

	//			if (invocable != null) {
	//				Double cvCh1 = device.getCh1();
	//				Double cvCh2 = device.getCh2();
	//				Double cvCh3 = device.getCh3();
	//				Double cvCh4 = device.getCh4();
	//				Double cvCh5 = device.getCh5();
	//
	//				cvCh1 = (Double) invocable.invokeFunction("add", cvCh1);
	//				cvCh2 = (Double) invocable.invokeFunction("add", cvCh2);
	//				cvCh3 = (Double) invocable.invokeFunction("add", cvCh3);
	//				cvCh4 = (Double) invocable.invokeFunction("add", cvCh4);
	//				cvCh5 = (Double) invocable.invokeFunction("add", cvCh5);
	//
	//				device.setCh1(cvCh1);
	//				device.setCh2(cvCh2);
	//				device.setCh3(cvCh3);
	//				device.setCh4(cvCh4);
	//				device.setCh5(cvCh5);
	//
	//				if(cvCh1.isNaN()) {
	//					throw new ScriptException("isNaN");
	//				}
	//			}

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



				//pointList.add(p);
				//influxDBManager.udpWrite(p);
				//influxDBManager.write(p);
				//influxDBTemplate.write(p);
				//batchPoints.point(p);

				//pointList.add(p);
				//AtomicReference<CommonEnum> redisSendEnum = new AtomicReference<>(CommonEnum.NOT_SEND);
				//AtomicReference<CommonEnum> influxSendEnum = new AtomicReference<>(CommonEnum.NOT_SEND);
	//
//				try {
//	//				for (Point point : pointList) {
//						//influxDBManager.udpWrite(point);
//						//influxDBManager.write(point);
//	//				}
//					redisSendEnum.set(CommonEnum.SEND);
//	//				pointList.clear();
//				} catch (Exception e) {
//					e.printStackTrace();
//					log.error("InfluxDB Exception : {}, {} => {} / pointList : {}", device.getCvtTime(), deviceId, e.getMessage(), pointList.size());
//				}

				// 2. Redis Insert
				ValueOperations<String, Object> vop = redisTemplate.opsForValue();
				redisObject.put("ch1", device.getCh1());
				redisObject.put("ch2", device.getCh2());
				redisObject.put("ch3", device.getCh3());
				redisObject.put("ch4", device.getCh4());
				redisObject.put("ch5", device.getCh5());
				redisObject.put("time", device.getTime());

				ctx.channel().eventLoop().execute(() -> {
					long startTime = System.nanoTime();

					// InfluxDB
					influxDBManager.udpWrite(p);

					// Redis
					vop.set(deviceId, redisObject.toString());

					long endTime = System.nanoTime();
					double diffTIme = (endTime - startTime)/1000000.0;

					if(deviceId.equals("WX-1Z") || deviceId.equals("WX-1A")) {
						log.info("Receive [{}, Count : {}, {}ms] => {} / Redis : {} , Influx : {}"
								, device.getCvtTime(), channelManager.channelSize(), diffTIme, device.getDeviceId(), CommonEnum.SEND.getCode(), CommonEnum.SEND.getCode());
					}
				});
			}
//			ctx.channel().eventLoop().execute(() -> {
//				for(Point p : pointList) {
//					influxDBManager.udpWrite(p);
//				}
//				//pointList.clear();
//			});
		} catch(Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {
			// 참조 해체
			//ReferenceCountUtil.release(packet);
			//ctx.alloc().heapBuffer().release();
//			ReferenceCountUtil.release(object);
//			packet = null;
			list.clear();
			obj = null;
		}
	}

	// 수신 데이터 처리 완료
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		//log.info("{} - Read Complete!!", deviceId);
		//ctx.writeAndFlush(Unpooled.EMPTY_BUFFER) // 대기중인 메시지를 플러시하고 채널을 닫음
			//.addListener(ChannelFutureListener.CLOSE);
		//logger.info(this.getClass() + " => channelReadComplete");
		//logger.info(this.getClass() + " / channelReadComplete!!");
	}
}