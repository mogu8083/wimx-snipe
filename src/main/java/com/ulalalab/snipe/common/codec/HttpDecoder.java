package com.ulalalab.snipe.common.codec;

import com.ulalalab.snipe.common.util.ByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.List;

public class HttpDecoder extends ByteToMessageDecoder {
    private static final Logger logger = LoggerFactory.getLogger(HttpDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        logger.info("decode -> " + ctx + " / " + in.readableBytes());

        if(in.readableBytes() >= 40) {
            try {
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < in.readableBytes(); i++) {
                    sb.append(ByteUtil.byteToHexString(in.getByte(i)) + " ");
                }
                logger.info("Receive HEX : " + sb.toString());

                LocalDateTime now = LocalDateTime.now();

                // 0x02
                in.readByte();
                int deviceSize = in.readInt();

                String deviceId = in.toString(5, deviceSize, Charset.defaultCharset());

                in.readBytes(deviceSize);
                Double ch1 = in.readDouble();
                Double ch2 = in.readDouble();
                Double ch3 = in.readDouble();
                Double ch4 = in.readDouble();
                Double ch5 = in.readDouble();

                // 0x03
                in.readByte();

                out.add(deviceId+"#"+ch1+"#"+ch2+"#"+ch3+"#"+ch4+"#"+ch5);
            } catch (Exception e) {
                logger.error(this.getClass() + " -> " + e.getMessage() + " 올바른 데이터 형식이 아님.");
                in.clear();
            }
        }
    }
}