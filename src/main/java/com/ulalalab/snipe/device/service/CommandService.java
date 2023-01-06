package com.ulalalab.snipe.device.service;

import com.ulalalab.snipe.device.model.Response;
import com.ulalalab.snipe.infra.channel.SpChannelGroup;
import com.ulalalab.snipe.infra.manager.InstanceManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Properties;

@Slf4j
@Service
public class CommandService {

    private final SpChannelGroup spChannelGroup = InstanceManager.getInstance().getSpChannelGroup();

    /**
     * @param deviceIndex 채널
     * @return true / false
     */
    public boolean reboot(short deviceIndex) {
        log.info("{} 장비 재부팅!", deviceIndex);

        boolean isResult = false;
        Channel channel = spChannelGroup.getChannel(deviceIndex);

        try {
            if (channel == null) {
                throw new NullPointerException();
            }

            ByteBuf buf = Unpooled.buffer();

            buf.writeShort(0x1616);
            buf.writeShort(0x0000);
            buf.writeByte(0x04);
            buf.writeInt((int) Instant.now().getEpochSecond());

            buf.writeByte(0x00);
            buf.writeShort(0x0000);
            buf.writeByte(0xF5);

            ChannelFuture future = channel.writeAndFlush(buf);

            if (future.sync().isDone()) {
                isResult = true;
            }
        } catch (NullPointerException e) {
            log.warn("{} 장비가 존재 하지 않음.", deviceIndex);
        } catch (Exception e) {
            log.warn("{} 장비 재부팅 실패!", deviceIndex);
        }
        return isResult;
    }

    public boolean write(Channel channel) {
//        ByteBuf buf = Unpooled.buffer();
//
//        buf.writeShort(0x1616);
//        buf.writeShort(0x0000);
//        buf.writeByte(0x04);
//        buf.writeInt((int) Instant.now().getEpochSecond());
//
//        buf.writeByte(0x00);
//        buf.writeShort(0x0000);
//        buf.writeByte(0xF5);
//
//        ChannelFuture future = channel.writeAndFlush(buf);
//
//        if(future.sync().isDone()) {
//            return true;
//        }
        return false;
    }
}