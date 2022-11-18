package com.ulalalab.snipe.infra.manage;

import com.ulalalab.snipe.device.model.ChannelInfo;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ChannelManager {

    //private ConcurrentHashMap<Channel, ChannelInfo> channelGroup = new ConcurrentHashMap<>();
    private Map<Channel, ChannelInfo> channelGroup = new ConcurrentHashMap<>();
    //private Map<Channel, ChannelInfo> channelGroup = new HashMap<>();
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
    public void removeChannel(String deviceId) throws Exception {

        for (Channel channel : channelGroup.keySet()) {
            ChannelInfo channelInfo = channelGroup.get(channel);
            String channelDeviceId = channelInfo.getDeviceId();

            if(StringUtils.hasText(channelDeviceId)) {
                if(channelDeviceId.equals(deviceId)) {
                    log.info("{} DeviceId Client Channel Close!", channelInfo.getDeviceId());
                    channel.close();
                    channelGroup.remove(channel);
                    break;
                }
            } else {
                if(deviceId == null) {
                    log.info("DeviceId Null Channel Close!");
                    channel.close();
                }
            }
        }
    }

    public ChannelInfo getChannelInfo(Channel channel) {
        return channelGroup.get(channel);
    }

    public void addChannel(Channel channel) {
        channelGroup.put(channel, new ChannelInfo());
    }

    public List<ChannelInfo> getChannelGroup() {
        List<ChannelInfo> list = new ArrayList<>();

        for (Channel channel : channelGroup.keySet()) {
            ChannelInfo channelInfo = channelGroup.get(channel);

            list.add(channelInfo);
        }
        return list;
    }

    public int channelSize() {
        return channelGroup.size();
    }

//    public int calculatePush(String deviceId) {
//        int resultCnt = 0;
//
//        for (Channel channel : channelGroup.keySet()) {
//            ChannelInfo channelInfo = channelGroup.get(channel);
//
//            String channelDeviceId = channelInfo.getDeviceId();
//
//            if(StringUtils.hasText(channelDeviceId)) {
//                if(channelDeviceId.equals(deviceId)) {
//                    log.info("{} DeviceId Filter Change!", channelInfo.getDeviceId());
//                    CalculateHandler handler = (CalculateHandler) channel.pipeline().get("TCP.CalculateHandler");
//
//                    if(handler == null) {
//                        channel.pipeline().addAfter("TCP.PacketDecoder", "TCP.CalculateHandler", new CalculateHandler());
//                        handler = (CalculateHandler) channel.pipeline().get("TCP.CalculateHandler");
//                    }
//                    handler.setInitFlag(true);
//                    resultCnt++;
//                    break;
//                }
//            } else {
//                log.info("DeviceId Null Channel Close!");
//                channel.close();
//            }
//        }
//        return resultCnt;
//    }
}