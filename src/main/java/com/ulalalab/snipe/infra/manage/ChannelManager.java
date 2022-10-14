package com.ulalalab.snipe.infra.manage;

import com.ulalalab.snipe.device.model.ChannelInfo;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ChannelManager {

    //private Set<Channel, ChannelInfo> channelGroup = new HashSet<>();
    private Map<Channel, ChannelInfo> channelGroup = new ConcurrentHashMap<>();
    private static ChannelManager channelManager;

    static {
        channelManager = new ChannelManager();
    }

    public static ChannelManager getInstance() {
        return channelManager;
    }

    public void addChannel(Channel channel, ChannelInfo channelInfo) {
        channelGroup.put(channel, channelInfo);
    }

    public void removeChannel(Channel channel) {
        channelGroup.remove(channel);
    }

    /**
     * 채널 삭제
     * @param deviceId
     */
    public void removeChannel(String deviceId) {

        for (Channel channel : channelGroup.keySet()) {
            ChannelInfo channelInfo = channelGroup.get(channel);

            if(channelInfo.getDeviceId().equals(deviceId)) {
                log.info("{} DeviceId Client Channel Close!", channelInfo.getDeviceId());
                channel.close();
                break;
            }
        }
    }

    public ChannelInfo getChannelInfo(Channel channel) {
        return channelGroup.get(channel);
    }

    public void setChannelInfo(Channel channel, ChannelInfo channelInfo) {
        channelGroup.put(channel, channelInfo);
    }

    public Map<Channel, ChannelInfo> getChannelGroup() {
        return channelGroup;
    }

    public int channelSize() {
        return channelGroup.size();
    }
}