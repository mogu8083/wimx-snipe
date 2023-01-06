package com.ulalalab.snipe.infra.handler;

import com.ulalalab.snipe.device.model.Device;
import com.ulalalab.snipe.infra.constant.DeviceCodeEnum;
import com.ulalalab.snipe.infra.util.CRC16ModubusUtils;
import com.ulalalab.snipe.infra.util.DevUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.*;
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

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		//ctx.alloc().heapBuffer(200);
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		//ctx.alloc().heapBuffer().release();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object packet) {
		WeakReference<Device> refDevice = new WeakReference<>(new Device());
		Device device = refDevice.get();

		ByteBuf in = (ByteBuf) packet;
		ByteBuf buffer = ctx.alloc().heapBuffer(128);
		buffer.writeBytes(in);
		in.release();

		while(true) {
			try {
				if(buffer.writerIndex()==0 || buffer.getShort(buffer.readerIndex()) != 0x1616) {
					break;
				}

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
				if(deviceIndex == 0) {
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

				for(int i = 0; i < channelCount; i++) {
					float chData = buffer.readFloat();
					channelDataList.add(chData);
				}

				// CRC (2 Byte)
				int checkCRC = CRC16ModubusUtils.calc(ByteBufUtil.getBytes(buffer, 0, buffer.readerIndex()));
				int receiveCRC = buffer.readUnsignedShort();

				if(checkCRC != receiveCRC) {
					log.warn("CRC 일치 하지 않음.");
					throw new Exception();
				}

				// ETX (1 Byte)
				buffer.skipBytes(1);

				// Device Setting
				if(deviceCodeEnum == null) {
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

				if(DevUtils.isPrint2(deviceIndex)) {
					//log.info(ByteBufUtil.prettyHexDump(buffer, 0, buffer.writerIndex()));
					log.info(buffer.toString());
					log.info(device.toString());
				}

				buffer.discardReadBytes();

				// Client 전송
				ctx.fireUserEventTriggered(transactionId);
				ctx.fireChannelRead(device);
			} catch(Exception e) {
				//e.printStackTrace();
				log.error(e.getMessage());
				log.error(ByteBufUtil.prettyHexDump(buffer));

				// 오류 인 경우
				int stx = buffer.indexOf(0, buffer.writerIndex(), (byte) 0x1616);

				if(stx > 0) {
					buffer.readerIndex(stx);
				}
				buffer.discardReadBytes();
				//break;
			}
		}
		buffer.clear();
		buffer.release();
	}
}