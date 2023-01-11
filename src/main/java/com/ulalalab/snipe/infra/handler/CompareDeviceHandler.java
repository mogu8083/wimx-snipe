package com.ulalalab.snipe.infra.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ulalalab.snipe.device.model.Device;
import com.ulalalab.snipe.infra.manager.RedisManager;
import com.ulalalab.snipe.infra.util.BeansUtils;
import com.ulalalab.snipe.infra.util.DevUtils;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.reactive.RedisStringReactiveCommands;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

//@Component
@ChannelHandler.Sharable
//@Scope(scopeName = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Slf4j(topic = "TCP.CompareDeviceHandler")
public class CompareDeviceHandler extends ChannelInboundHandlerAdapter {

    private StatefulRedisConnection<String, String> redisConnection;
    private RedisStringReactiveCommands<String, String> reactiveCommands;

    private RedisManager redisManager;
    private Device prevDevice;
    ObjectMapper mapper = new ObjectMapper();

    CompareDeviceHandler() {
        this.redisManager = (RedisManager) BeansUtils.getBean("redisManager");

        redisConnection = redisManager.getRedisConnection();
        reactiveCommands = redisConnection.reactive();
    }

    @Override
    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) throws Exception {
        Device device = (Device) msg;

        if(DevUtils.isPrint(device.getDeviceIndex())) {
            log.info("PrevDevice : {}", mapper.writeValueAsString(prevDevice));
        }

        if(prevDevice == null) {
            Mono<String> prevDeviceString = reactiveCommands.get(String.valueOf(device.getDeviceIndex()));

            prevDeviceString.subscribe(value -> {
                try {
                    prevDevice = mapper.readValue(value, Device.class);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            });
        } else {
            prevDevice = device;
        }
        ctx.fireChannelRead(msg);
    }
}
