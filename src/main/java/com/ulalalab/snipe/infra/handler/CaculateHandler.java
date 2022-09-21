package com.ulalalab.snipe.infra.handler;

import com.ulalalab.snipe.device.model.Device;
import com.ulalalab.snipe.infra.util.BeansUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import java.util.Map;

public class CaculateHandler extends ChannelInboundHandlerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(CaculateHandler.class);
	private JdbcTemplate jdbcTemplate;
	private Map<String, Object> resultMap;
	private boolean initFlag = true;

	public CaculateHandler() {
		this.jdbcTemplate = (JdbcTemplate) BeansUtils.getBean("jdbcTemplate");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object packet) {
		try {
			Device device = (Device) packet;

			if(initFlag) {
				initFlag = false;

				System.out.println(device.toString());

				String source = "";

				try {
					source = jdbcTemplate.queryForObject("select source from device_filter where device_id = ?", String.class, device.getDeviceId());
				} catch(Exception e) {
					logger.error(e.getMessage());
				}
				if(StringUtils.hasText(source)) {
					device.setSource(source);
				} else {
					ctx.pipeline().remove("caculateHandler");
				}
			}
			ctx.fireChannelRead(packet);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}
}