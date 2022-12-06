//package com.ulalalab.snipe.infra.handler;
//
//import com.ulalalab.snipe.device.model.Device;
//import com.ulalalab.snipe.infra.manage.InfluxDBManager;
//import com.ulalalab.snipe.infra.manage.RedisManager;
//import com.ulalalab.snipe.infra.util.DevUtils;
//import io.lettuce.core.FlushMode;
//import io.lettuce.core.RedisFuture;
//import io.lettuce.core.SetArgs;
//import io.lettuce.core.api.StatefulRedisConnection;
//import io.lettuce.core.api.async.RedisAsyncCommands;
//import io.lettuce.core.api.sync.RedisCommands;
//import io.netty.buffer.ByteBuf;
//import io.netty.buffer.Unpooled;
//import io.netty.channel.ChannelHandler;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.ChannelInboundHandlerAdapter;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.influxdb.dto.Point;
//import org.springframework.boot.configurationprocessor.json.JSONException;
//import org.springframework.boot.configurationprocessor.json.JSONObject;
//import org.springframework.dao.DataAccessException;
//import org.springframework.data.influxdb.InfluxDBTemplate;
//import org.springframework.data.redis.connection.RedisConnection;
//import org.springframework.data.redis.connection.RedisTxCommands;
//import org.springframework.data.redis.connection.StringRedisConnection;
//import org.springframework.data.redis.core.RedisCallback;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.ValueOperations;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//import org.springframework.stereotype.Component;
//
//import java.lang.ref.WeakReference;
//import java.time.Duration;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//
//
//@Component
//@Slf4j(topic = "TCP.RedisHandler")
//@ChannelHandler.Sharable
//@RequiredArgsConstructor
//public class RedisHandler extends ChannelInboundHandlerAdapter {
//
//	private StatefulRedisConnection<String, String> redisConnection;
//	private RedisAsyncCommands<String, String> commands;
//	// Bean
//	private final RedisManager redisManager;
//	private final RedisTemplate<String, String> redisTemplate;
//	private final ThreadPoolTaskExecutor threadPoolTaskExecutor;
//
//	private JSONObject redisObject;
//
//	@Override
//	public void channelActive(ChannelHandlerContext ctx) throws Exception {
//		redisConnection = redisManager.getRedisConnection();
//		//commands = redisManager.getRedisConnection().async();
//		commands = redisManager.getRedisCommands();
//		//commands.setAutoFlushCommands(true);
//		redisObject = new JSONObject();
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
//
//			try {
//				redisObject.put("ch1", device.getCh1());
//				redisObject.put("ch2", device.getCh2());
//				redisObject.put("ch3", device.getCh3());
//				redisObject.put("ch4", device.getCh4());
//				redisObject.put("ch5", device.getCh5());
//				redisObject.put("time", device.getTime());
//				redisObject.put("cvtTime", device.getCvtTime());
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//
//			ctx.channel().eventLoop().execute(() -> {
//				commands.set(deviceId, redisObject.toString());
//
//				if (DevUtils.isPrint(deviceId)) {
//					log.info("Redis Execute 11 -> " + device.toString());
//				}
//			});
////			threadPoolTaskExecutor.execute(() -> {
////				commands.set(deviceId, redisObject.toString());
////			});
//				//commands.setTimeout(Duration.ofMillis(5000));
//				//commands.flushCommands();
//				//commands.flushall(FlushMode.SYNC);
//				//commands.flushall(FlushMode.ASYNC);
//				//commands.flushCommands();
//				//commands.setTimeout(Dur);
////			} catch (Exception e) {
//				//commands.reset();
//			ctx.fireChannelRead(obj);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	// 수신 데이터 처리 완료
//	@Override
//	public void channelReadComplete(ChannelHandlerContext ctx) {
//
//	}
//
//	public static void main(String[] args) {
//		ByteBuf buf = Unpooled.buffer();
//		buf.writeByte(0x16);
//		buf.writeByte(0x0c);
//
//		buf.readerIndex(0);
//
//		System.out.println("##@@ " + buf.getByte(0));
//	}
//}