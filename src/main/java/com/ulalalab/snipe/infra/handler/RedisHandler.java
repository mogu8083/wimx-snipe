package com.ulalalab.snipe.infra.handler;

import com.ulalalab.snipe.device.model.Device;
import com.ulalalab.snipe.infra.channel.SpChannelGroup;
import com.ulalalab.snipe.infra.manage.EventManager;
import com.ulalalab.snipe.infra.manage.InfluxDBManager;
import com.ulalalab.snipe.infra.util.BeansUtils;
import com.ulalalab.snipe.infra.util.DevUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.dto.Point;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.influxdb.InfluxDBTemplate;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Slf4j(topic = "TCP.RedisHandler")
@ChannelHandler.Sharable
public class RedisHandler extends ChannelInboundHandlerAdapter {

	private List<String> list = new ArrayList<>();

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object obj) {
		Device device = (Device) obj;

		list.add(device.getDeviceId());

		if(DevUtils.isPrint(device.getDeviceId()))
			log.info(device.getDeviceId() + " / list size : " + list.size());
	}
}