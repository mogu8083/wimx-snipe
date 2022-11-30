package com.ulalalab.snipe.infra.handler;

import com.ulalalab.snipe.device.model.ChannelInfo;
import com.ulalalab.snipe.device.model.Device;
import com.ulalalab.snipe.infra.channel.SpChannelGroup;
import com.ulalalab.snipe.infra.constant.CommonEnum;
import com.ulalalab.snipe.infra.manage.ChannelManager;
import com.ulalalab.snipe.infra.manage.EventManager;
import com.ulalalab.snipe.infra.manage.InfluxDBManager;
import com.ulalalab.snipe.infra.manage.RedisManager;
import com.ulalalab.snipe.infra.util.BeansUtils;
import com.ulalalab.snipe.infra.util.DevUtils;
import com.ulalalab.snipe.server.MainServer;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisStringCommands;
import io.lettuce.core.support.ConnectionPoolSupport;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.hibernate.engine.jdbc.batch.spi.Batch;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.dao.DataAccessException;
import org.springframework.data.influxdb.InfluxDBTemplate;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

//@Component
//@ChannelHandler.Sharable
@Slf4j(topic = "TCP.ResultHandler")
public class ResultHandler extends ChannelInboundHandlerAdapter {

	//private ChannelManager channelManager = ChannelManager.getInstance();
	//private ExecutorService executorService = EventManager.getInstance().getExecutorService();
	private SpChannelGroup spChannelGroup = EventManager.getInstance().getSpChannelGroup();

	//private ThreadPoolTaskExecutor taskExecutor;
	//private static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	Queue<Map<String, String>>queue = new LinkedList<>();
	//private GenericObjectPool<StatefulRedisConnection<String, String>> redisPool = RedisManager.getInstance().getRedisPool();
	private RedisManager redisManager = RedisManager.getInstance();
	//private StatefulRedisConnection<String, String> redisConnection = RedisManager.getInstance().getConnection();

	private final RedisTemplate<String, Object> redisTemplate;
	private final InfluxDBTemplate<Point> influxDBTemplate;
	private final InfluxDBManager influxDBManager;
	private final ThreadPoolTaskExecutor taskExecutor;
	private final ThreadPoolTaskScheduler taskScheduler;
	JSONObject redisObject;
	String deviceId = null;
	RedisClient redisClient = null;
	StatefulRedisConnection<String, String> redisConnection = null;

	public ResultHandler() throws Exception {
		this.redisTemplate = (RedisTemplate<String, Object>) BeansUtils.getBean("redisTemplate");
		this.influxDBManager = (InfluxDBManager) BeansUtils.getBean("influxDBManager");
		this.influxDBTemplate = (InfluxDBTemplate<Point>) BeansUtils.getBean("influxDBTemplate");
		this.taskExecutor = (ThreadPoolTaskExecutor) BeansUtils.getBean("taskExecutor");
		this.taskScheduler = (ThreadPoolTaskScheduler) BeansUtils.getBean("taskScheduler");
		//this.jdbcTemplate = (JdbcTemplate) BeansUtils.getBean("jdbcTemplate");
		redisObject = new JSONObject();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object obj) {
		Device device = (Device) obj;

		try {
			String deviceId = device.getDeviceId();
			this.deviceId = deviceId;

			try {
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
				redisObject.put("ch1", device.getCh1());
				redisObject.put("ch2", device.getCh2());
				redisObject.put("ch3", device.getCh3());
				redisObject.put("ch4", device.getCh4());
				redisObject.put("ch5", device.getCh5());
				redisObject.put("time", device.getTime());
				redisObject.put("cvtTime", device.getCvtTime());

				ctx.channel().eventLoop().execute(()-> {
					RedisAsyncCommands<String, String> commands = MainServer.redisConnection.async();
					commands.set(deviceId, redisObject.toString());
					if(DevUtils.isPrint(deviceId)) {
						log.info("Redis Execute -> " + deviceId);
					}
				});

				//Device finalDevice = device;

				// 속도 문제로 주석
//				taskExecutor.execute(() -> {
//					ValueOperations<String, Object> vop = redisTemplate.opsForValue();
//					vop.set(deviceId, redisObject.toString());
//
//					if(DevUtils.isPrint(deviceId)) {
//						log.info("Redis Execute -> " + deviceId + " / Queue Size : " + taskExecutor.getQueueSize());
//					}
//				});

//				redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
//
//					//connection.set(keySerializer.serialize(deviceId), keySerializer.serialize(redisObject.toString()));
//					connection.set(keySerializer.serialize(deviceId), valueSerializer.serialize(redisObject.toString()));
//
//					if (DevUtils.isPrint(deviceId)) {
//						log.info("Redis Execute -> " + deviceId);
//					}
//					return null;
//				});

				ctx.channel().eventLoop().execute(()-> {
					//long startTime = System.nanoTime();

					influxDBManager.udpWrite(p);
					if(DevUtils.isPrint(deviceId)) {
						log.info("InfluxDB Execute -> " + deviceId);
					}

//					ValueOperations<String, Object> vop = redisTemplate.opsForValue();
//					vop.set(deviceId, redisObject.toString());

					//long endTime = System.nanoTime();
					//double diffTIme = (endTime - startTime) / 1000000.0;
				});

//				GenericObjectPool<StatefulRedisConnection<String, String>> redisPool =
//						redisManager.getRedisPool();
//
//				StatefulRedisConnection<String, String> redisConnection = null;
//				try {
//					redisConnection = redisPool.borrowObject();
//					redisConnection.setAutoFlushCommands(true);
//					redisConnection.sync().set(deviceId, redisObject.toString());
//
//					if(DevUtils.isPrint(deviceId)) {
//						//log.info(redisPool.toString());
//						log.info("Redis Execute -> " + deviceId);
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				} finally {
//					redisConnection.close();
//					//redisPool.close();
//				}
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