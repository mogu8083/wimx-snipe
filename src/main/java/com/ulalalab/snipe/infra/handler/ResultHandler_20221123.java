//package com.ulalalab.snipe.infra.handler;
//
//import com.ulalalab.snipe.device.model.Device;
//import com.ulalalab.snipe.infra.constant.CommonEnum;
//import com.ulalalab.snipe.infra.manage.ChannelManager;
//import com.ulalalab.snipe.infra.manage.InfluxDBManager;
//import com.ulalalab.snipe.infra.util.BeansUtils;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.ChannelInboundHandlerAdapter;
//import lombok.extern.slf4j.Slf4j;
//import org.influxdb.dto.Point;
//import org.springframework.boot.configurationprocessor.json.JSONObject;
//import org.springframework.data.influxdb.InfluxDBTemplate;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.ValueOperations;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
////@Component
////@ChannelHandler.Sharable
//@Slf4j(topic = "TCP.ResultHandler")
//public class ResultHandler_20221123 extends ChannelInboundHandlerAdapter {
//
//	private ChannelManager channelManager = ChannelManager.getInstance();
//	private boolean deviceSetFlag = false;
//
//	private final RedisTemplate<String, Object> redisTemplate;
//	private final InfluxDBTemplate<Point> influxDBTemplate;
//	private final InfluxDBManager influxDBManager;
//
//	JSONObject redisObject = new JSONObject();
//
//	public ResultHandler_20221123() {
//		this.redisTemplate = (RedisTemplate<String, Object>) BeansUtils.getBean("redisTemplate");
//		this.influxDBManager = (InfluxDBManager) BeansUtils.getBean("influxDBManager");
//		this.influxDBTemplate = (InfluxDBTemplate<Point>) BeansUtils.getBean("influxDBTemplate");
//
//		//this.jdbcTemplate = (JdbcTemplate) BeansUtils.getBean("jdbcTemplate");
//	}
//
//	@Override
//	public void channelRead(ChannelHandlerContext ctx, Object obj) {
//		List<Device> list = null;
//
//		if(obj instanceof List) {
//			list = (List<Device>) obj;
//		} else if(obj instanceof Device) {
//			list = new ArrayList<>();
//			list.add((Device) obj);
//		}
//
//		try {
//			for(Device device : list) {
//				String deviceId = device.getDeviceId();
//
//				if("WX-1Z".equals(device.getDeviceId()) || "WX-1S".equals(device.getDeviceId())) {
//					log.info(device.toString());
//				}
//
//				// 1. InfluxDB Insert
//				Point p = Point.measurement(device.getDeviceId())
//						.time(device.getTime(), TimeUnit.MILLISECONDS)
//						//.tag("test", "default")
//						.addField("ch1", device.getCh1())
//						.addField("ch2", device.getCh2())
//						.addField("ch3", device.getCh3())
//						.addField("ch4", device.getCh4())
//						.addField("ch5", device.getCh5())
//						.addField("insert_time", System.currentTimeMillis())
//						.build();
//
//				// 2. Redis Insert
//				ValueOperations<String, Object> vop = redisTemplate.opsForValue();
//				redisObject.put("ch1", device.getCh1());
//				redisObject.put("ch2", device.getCh2());
//				redisObject.put("ch3", device.getCh3());
//				redisObject.put("ch4", device.getCh4());
//				redisObject.put("ch5", device.getCh5());
//				redisObject.put("time", device.getTime());
////
//				//Device finalDevice = device;
//				ctx.channel().eventLoop().execute(() -> {
//					long startTime = System.nanoTime();
//
//					// InfluxDB
//					influxDBManager.udpWrite(p);
//
//					// Redis
//					vop.set(deviceId, redisObject.toString());
//
//					long endTime = System.nanoTime();
//					double diffTIme = (endTime - startTime)/1000000.0;
//
//					if(deviceId.equals("WX-1Z") || deviceId.equals("WX-1S")) {
//						log.info("Receive [{}, Count : {}, {}ms] => {} / Redis : {} , Influx : {}"
//								, device.getCvtTime(), channelManager.channelSize(), diffTIme, device.getDeviceId(), CommonEnum.SEND.getCode(), CommonEnum.SEND.getCode());
//					}
//				});
//			}
//		} catch(Exception e) {
//			log.error(e.getMessage());
//			e.printStackTrace();
//		} finally {
//			list.clear();
//			//obj = null;
//		}
//	}
//
//	// 수신 데이터 처리 완료
//	@Override
//	public void channelReadComplete(ChannelHandlerContext ctx) {
//		//log.info("{} - Read Complete!!", deviceId);
//		//ctx.writeAndFlush(Unpooled.EMPTY_BUFFER) // 대기중인 메시지를 플러시하고 채널을 닫음
//			//.addListener(ChannelFutureListener.CLOSE);
//		//logger.info(this.getClass() + " => channelReadComplete");
//		//logger.info(this.getClass() + " / channelReadComplete!!");
//	}
//}