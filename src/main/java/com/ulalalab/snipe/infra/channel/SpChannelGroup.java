package com.ulalalab.snipe.infra.channel;

import com.ulalalab.snipe.device.model.ChannelInfo;
import com.ulalalab.snipe.infra.manage.ChannelManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.internal.PlatformDependent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class SpChannelGroup extends DefaultChannelGroup {

	private final ConcurrentMap<ChannelId, ChannelInfo> channelInfos = PlatformDependent.newConcurrentHashMap();

	public SpChannelGroup(EventExecutor executor) {
		super(executor);
	}

	public void addChannelInfo(ChannelId channelId, ChannelInfo channelInfo) {
		channelInfos.put(channelId, channelInfo);
	}

	public ChannelInfo getChannelInfo(ChannelId channelId) {
		return channelInfos.get(channelId);
	}

	public List<ChannelInfo> getChannelInfoList() {
		List<ChannelInfo> list = new ArrayList<>();

		for (ChannelId channelId : channelInfos.keySet()) {
			ChannelInfo channelInfo = channelInfos.get(channelId);
			list.add(channelInfo);
		}
		return list;
	}
}