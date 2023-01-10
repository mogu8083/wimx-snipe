package com.ulalalab.snipe.infra.handler;

import com.ulalalab.snipe.device.model.Device;
import com.ulalalab.snipe.infra.constant.DeviceCodeEnum;
import com.ulalalab.snipe.infra.util.CRC16ModubusUtils;
import com.ulalalab.snipe.infra.util.DevUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

@Component
@ChannelHandler.Sharable
@Scope(scopeName = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Slf4j(topic = "TCP.PacketHandler")
public class PacketHandler extends ChannelInboundHandlerAdapter {

	private DeviceCodeEnum deviceCodeEnum;
	private short deviceIndex = 0;
	private ByteBuf buffer;
	private byte[] remainBytes;
	private ByteBuf remainByteBuf;

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		buffer = ctx.alloc().heapBuffer(128);
	}

//	@Override
//	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
//		//ctx.alloc().heapBuffer().release();
//	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object packet) {
		WeakReference<Device> refDevice = new WeakReference<>(new Device());
		Device device = refDevice.get();

		ByteBuf in = (ByteBuf) packet;
		buffer = ctx.alloc().heapBuffer(128);

//		log.info("in : " + ByteBufUtil.prettyHexDump(in));

		if(remainByteBuf != null) {
			buffer.writeBytes(remainByteBuf);
			remainByteBuf = null;
		}
		buffer.writeBytes(in);
		in.clear();
		in.release();

//		log.info("남은 패킷과 병합 : " + ByteBufUtil.prettyHexDump(buffer));

		while(true) {
			try {
				if (buffer.readableBytes() > 1
						&& buffer.getShort(buffer.readerIndex()) == 0x1616) {

					// STX : 0x1616
					buffer.skipBytes(2);

					// Transaction ID
					short transactionId = buffer.readShort();

					// CMD (1 byte)
					byte cmd = buffer.readByte();

					// Timestamp (4 byte)
					int timestamp = buffer.readInt();

					// Device Code (2 byte)
					short deviceCode = buffer.readShort();

					// Device 등록 년
					byte deviceRegYear = buffer.readByte();

					// Device 등록 월
					byte deviceRegMonth = buffer.readByte();

					// Device index 2 Byte
					if (deviceIndex == 0) {
						deviceIndex = buffer.readShort();
					} else {
						buffer.skipBytes(2);
					}

					// Version 4 Byte
					float version = buffer.readFloat();

					// RSSI 1 byte
					byte rssi = buffer.readByte();

					// Data Length (2 Byte)
					short dataLength = buffer.readShort();

					// Data (4 * 채널수 Byte)
					int channelCount = dataLength / Float.BYTES;
					List<Float> channelDataList = new ArrayList<>();

					for (int i = 0; i < channelCount; i++) {
						float chData = buffer.readFloat();
						channelDataList.add(chData);
					}

					// CRC (2 Byte)
					int checkCRC = CRC16ModubusUtils.calc(ByteBufUtil.getBytes(buffer, 0, buffer.readerIndex()));
					int receiveCRC = buffer.readUnsignedShort();

					if (checkCRC != receiveCRC) {
						log.warn("CRC 일치 하지 않음.");
						throw new Exception();
					}

					// ETX (1 Byte)
					buffer.skipBytes(1);

					// Device Setting
					if (deviceCodeEnum == null) {
						deviceCodeEnum = DeviceCodeEnum.codeToDevice(deviceCode);
					}

					device.setTimestamp(timestamp);
					device.setTransactionId(transactionId);
					device.setDeviceCode(deviceCodeEnum);
					device.setDeviceIndex(deviceIndex);
					device.setDeviceRegYear(deviceRegYear);
					device.setDeviceRegMonth(deviceRegMonth);
					device.setDataLength(dataLength);
					device.setVersion(version);
					device.setRssi(rssi);
					device.setChannelDataList(channelDataList);

					if (DevUtils.isPrint(deviceIndex)) {
						//log.info(ByteBufUtil.prettyHexDump(buffer, 0, buffer.writerIndex()));
						log.info(buffer.toString());
						log.info(device.toString());
					}

					buffer.discardReadBytes();

					// Client 전송
					ctx.fireUserEventTriggered(transactionId);
					ctx.fireChannelRead(device);
				} else {
					// STX가 아닌경우
					this.setPacketDiscard(buffer);
					break;
				}
			} catch(Exception e) {
				DevUtils.printStackTrace(e);
				log.error(e.getMessage());
				log.error(ByteBufUtil.prettyHexDump(buffer));

				// 오류 인 경우
				this.setPacketDiscard(buffer);
				break;
			}
		}

//		log.info("남은.. : " + ByteBufUtil.prettyHexDump(buffer));
		buffer.readerIndex(0);

		if(buffer.readableBytes()==0) {
			buffer.clear();
		} else {
			remainByteBuf = Unpooled.buffer();
			remainByteBuf.writeBytes(buffer);
		}
		ReferenceCountUtil.safeRelease(buffer);
	}

	private void setPacketDiscard(ByteBuf buffer) {
		if(buffer.writerIndex() > 1) {
			int stx = buffer.indexOf(0, buffer.writerIndex(), (byte) 0x16);

			if(stx > -1 && buffer.getByte(stx+1)==0x16) {
				buffer.readerIndex(stx);
			} else {
				buffer.readerIndex(buffer.writerIndex());
			}
		}
		buffer.discardReadBytes();
	}
}