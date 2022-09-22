package com.ulalalab.snipe.infra.handler;

import com.ulalalab.snipe.device.model.Device;
import com.ulalalab.snipe.infra.util.BeansUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;
import java.util.List;

public class CaculateHandler extends ChannelInboundHandlerAdapter {

	private final Logger logger = LoggerFactory.getLogger("TCP."+ this.getClass());
	private JdbcTemplate jdbcTemplate;
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

				List<String> sourceList;
				String source = "";

				sourceList = jdbcTemplate.queryForList("select source from device_filter where device_id = ?", String.class, device.getDeviceId());
				if(sourceList.size() > 0) {
					source = sourceList.get(0);
				}

				if(StringUtils.hasText(source)) {
					device.setSource(source);
				} else {
					ctx.pipeline().remove(this);
				}
			}
			ctx.fireChannelRead(packet);
		} catch (Exception e) {
			logger.error(this.getClass() + " / " + e.getMessage());
			ctx.pipeline().remove(this);
		}
	}
}