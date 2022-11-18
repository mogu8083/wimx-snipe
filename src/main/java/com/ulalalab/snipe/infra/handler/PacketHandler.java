package com.ulalalab.snipe.infra.handler;

import com.ulalalab.snipe.device.model.Device;
import com.ulalalab.snipe.infra.util.ByteUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

//@Component
@Slf4j(topic = "TCP.PacketHandler")
public class PacketHandler extends ChannelInboundHandlerAdapter {

	List<Device> deviceList = new ArrayList<>();
	private ByteBuf buffer;
	private final int bufferCapacity = 256;
	//private final int maxBufferCapacity = 4086;
	private String deviceId;

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		buffer = ctx.alloc().buffer(bufferCapacity);
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		buffer.release();
		buffer = null;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object packet) {

		ByteBuf in = (ByteBuf) packet;
		buffer.writeBytes(in);
		in.release();

		//ByteBuf buffer = (ByteBuf) packet;
		//log.info("Receive HEX : {}", ByteUtils.byteBufToHexString(buffer, buffer.readerIndex(), buffer.writerIndex()));

		while(true) {
			if(buffer.writerIndex() > 0 && buffer.getByte(0)==0x02) {

				int writerIndex = buffer.writerIndex();

				if(writerIndex - buffer.readerIndex() > 58
						&& buffer.indexOf(buffer.readerIndex(), writerIndex, (byte) 0x03) > -1) {

					Device device = new Device();
					int initReaderIndex = buffer.readerIndex();

					try {

//						int writerIndex = buffer.writerIndex();

						buffer.readByte();
						int deviceSize = buffer.readInt();

						String deviceId = buffer.toString(buffer.readerIndex(), deviceSize, StandardCharsets.UTF_8);
						//this.deviceId = deviceId;
						device.setDeviceId(deviceId);

						buffer.readBytes(deviceSize);

						if(deviceId.equals("WX-1A") || deviceId.equals("WX-1Z")) {
							log.info(buffer.toString());
							log.info("처리 Data HEX : {}", ByteUtils.byteBufToHexString(buffer, initReaderIndex, buffer.writerIndex()));
						}

						Long time = buffer.readLong();
						device.setTime(time);
						//LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), TimeZone.getDefault().toZoneId());

						Double ch1 = buffer.readDouble();
						Double ch2 = buffer.readDouble();
						Double ch3 = buffer.readDouble();
						Double ch4 = buffer.readDouble();
						Double ch5 = buffer.readDouble();

						device.setCh1(ch1);
						device.setCh2(ch2);
						device.setCh3(ch3);
						device.setCh4(ch4);
						device.setCh5(ch5);

						deviceList.add(device);

						// 0x03
						buffer.readByte();

						buffer.slice();
					} catch (Exception e) {
						e.printStackTrace();
						log.error(e.getMessage());

						log.error("Error Data HEX : {}", ByteUtils.byteBufToHexString(buffer, initReaderIndex, buffer.writerIndex()));

						this.setBufferInit((byte) 0x02);
					}
				} else {
					break;
				}
			} else {
				this.setBufferInit((byte) 0x02);
				break;
			}
		}

//		if("WX-1Z".equals(this.deviceId) || "WX-1A".equals(this.deviceId)) {
//			log.info("남은 HEX : {}", ByteUtils.byteBufToHexString(buffer, buffer.readerIndex(), buffer.writerIndex()));
//		}

		//buffer.readerIndex(0);
		buffer.clear();

		// buffer 초기화
		if(buffer.capacity() > bufferCapacity && buffer.writerIndex()==0) {
			buffer.capacity(bufferCapacity);
		}

		if(deviceList.size() > 0 ) {
			ctx.fireChannelRead(deviceList);
		}
	}

	private void setBufferInit(byte b) {
		int index = buffer.indexOf(buffer.readerIndex(), buffer.writerIndex(), b);
		if(index > -1) {
			buffer.readerIndex(index);
		} else {
			buffer.clear();
		}
	}
}