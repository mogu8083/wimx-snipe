package com.ulalalab.snipe.infra.manage;

import io.netty.channel.Channel;
import java.util.HashSet;
import java.util.Set;

public class ChannelManager {

    private Set<Channel> channelGroup = new HashSet<>();
    private static ChannelManager channelManager;

    static {
        channelManager = new ChannelManager();
    }

    public static ChannelManager getInstance() {
        return channelManager;
    }

    public synchronized void addChannel(Channel channel) {
        channelGroup.add(channel);
    }

    public void removeChannel(Channel channel) {
        channelGroup.remove(channel);
    }

    public int channelSize() {
        return channelGroup.size();
    }
}