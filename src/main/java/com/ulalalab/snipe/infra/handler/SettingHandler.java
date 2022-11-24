package com.ulalalab.snipe.infra.handler;

import com.ulalalab.snipe.device.model.ChannelInfo;
import com.ulalalab.snipe.device.model.Device;
import com.ulalalab.snipe.infra.manage.ChannelManager;
import com.ulalalab.snipe.infra.util.BeansUtils;
import com.ulalalab.snipe.infra.util.ByteUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

//@Component
@Slf4j(topic = "TCP.SettingHandler")
public class SettingHandler extends ChannelInboundHandlerAdapter {

	private ChannelManager channelManager = ChannelManager.getInstance();
	private boolean isSettingDevice = false;

	private Map<Integer, LocalDateTime> alarmMap = new HashMap<>();

	private JdbcTemplate jdbcTemplate;

	public SettingHandler() {
		this.jdbcTemplate = (JdbcTemplate) BeansUtils.getBean("jdbcTemplate");

		// 알람 기준 설정
		//alarmMap.put(1, );
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object obj) {
		Device device = (Device) obj;
		Channel channel = ctx.channel();
		ChannelInfo channelInfo = channelManager.getChannelInfo(channel);

		// 1. 채널 정보
		if(!isSettingDevice) {
			channelInfo.setDeviceId(device.getDeviceId());
			channelInfo.setRemoteAddress(channel.remoteAddress().toString());
			channelInfo.setConnectTime(LocalDateTime.now());
			channelInfo.setLocalAddress(channel.localAddress().toString());
			channelInfo.setHandlerList(channel.pipeline().names()
					.stream().filter(c -> !c.contains("TailContext")).collect(Collectors.toList()));

			this.isSettingDevice = true;

			DefaultHandler defaultHandler = (DefaultHandler) ctx.channel().pipeline().get("TCP.DefaultHandler");
			defaultHandler.deviceId = device.getDeviceId();
		} else {
			channelInfo.setLastPacketTime(LocalDateTime.now());
		}

		// TODO : 2. 알람
		/*
		if(device.getCh1() > 95) {
			jdbcTemplate.update("insert into t_alarm values(?, ?, ?)"
				, device.getDeviceId(), "알람 발생", LocalDateTime.now());
		}
		*/
		ctx.fireChannelRead(obj);
	}
}