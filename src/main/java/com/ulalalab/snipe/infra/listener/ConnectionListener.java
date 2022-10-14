package com.ulalalab.snipe.infra.listener;

import com.ulalalab.snipe.server.ClientServer;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.util.concurrent.TimeUnit;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

//@Component
@Slf4j(topic = "CLIENT.ConnectionListener")
public class ConnectionListener implements ChannelFutureListener {

    private ClientServer clientServer;
    private Integer deviceId;

    public ConnectionListener(ClientServer clientServer, Integer deviceId) {
        this.deviceId = deviceId;
        this.clientServer = clientServer;
    }

    @Override
    public void operationComplete(ChannelFuture channelFuture) throws Exception {
        if (!channelFuture.isSuccess()) {
            final EventLoop loop = channelFuture.channel().eventLoop();

            loop.schedule(() -> {
                try {
                    clientServer.createBootstrap(new Bootstrap(), loop, deviceId);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }, 3L, TimeUnit.SECONDS);
        }
    }
}