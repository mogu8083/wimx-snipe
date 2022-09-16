package com.ulalalab.snipe.common.codec;

import com.ulalalab.snipe.common.util.ByteUtil;
import com.ulalalab.snipe.device.model.Device;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {

    private static final Logger logger = LoggerFactory.getLogger(PacketDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //logger.info("in.readableBytes() -> " + in.readableBytes() + " / " + in.readerIndex() + "-> " + in.readerIndex());

        if (in.readableBytes() >= 40) {
            try {
                StringBuffer sb = new StringBuffer();
                for (int i = in.readerIndex(); i < in.readableBytes() + in.readerIndex(); i++) {
                    sb.append(ByteUtil.byteToHexString(in.getByte(i)) + " ");
                }
                //logger.info("Receive HEX : " + sb.toString());

                LocalDateTime now = LocalDateTime.now();

                // 0x02
                in.readByte();
                int deviceSize = in.readInt();

                String deviceId = in.toString(in.readerIndex(), deviceSize, StandardCharsets.UTF_8);

                in.readBytes(deviceSize);
                Double ch1 = in.readDouble();
                Double ch2 = in.readDouble();
                Double ch3 = in.readDouble();
                Double ch4 = in.readDouble();
                Double ch5 = in.readDouble();

                // 0x03
                in.readByte();

                // Data Setting
                Device device = new Device();
                device.setTime(now);
                device.setDeviceId(deviceId);
                device.setCh1(ch1);
                device.setCh2(ch2);
                device.setCh3(ch3);
                device.setCh4(ch4);
                device.setCh5(ch5);

                out.add(device);
                in.slice(in.readerIndex(), in.readableBytes());
            } catch (Exception e) {
                logger.error(this.getClass() + " -> " + e.getMessage() + " 올바른 데이터 형식이 아님.");
                in.clear();
            }
        }
    }
}