package com.ulalalab.snipe.infra.channel;

import com.ulalalab.snipe.device.model.ChannelInfo;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.internal.PlatformDependent;
import lombok.extern.slf4j.Slf4j;
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

	public Channel getChannel(int deviceId) {
		Channel channel = null;
		String tempDeviceId = "W" + deviceId;

		for (ChannelId channelId : channelInfos.keySet()) {
			ChannelInfo channelInfo = channelInfos.get(channelId);

			if(tempDeviceId.equals(channelInfo.getDeviceId())) {
				channel = this.find(channelId);
				break;
			}
		}
		return channel;
	}

	public void remove(ChannelId channelId) {
		channelInfos.remove(channelId);
		super.remove(channelId);
	}

	public boolean channelDisconnect(String deviceId) {
		ChannelId removeChannelId = null;

		for (ChannelId channelId : channelInfos.keySet()) {
			ChannelInfo channelInfo = channelInfos.get(channelId);

			if(deviceId.equals(channelInfo.getDeviceId())) {
				this.find(channelId).close();
				removeChannelId = channelId;
				break;
			}
		}

		if(removeChannelId != null) {
			channelInfos.remove(removeChannelId);
			return true;
		}
		return false;
	}
}