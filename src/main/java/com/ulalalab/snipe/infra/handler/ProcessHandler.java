package com.ulalalab.snipe.infra.handler;

import com.ulalalab.snipe.device.model.Device;
import com.ulalalab.snipe.infra.manage.ChannelManager;
import com.ulalalab.snipe.infra.util.BeansUtils;
import com.ulalalab.snipe.infra.util.ScriptUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.dto.Point;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.influxdb.InfluxDBTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;
import javax.script.Invocable;
import javax.script.ScriptException;
import java.util.concurrent.TimeUnit;

@Slf4j(topic = "TCP.ProcessHandler")
public class ProcessHandler extends ChannelInboundHandlerAdapter {

	private static Long receive = 1L;
	private static ChannelManager channelManager = ChannelManager.getInstance();

	//	@Autowired
	//	private JdbcTemplate jdbcTemplate;

	private RedisTemplate<String, Object> redisTemplate;
	private InfluxDBTemplate<Point> influxDBTemplate;

	private Invocable invocable;
	private String deviceId = null;

	public ProcessHandler() {
		this.redisTemplate = (RedisTemplate<String, Object>) BeansUtils.getBean("redisTemplate");
		this.influxDBTemplate = (InfluxDBTemplate<Point>) BeansUtils.getBean("influxDBTemplate");
		//this.jdbcTemplate = (JdbcTemplate) BeansUtils.getBean("jdbcTemplate");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object packet) {
		try {
			Device device = (Device) packet;

			if (StringUtils.hasText(device.getSource())) {
				this.invocable = ScriptUtils.getInvocable(device.getSource());
			}

			if(deviceId==null) {
				DefaultHandler defaultHandler = (DefaultHandler) ctx.channel().pipeline().get("TCP.DefaultHandler");

				defaultHandler.deviceId = device.getDeviceId();
				this.deviceId = device.getDeviceId();
			}

			Double cvCh1 = device.getCh1();
			Double cvCh2 = device.getCh2();
			Double cvCh3 = device.getCh3();
			Double cvCh4 = device.getCh4();
			Double cvCh5 = device.getCh5();

			if (invocable != null) {
				cvCh1 = (Double) invocable.invokeFunction("add", cvCh1);
				cvCh2 = (Double) invocable.invokeFunction("add", cvCh2);
				cvCh3 = (Double) invocable.invokeFunction("add", cvCh3);
				cvCh4 = (Double) invocable.invokeFunction("add", cvCh4);
				cvCh5 = (Double) invocable.invokeFunction("add", cvCh5);

				device.setCh1(cvCh1);
				device.setCh2(cvCh2);
				device.setCh3(cvCh3);
				device.setCh4(cvCh4);
				device.setCh5(cvCh5);

				if(cvCh1.isNaN()) {
					throw new ScriptException("isNaN");
				}
			}
			log.info("받은 데이터 [" + (receive++) + ", 클라이언트 Count : " + channelManager.channelSize() + "] => {}", device);

			// 1. TimscaleDB Update
//			jdbcTemplate.update("insert into ulalalab_e(time, insert_time, device_id, ch1, ch2, ch3, ch4, ch5) " +
//							"values(?, now(), ?, ?, ?, ?, ?, ?)"
//					, device.getTime()
//					, device.getDeviceId()
//					, cvCh1
//					, cvCh2
//					, cvCh3
//					, cvCh4
//					, cvCh5
//			);

			System.out.println("##@@ " + influxDBTemplate);

			// 1. InfluxDB Insert
			influxDBTemplate.createDatabase();
			final Point p = Point.measurement(device.getDeviceId())
					.time(device.getTime(), TimeUnit.MILLISECONDS)
					//.tag("test", "default")
					.addField("ch1", device.getCh1())
					.addField("ch2", device.getCh2())
					.addField("ch3", device.getCh3())
					.addField("ch4", device.getCh4())
					.addField("ch5", device.getCh5())
					.addField("insert_time", System.currentTimeMillis())
					.build();
			influxDBTemplate.write(p);

			// 2. Redis Update
			ValueOperations<String, Object> vop = redisTemplate.opsForValue();
			JSONObject redisObject = new JSONObject();
			redisObject.put("ch1", device.getCh1());
			redisObject.put("ch2", device.getCh2());
			redisObject.put("ch3", device.getCh3());
			redisObject.put("ch4", device.getCh4());
			redisObject.put("ch5", device.getCh5());
			redisObject.put("time", device.getTime());
			vop.set(deviceId, redisObject.toString());

		} catch(ScriptException e) {
			log.error(e.getMessage());
			e.printStackTrace();

			invocable = null;
			ctx.pipeline().remove("caculateHandler");
			log.error("{} -> caculateHandler 제거!", this.getClass());
		} catch(Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {
			// 참조 해체
			ReferenceCountUtil.release(packet);
		}
	}

	// 수신 데이터 처리 완료
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		//ctx.writeAndFlush(Unpooled.EMPTY_BUFFER) // 대기중인 메시지를 플러시하고 채널을 닫음
			//.addListener(ChannelFutureListener.CLOSE);
		//logger.info(this.getClass() + " => channelReadComplete");
		//logger.info(this.getClass() + " / channelReadComplete!!");
	}
}