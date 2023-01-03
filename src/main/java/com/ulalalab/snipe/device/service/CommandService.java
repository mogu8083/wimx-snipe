package com.ulalalab.snipe.device.service;

import com.ulalalab.snipe.device.model.Response;
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

    public void set(ByteBuf buffer, byte cmd) {

//        switch (cmd) {
//            case 0x02:
//                this.write(buffer);
//                break;
//        }
    }

    public boolean reboot(Channel channel) throws InterruptedException {
        log.info(channel.id() + " 재부팅!");

        ByteBuf buf = Unpooled.buffer();

        buf.writeShort(0x1616);
        buf.writeShort(0x0000);
        buf.writeByte(0x04);
        buf.writeInt((int) Instant.now().getEpochSecond());

        buf.writeByte(0x00);
        buf.writeShort(0x0000);
        buf.writeByte(0xF5);

        ChannelFuture future = channel.writeAndFlush(buf);

        if(future.sync().isDone()) {
            return true;
        }
        return false;
    }
}