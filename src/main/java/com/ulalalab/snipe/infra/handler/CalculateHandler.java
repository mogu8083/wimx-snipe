//package com.ulalalab.snipe.infra.handler;
//
//import com.ulalalab.snipe.device.model.Device;
//import com.ulalalab.snipe.infra.util.BeansUtils;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.ChannelInboundHandlerAdapter;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.util.StringUtils;
//import java.util.List;
//
//@Slf4j(topic = "TCP.CaculateHandler")
//public class CalculateHandler extends ChannelInboundHandlerAdapter {
//
//	private JdbcTemplate jdbcTemplate;
//	private boolean initFlag = true;
//
//	public CalculateHandler() {
//		this.jdbcTemplate = (JdbcTemplate) BeansUtils.getBean("jdbcTemplate");
//	}
//
//	@Override
//	public void channelRead(ChannelHandlerContext ctx, Object packet) {
//		try {
//			Device device = (Device) packet;
//
//			if(initFlag) {
//				initFlag = false;
//
//				List<String> sourceList;
//				String source = "";
//				String sql = "select source from device_filter where device_id = ?";
//				sourceList = jdbcTemplate.queryForList(sql, String.class, device.getDeviceId());
//
//				if(sourceList.size() > 0) {
//					source = sourceList.get(0);
//				}
//
//				if(StringUtils.hasText(source)) {
//					device.setSource(source);
//				} else {
//					ctx.pipeline().remove(this);
//				}
//			}
//			ctx.fireChannelRead(packet);
//		} catch (Exception e) {
//			log.error("{} / {}", this.getClass(), e.getMessage());
//			ctx.pipeline().remove(this);
//		}
//	}
//
//	/**
//	 * 계산식 다시 조회 Flag
//	 * @param flag
//	 */
//	public void setInitFlag(boolean flag) {
//		this.initFlag = flag;
//	}
//}