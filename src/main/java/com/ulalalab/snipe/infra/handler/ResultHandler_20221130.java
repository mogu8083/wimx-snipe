//package com.ulalalab.snipe.infra.handler;
//
//import com.ulalalab.snipe.device.model.Device;
//import com.ulalalab.snipe.infra.channel.SpChannelGroup;
//import com.ulalalab.snipe.infra.manage.EventManager;
//import com.ulalalab.snipe.infra.manage.InfluxDBManager;
//import com.ulalalab.snipe.infra.manage.RedisManager;
//import com.ulalalab.snipe.infra.util.BeansUtils;
//import com.ulalalab.snipe.infra.util.DevUtils;
//import com.ulalalab.snipe.server.MainServer;
//import io.lettuce.core.api.StatefulRedisConnection;
//import io.lettuce.core.api.async.RedisAsyncCommands;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.ChannelInboundHandlerAdapter;
//import lombok.extern.slf4j.Slf4j;
//import org.influxdb.dto.Point;
//import org.jetbrains.annotations.NotNull;
//import org.springframework.boot.configurationprocessor.json.JSONObject;
//import org.springframework.data.influxdb.InfluxDBTemplate;
//import org.springframework.data.redis.core.RedisTemplate;
//
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//
////@Component
////@ChannelHandler.Sharable
//@Slf4j(topic = "TCP.ResultHandler")
//public class ResultHandler_20221130 extends ChannelInboundHandlerAdapter {
//
//	private SpChannelGroup spChannelGroup = EventManager.getInstance().getSpChannelGroup();
//	//private Map<StatefulRedisConnection<String, String>, Integer> redisConnectionMap = RedisManager.getInstance().getRedisConnectionMap();
//	private final RedisTemplate<String, Object> redisTemplate;
//	private final InfluxDBTemplate<Point> influxDBTemplate;
//	private final InfluxDBManager influxDBManager;
//	private JSONObject redisObject;
//	private String deviceId = null;
//	private StatefulRedisConnection<String, String> redisConnection = null;
//
//	public ResultHandler_20221130() throws Exception {
//		this.redisTemplate = (RedisTemplate<String, Object>) BeansUtils.getBean("redisTemplate");
//		this.influxDBManager = (InfluxDBManager) BeansUtils.getBean("influxDBManager");
//		this.influxDBTemplate = (InfluxDBTemplate<Point>) BeansUtils.getBean("influxDBTemplate");
//		//this.taskExecutor = (ThreadPoolTaskExecutor) BeansUtils.getBean("taskExecutor");
//		//this.taskScheduler = (ThreadPoolTaskScheduler) BeansUtils.getBean("taskScheduler");
//		//this.jdbcTemplate = (JdbcTemplate) BeansUtils.getBean("jdbcTemplate");
//		redisObject = new JSONObject();
//	}
//
//	@Override
//	public void channelActive(@NotNull ChannelHandlerContext ctx) throws Exception {
//		Map<StatefulRedisConnection<String, String>, Integer> redisConnectionMap = MainServer.redisConnectionMap;
//
//		Map.Entry<StatefulRedisConnection<String, String>, Integer> minEntry = null;
//		for(Map.Entry<StatefulRedisConnection<String, String>, Integer> entry : redisConnectionMap.entrySet()) {
//			if(minEntry==null || entry.getValue().compareTo(minEntry.getValue()) < 0) {
//				minEntry = entry;
//			}
//		}
//		int count = minEntry.getValue() + 1;
//		minEntry.setValue(count);
//		redisConnection = minEntry.getKey();
//
//		log.info(redisConnection.toString());
//		log.info("Redis Count : " + count);
//	}
//
//	@Override
//	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//		redisConnection.close();
//	}
//
//	@Override
//	public void channelRead(ChannelHandlerContext ctx, Object obj) {
//		Device device = (Device) obj;
//
//		try {
//			String deviceId = device.getDeviceId();
//			this.deviceId = deviceId;
//
//			try {
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
//				redisObject.put("ch1", device.getCh1());
//				redisObject.put("ch2", device.getCh2());
//				redisObject.put("ch3", device.getCh3());
//				redisObject.put("ch4", device.getCh4());
//				redisObject.put("ch5", device.getCh5());
//				redisObject.put("time", device.getTime());
//				redisObject.put("cvtTime", device.getCvtTime());
//
//
//				RedisAsyncCommands<String, String> commands = MainServer.redisConnection.async();
//				commands.set(deviceId, redisObject.toString());
//				if(DevUtils.isPrint(deviceId)) {
//					log.info("Redis Execute -> " + device.toString());
//				}
//
//				//Device finalDevice = device;
//
//				// 속도 문제로 주석
////				taskExecutor.execute(() -> {
////					ValueOperations<String, Object> vop = redisTemplate.opsForValue();
////					vop.set(deviceId, redisObject.toString());
////
////					if(DevUtils.isPrint(deviceId)) {
////						log.info("Redis Execute -> " + deviceId + " / Queue Size : " + taskExecutor.getQueueSize());
////					}
////				});
//
////				redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
////
////					//connection.set(keySerializer.serialize(deviceId), keySerializer.serialize(redisObject.toString()));
////					connection.set(keySerializer.serialize(deviceId), valueSerializer.serialize(redisObject.toString()));
////
////					if (DevUtils.isPrint(deviceId)) {
////						log.info("Redis Execute -> " + deviceId);
////					}
////					return null;
////				});
//
//				//Device finalDevice1 = device;
//				//ctx.channel().eventLoop().execute(()-> {
//					//long startTime = System.nanoTime();
//
//				Device finalDevice = device;
//				ctx.channel().eventLoop().execute(() -> {
//					influxDBManager.udpWrite(p);
//
//					if (DevUtils.isPrint(deviceId)) {
//						log.info("InfluxDB Execute -> " + finalDevice.toString());
//					}
//				});
//
////				ctx.channel().eventLoop().execute(() -> {
////					ValueOperations<String, Object> vop = redisTemplate.opsForValue();
////					vop.set(deviceId, redisObject.toString());
////					log.info("Redis Execute -> " + finalDevice.toString());
////				});
//
//				//});
//
////				GenericObjectPool<StatefulRedisConnection<String, String>> redisPool =
////						redisManager.getRedisPool();
////
////				StatefulRedisConnection<String, String> redisConnection = null;
////				try {
////					redisConnection = redisPool.borrowObject();
////					redisConnection.setAutoFlushCommands(true);
////					redisConnection.sync().set(deviceId, redisObject.toString());
////
////					if(DevUtils.isPrint(deviceId)) {
////						//log.info(redisPool.toString());
////						log.info("Redis Execute -> " + deviceId);
////					}
////				} catch (Exception e) {
////					e.printStackTrace();
////				} finally {
////					redisConnection.close();
////					//redisPool.close();
////				}
//			} catch(Exception e) {
//				e.printStackTrace();
//				log.error(e.getMessage());
//			}
//		} catch(Exception e) {
//			log.error(e.getMessage());
//			e.printStackTrace();
//		} finally {
//			device = null;
//			obj = null;
//		}
//	}
//
//	// 수신 데이터 처리 완료
//	@Override
//	public void channelReadComplete(ChannelHandlerContext ctx) {
//
//	}
//
//}