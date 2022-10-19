package com.ulalalab.snipe.infra.manage;

import com.ulalalab.snipe.device.model.ChannelInfo;
import com.ulalalab.snipe.infra.handler.CalculateHandler;
import com.ulalalab.snipe.infra.handler.DefaultHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ChannelManager {

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

    public synchronized void removeChannel(Channel channel) {
        channelGroup.remove(channel);
    }

    /**
     * 채널 삭제
     * @param deviceId
     */
    public synchronized void removeChannel(String deviceId) throws Exception {

        for (Channel channel : channelGroup.keySet()) {
            ChannelInfo channelInfo = channelGroup.get(channel);

            if(channelInfo.getDeviceId().equals(deviceId)) {
                log.info("{} DeviceId Client Channel Close!", channelInfo.getDeviceId());
                DefaultHandler handler = (DefaultHandler) channel.pipeline().get("TCP.DefaultHandler");
                ChannelHandlerContext ctx = handler.getChannelHandlerContext();

                ctx.close();
                break;
            }
        }
    }

    public ChannelInfo getChannelInfo(Channel channel) {
        return channelGroup.get(channel);
    }

    public synchronized void addChannel(Channel channel) {
        channelGroup.put(channel, new ChannelInfo());
    }

    public Map<Channel, ChannelInfo> getChannelGroup() {
        return channelGroup;
    }

    public int channelSize() {
        return channelGroup.size();
    }

    public int calculatePush(String deviceId) {
        int resultCnt = 0;

        for (Channel channel : channelGroup.keySet()) {
            ChannelInfo channelInfo = channelGroup.get(channel);

            if(channelInfo.getDeviceId().equals(deviceId)) {
                log.info("{} DeviceId Filter Change!", channelInfo.getDeviceId());
                CalculateHandler handler = (CalculateHandler) channel.pipeline().get("TCP.CalculateHandler");

                if(handler == null) {
                    channel.pipeline().addAfter("TCP.PacketDecoder", "TCP.CalculateHandler", new CalculateHandler());
                    handler = (CalculateHandler) channel.pipeline().get("TCP.CalculateHandler");
                }
                handler.setInitFlag(true);
                resultCnt++;
                break;
            }
        }
        return resultCnt;
    }
}