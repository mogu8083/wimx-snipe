package com.ulalalab.snipe.infra.manager;

import com.ulalalab.snipe.device.model.SpChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InstanceManager {

    private static InstanceManager eventManager;
    private SpChannelGroup spChannelGroup;

    static {
        eventManager = new InstanceManager();
    }

    private InstanceManager() {
        this.initSpChannelGroup();
    }

    public static InstanceManager getInstance() {
        return eventManager;
    }

    private void initSpChannelGroup() {
        spChannelGroup = new SpChannelGroup(GlobalEventExecutor.INSTANCE);
    }

    public SpChannelGroup getSpChannelGroup() {
        return spChannelGroup;
    }
}