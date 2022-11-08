package com.ulalalab.snipe.infra.codec;

import com.ulalalab.snipe.device.model.Device;
import com.ulalalab.snipe.infra.util.ByteUtils;
import io.netty.buffer.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import java.nio.charset.StandardCharsets;
import java.util.List;

//@Component
@Slf4j(topic = "TCP.PacketDecoder")
public class PacketDecoder extends ByteToMessageDecoder {

    //ByteBuf in = Unpooled.buffer(70);
    //ByteBuf in = PooledByteBufAllocator.DEFAULT.heapBuffer(70);
    //ByteBuf in = PooledByteBufAllocator.DEFAULT.heapBuffer(61);
//    Device device;

//    @Override
//    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
//        device = new Device();
//    }
//
//    @Override
//    protected void handlerRemoved0(ChannelHandlerContext ctx) throws Exception {
////        in.release(); // (1)
////        in = null;
////        //super.handlerRemoved0(ctx);
//        device = null;
//    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        Device device = new Device();

        int readerIndex = in.readerIndex();
        int readableBytes = in.readableBytes();

//        if(in.readableBytes() >= 59) {
        if(in.getByte(0)==0x02 && in.getByte(readableBytes + readerIndex - 1)==0x03) {
            try {
                in.readByte();
                int deviceSize = in.readInt();

                String deviceId = in.toString(in.readerIndex(), deviceSize, StandardCharsets.UTF_8);
                in.readBytes(deviceSize);

                Long time = in.readLong();
                //LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), TimeZone.getDefault().toZoneId());

                StringBuffer hexString = new StringBuffer();

                if(deviceId.equals("WX-5A") || deviceId.equals("WX-5Z")) {
                    for (int i = readerIndex; i < readableBytes + readerIndex; i++) {
                        hexString.append(ByteUtils.byteToHexString(in.getByte(i)));
                        hexString.append(" ");
                    }
                    log.info("Receive HEX : " + hexString.toString());
                }
                Double ch1 = in.readDouble();
                Double ch2 = in.readDouble();
                Double ch3 = in.readDouble();
                Double ch4 = in.readDouble();
                Double ch5 = in.readDouble();

                // 0x03
                in.readByte();

                // Data Setting
                device.setTime(time);
                device.setDeviceId(deviceId);
                device.setCh1(ch1);
                device.setCh2(ch2);
                device.setCh3(ch3);
                device.setCh4(ch4);
                device.setCh5(ch5);

                out.add(device);

//                if (deviceId.equals("WX-1Z") || deviceId.equals("WX-1A")) {
//                    log.info(device.toString());
//                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error(device.getDeviceId() + " -> " + this.getClass() + " -> " + e.getMessage() + " 올바른 데이터 형식이 아님 -> 초기화");

                int index = in.indexOf(in.readerIndex(), in.readableBytes(), (byte) 0x02);
                if(index > -1) {
                    in.slice(index, in.readableBytes());
                }
                //in.resetReaderIndex();
                //in.resetWriterIndex();
            } finally {
                int index = in.indexOf(in.readerIndex(), in.readableBytes(), (byte) 0x02);

                if(index > -1) {
                    in.slice(index, in.readableBytes());
                }
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("{} -> PacketDecoder Error ! -> {} / {} / {}", this.getClass(), cause.getMessage(), cause.toString(), cause.getCause());
        //ctx.alloc().heapBuffer().release();
        //in.clear();
        cause.printStackTrace();
    }
}