package com.ulalalab.snipe.infra.manage;

import io.netty.channel.Channel;
import java.util.HashSet;
import java.util.Set;

public class ChannelManager {

    private static Set<Channel> channelGroup;

    static {
        channelGroup = new HashSet<>();
    }

    public static Set<Channel> getInstance() {
        return channelGroup;
    }
}