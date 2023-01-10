package com.ulalalab.snipe.device.model;

import com.ulalalab.snipe.api.model.Response;
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

	public Response getChannelInfoList() {
		List<ChannelInfo> list = new ArrayList<>();

		for (ChannelId channelId : channelInfos.keySet()) {
			ChannelInfo channelInfo = channelInfos.get(channelId);
			list.add(channelInfo);
		}
		return new Response(list);
	}

	public Channel getChannel(int deviceIndex) {
		Channel channel = null;

		for (ChannelId channelId : channelInfos.keySet()) {
			ChannelInfo channelInfo = channelInfos.get(channelId);

			if(deviceIndex == channelInfo.getDeviceIndex()) {
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

	public Response channelDisconnect(short deviceIndex) {
		ChannelId removeChannelId = null;
		Response response = null;

		for (ChannelId channelId : channelInfos.keySet()) {
			ChannelInfo channelInfo = channelInfos.get(channelId);

			if(deviceIndex == channelInfo.getDeviceIndex()) {
				this.find(channelId).close();
				removeChannelId = channelId;
				break;
			}
		}

		if(removeChannelId != null) {
			channelInfos.remove(removeChannelId);
			response = new Response("장비 연결이 해제되었습니다.");
		} else {
			response = new Response(Response.Code.FAIL, deviceIndex + " 해당 장비가 존재 하지 않습니다.");
		}
		return response;
	}
}