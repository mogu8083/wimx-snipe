package com.ulalalab.snipe.infra.handler;

import com.ulalalab.snipe.device.model.ChannelInfo;
import com.ulalalab.snipe.device.model.Device;
import com.ulalalab.snipe.infra.channel.SpChannelGroup;
import com.ulalalab.snipe.infra.manage.EventManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Component
@ChannelHandler.Sharable
@Slf4j(topic = "TCP.SettingHandler")
public class SettingHandler extends ChannelInboundHandlerAdapter {

	//private boolean isSettingDevice = false;
    private SpChannelGroup spChannelGroup = EventManager.getInstance().getSpChannelGroup();

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object obj) {
		Device device = (Device) obj;
		Channel channel = ctx.channel();
		ChannelInfo channelInfo = spChannelGroup.getChannelInfo(channel.id());

		// 1. 채널 정보
		if(channelInfo!=null) {
			if(channelInfo.isInitSetting()) {
				channelInfo.setLastPacketTime(LocalDateTime.now());
			} else {
				channelInfo.setDeviceId(device.getDeviceId());
				channelInfo.setRemoteAddress(channel.remoteAddress().toString());
				channelInfo.setConnectTime(LocalDateTime.now());
				channelInfo.setLocalAddress(channel.localAddress().toString());
				channelInfo.setHandlerList(channel.pipeline().names()
						.stream().filter(c -> !c.contains("TailContext")).collect(Collectors.toList()));

				channelInfo.setInitSetting(true);
			}
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