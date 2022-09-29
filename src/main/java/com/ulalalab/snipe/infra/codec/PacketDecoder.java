package com.ulalalab.snipe.infra.codec;

import com.ulalalab.snipe.device.model.Device;
import com.ulalalab.snipe.infra.util.ByteUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;

@Component
@Slf4j(topic = "TCP.PacketDecoder")
public class PacketDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {

        //ByteBuf in = ctx.alloc().directBuffer(1024);

        int readerIndex = in.readerIndex();
        int readableBytes = in.readableBytes();

        if (in.readableBytes() < 49) {
            return;
        }

        if (in.getByte(readerIndex) != 0x02 || in.getByte(readableBytes + readerIndex - 1) != 0x03) {
            return;
        }
        //logger.info("in.readableBytes() -> " + in.readableBytes() + " / in.readerIndex() -> " + in.readerIndex());

        StringBuffer hexString = new StringBuffer();

        for (int i = readerIndex; i < readableBytes + readerIndex; i++) {
            hexString.append(ByteUtils.byteToHexString(in.getByte(i)) + " ");
        }
        //logger.info("Receive HEX : " + hexString.toString());

        do {
            try {
                    /* 데이터 형식
                    0x02            : STX
                    (x, 가변)         : deviceId
                    Long    : 8Byte
                    Double : 8byte
                    Double : 8byte
                    Double : 8byte
                    Double : 8byte
                    Double : 8byte
                    0x03            : ETX
                    */

                // 0x02
                in.readByte();
                int deviceSize = in.readInt();

                String deviceId = in.toString(in.readerIndex(), deviceSize, StandardCharsets.UTF_8);
                in.readBytes(deviceSize);

                Long time = in.readLong();
                //LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), TimeZone.getDefault().toZoneId());

                Double ch1 = in.readDouble();
                Double ch2 = in.readDouble();
                Double ch3 = in.readDouble();
                Double ch4 = in.readDouble();
                Double ch5 = in.readDouble();

                // 0x03
                in.readByte();

                // Data Setting
                Device device = new Device();

                device.setTime(time);
                device.setDeviceId(deviceId);
                device.setCh1(ch1);
                device.setCh2(ch2);
                device.setCh3(ch3);
                device.setCh4(ch4);
                device.setCh5(ch5);

                if (!deviceId.contains("WX")) {
                    throw new Exception("Not Vaild DeviceId -> " + hexString.toString() + " / Device : " + device.toString());
                }
                out.add(device);
                in.slice(in.readerIndex(), in.readableBytes());
            } catch (Exception e) {
                e.printStackTrace();
                log.error(this.getClass() + " -> " + e.getMessage() + " 올바른 데이터 형식이 아님. clear -> " + hexString.toString());
                in.clear();
            }
        } while(in.readableBytes() > 49);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("{} -> PacketDecoder Error ! -> {}", this.getClass(), cause.getCause());
        cause.printStackTrace();
    }
}