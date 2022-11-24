package com.ulalalab.snipe.infra.handler;

import com.ulalalab.snipe.device.model.Device;
import com.ulalalab.snipe.infra.util.ByteUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

//@Component
@Slf4j(topic = "TCP.PacketHandler")
public class PacketHandler_20221123 extends ChannelInboundHandlerAdapter {

	List<Device> deviceList = new ArrayList<>();
	private ByteBuf buffer;
	private final int bufferCapacity = 64;
	private String deviceId = null;
	Device device;
	ByteBufAllocator alloc = null;

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		//buffer = ctx.alloc().heapBuffer(bufferCapacity);

		alloc = ctx.alloc();
		//alloc.heapBuffer().capacity(64);
		//alloc.buffer().capacity(70);
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		alloc = null;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object packet) {
		// 1. 1차
//		ByteBuf in = (ByteBuf) packet;
//		buffer = ctx.alloc().heapBuffer();
//		buffer.writeBytes(in);
//		in.release();
//		in = null;
		//ctx.alloc().buffer().clear();

		ByteBuf in = (ByteBuf) packet;
		buffer = alloc.heapBuffer();
		buffer.writeBytes(in);
		in.release();
		in = null;

		//log.info("Receive HEX : {}", ByteUtils.byteBufToHexString(buffer, buffer.readerIndex(), buffer.writerIndex()));

		while(true) {
			if(buffer.writerIndex() - buffer.readerIndex() > 0) {
				if (buffer.getByte(0) == 0x02) {

					int writerIndex = buffer.writerIndex();

					if (writerIndex - buffer.readerIndex() > 58
							&& buffer.indexOf(buffer.readerIndex(), writerIndex, (byte) 0x03) > -1) {

						Device device = new Device();
						int initReaderIndex = buffer.readerIndex();

						try {

							//						int writerIndex = buffer.writerIndex();

							buffer.readByte();
							int deviceSize = buffer.readInt();

							if(this.deviceId==null) {
								this.deviceId = buffer.toString(buffer.readerIndex(), deviceSize, StandardCharsets.UTF_8);
							}
							buffer.readBytes(deviceSize);

							if (this.deviceId.equals("WX-1A") || this.deviceId.equals("WX-1Z") || this.deviceId.equals("WX-1S")) {
								log.info(buffer.toString());
								String logString = ByteUtils.byteBufToHexString(buffer, initReaderIndex, buffer.writerIndex());
								log.info("처리 Data HEX : {}", logString);
								logString = null;
							}
							device.setDeviceId(this.deviceId);
							device.setTime(buffer.readLong());
							device.setCh1(buffer.readDouble());
							device.setCh2(buffer.readDouble());
							device.setCh3(buffer.readDouble());
							device.setCh4(buffer.readDouble());
							device.setCh5(buffer.readDouble());

							deviceList.add(device);

							// 0x03
							buffer.readByte();

							//buffer.slice(buffer.readerIndex(), buffer.writableBytes());
							buffer.slice();
							//device = null;
						} catch (Exception e) {
							e.printStackTrace();
							log.error(e.getMessage());

							log.error("Error Data HEX : {}", ByteUtils.byteBufToHexString(buffer, initReaderIndex, buffer.writerIndex()));

							this.setBufferInit(buffer, (byte) 0x02, false);
						}
					} else {
						this.setBufferInit(buffer, (byte) 0x02, true);
						break;
					}
				} else {
					this.setBufferInit(buffer, (byte) 0x02, false);
					break;
				}
			} else {
				buffer.clear();
				//ReferenceCountUtil.release(buffer);
				break;
			}
		}

//		if("WX-1Z".equals(this.deviceId) || "WX-1A".equals(this.deviceId)) {
//			log.info("남은 HEX : {}", ByteUtils.byteBufToHexString(buffer, buffer.readerIndex(), buffer.writerIndex()));
//		}

		//buffer.readerIndex(0);
		//buffer.clear();

		// buffer 초기화
		if(buffer.writerIndex()==0) {
			buffer.capacity(bufferCapacity);
		}
		ReferenceCountUtil.safeRelease(buffer);

		if(deviceList.size() > 0 ) {
			ctx.fireChannelRead(deviceList);
			//deviceList.clear();
		}
	}

	private void setBufferInit(ByteBuf buffer, byte b, boolean isInit) {
		int readerIndex = 0;

		if(!isInit) {
			readerIndex = buffer.readerIndex();
		}

		int index = buffer.indexOf(readerIndex, buffer.writerIndex(), b);
		if(index > -1) {
			buffer.readerIndex(index);
		} else {
			buffer.clear();
		}
	}
}