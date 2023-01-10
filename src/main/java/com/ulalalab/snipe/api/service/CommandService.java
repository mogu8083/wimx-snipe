package com.ulalalab.snipe.api.service;

import com.ulalalab.snipe.api.model.Response;
import com.ulalalab.snipe.device.model.SpChannelGroup;
import com.ulalalab.snipe.infra.manager.InstanceManager;
import com.ulalalab.snipe.infra.util.CRC16ModubusUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Service
@Slf4j(topic = "API.CommandService")
public class CommandService {

    private final SpChannelGroup spChannelGroup = InstanceManager.getInstance().getSpChannelGroup();

    public boolean write(Channel channel) {
        return true;
    }

    /**
     * 장비 업데이트 -> 장비 Packet 전송
     * @param deviceIndex 채널
     * @return true / false
     */
    public Response update(short deviceIndex) {
        Response response = null;
        Channel channel = spChannelGroup.getChannel(deviceIndex);

        try {
            if (channel == null) {
                throw new NullPointerException();
            }
            ByteBuf buffer = getInitBuffer((byte) 0x03);

            buffer.writeByte(0x00);
            buffer.writeShort(CRC16ModubusUtils.calc(ByteBufUtil.getBytes(buffer, 0, buffer.readerIndex())));
            buffer.writeByte(0xF5);

            ChannelFuture future = channel.writeAndFlush(buffer);

            if (future.sync().isDone()) {
                response = new Response("장비에 업데이트를 요청하였습니다.");
            }
        } catch (NullPointerException e) {
            log.warn("[{}] Not Found Device", deviceIndex);
            response = new Response(Response.Code.FAIL, "해당 장비가 존재 하지 않습니다.");
        } catch (Exception e) {
            log.warn("[{}] Device Update Failed", deviceIndex);
            response = new Response(Response.Code.FAIL, "해당 장비의 업데이트를 실패하였습니다.");
        }
        return response;
    }

    /**
     * 장비 재부팅 -> 장비 Packet 전송
     * @param deviceIndex 채널
     * @return true / false
     */
    public Response reboot(short deviceIndex) {
        boolean isResult = false;
        Response response = null;
        Channel channel = spChannelGroup.getChannel(deviceIndex);

        try {
            if (channel == null) {
                throw new NullPointerException();
            }

            ByteBuf buffer = getInitBuffer((byte) 0x04);

            buffer.writeByte(0x00);
            buffer.writeShort(0x0000);
            buffer.writeByte(0xF5);

            ChannelFuture future = channel.writeAndFlush(buffer);

            if (future.sync().isDone()) {
                response = new Response("장비에 재부팅을 요청하였습니다.");
            }
        } catch (NullPointerException e) {
            log.warn("[{}] Not Found Device ", deviceIndex);
            response = new Response(Response.Code.FAIL, "해당 장비가 존재 하지 않습니다.");
        } catch (Exception e) {
            log.warn("[{}] Device Update Failed", deviceIndex);
            response = new Response(Response.Code.FAIL, "해당 장비의 재부팅이 실패하였습니다.");
        }
        return response;
    }

    /**
     * ByteBuf 기본 헤더 Write
     */
    private ByteBuf getInitBuffer(byte cmd) {
        ByteBuf buffer = Unpooled.buffer();

        buffer.writeShort(0x1616);
        buffer.writeShort(0x0000);
        buffer.writeByte(cmd);
        buffer.writeInt((int) Instant.now().getEpochSecond());

        return buffer;
    }
}