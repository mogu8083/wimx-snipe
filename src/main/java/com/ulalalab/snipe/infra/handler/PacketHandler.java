package com.ulalalab.snipe.infra.handler;

import com.ulalalab.snipe.device.model.Device;
import com.ulalalab.snipe.device.model.DeviceCode;
import com.ulalalab.snipe.infra.util.DevUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.lang.ref.WeakReference;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
@ChannelHandler.Sharable
@Slf4j(topic = "TCP.PacketHandler")
public class PacketHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		ctx.alloc().heapBuffer(200);
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		ctx.alloc().heapBuffer().release();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object packet) {
		ByteBuf buffer;

		WeakReference<Device> refDevice = new WeakReference<>(new Device());
		Device device = refDevice.get();

		ByteBuf in = (ByteBuf) packet;
		buffer = ctx.alloc().heapBuffer(128);
		buffer.writeBytes(in);
		in.release();
		in.unwrap();

		boolean isDevice = false;
		ByteBuf clientBuf = Unpooled.buffer(14);

		// 초기 Command
		if(buffer.getByte(4)==0x00) {
			clientBuf.writeByte(0x16);
			clientBuf.writeByte(0x16);
			clientBuf.writeShort(0x0000);
			clientBuf.writeByte(0x00);

			clientBuf.writeInt((int) Instant.now().getEpochSecond());
			clientBuf.writeByte(0x00);
			clientBuf.writeByte(0x00);
			clientBuf.writeByte(0x00);
			clientBuf.writeByte(0xF5);
			buffer.slice();
		}

		while(true) {
			try {
				//log.info("hex : " + ByteBufUtil.prettyHexDump(buffer));

				if(buffer.getShort(buffer.readerIndex()) != 0x1616) {
					break;
				}

				if (buffer.getShort(buffer.readerIndex()) == 0x1616) {

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

					// TODO : Device 등록년월
					buffer.skipBytes(2);

					// Device index 2 Byte
					short deviceIndex = buffer.readShort();

					// Version 4 Byte
					float version = buffer.readFloat();

					// RSSI 1 byte
					byte rssi = buffer.readByte();

					// Data Length (2 Byte)
					short dataLength = buffer.readShort();

					// Data Length (2 Byte)
					int channelCount = dataLength / Float.BYTES;
					List<Float> channelDataList = new ArrayList<>();

					for(int i = 0; i < channelCount; i++) {
						float chData = buffer.readFloat();
						channelDataList.add(chData);
					}

					// CRC (2 Byte)
					buffer.skipBytes(2);

					// ETX (1 Byte)
					buffer.skipBytes(1);

					buffer.slice();

					// Device Setting
					device.setTimestamp(timestamp);
					device.setDeviceCode(DeviceCode.codeToDevice(deviceCode));
					device.setDeviceIndex(deviceIndex);
					device.setDataLength(dataLength);
					device.setVersion(version);
					device.setRssi(rssi);
					device.setChannelDataList(channelDataList);

					ctx.fireChannelRead(device);

					// Client ByteBuf Setting
					// TODO : 응답 시간을 찍어야 할지, 시스템 시간을 찍어야 할지 확인 필요
					int receiveTimestamp = (int) Instant.now().getEpochSecond();

					clientBuf.writeByte(0x16);
					clientBuf.writeByte(0x16);
					clientBuf.writeShort(transactionId);
					clientBuf.writeByte(0x01);
					clientBuf.writeInt(timestamp);
					clientBuf.writeByte(0x00);
					clientBuf.writeByte(0x00);

					// TODO : CRC 값 2byte 계산 필요
					clientBuf.writeByte(0x00);
					clientBuf.writeByte(0x00);
					clientBuf.writeByte(0xF5);

					ctx.writeAndFlush(clientBuf);
					clientBuf.unwrap();

					if(DevUtils.isPrint2(deviceIndex)) {
						log.info(ByteBufUtil.prettyHexDump(buffer, 0, buffer.writerIndex()));
						log.info(buffer.toString());
						log.info(device.toString());
					}
				}
			} catch(Exception e) {
				e.printStackTrace();
				log.error(e.getMessage());
				buffer.clear();
			}
		}
		buffer.release();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ByteBuf buffer = ctx.channel().alloc().buffer();
		buffer.clear();
		buffer.release();
	}
}