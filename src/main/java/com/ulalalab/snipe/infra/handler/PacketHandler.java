package com.ulalalab.snipe.infra.handler;

import com.ulalalab.snipe.device.model.Device;
import com.ulalalab.snipe.infra.util.DevUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;

@Slf4j(topic = "TCP.PacketHandler")
public class PacketHandler extends ChannelInboundHandlerAdapter {

	private boolean isDevice = false;
	private ByteBuf buffer;

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		buffer = ctx.alloc().heapBuffer(64);
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		ctx.alloc().heapBuffer().release();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object packet) {
		//WeakReference<Device> refDevice = new WeakReference<>(new Device());
		//Device device = refDevice.get();
		Device device = new Device();

		String deviceId = null;

		ByteBuf in = (ByteBuf) packet;
		buffer = ctx.alloc().heapBuffer(65);
		buffer.writeBytes(in);
		in.release();
		in = null;

		if (DevUtils.isTest()) {
			log.info("-------------받은 HEX----------");
			System.out.println(ByteBufUtil.prettyHexDump(buffer, 0, buffer.writerIndex()));
			log.info(buffer.toString());
		}

		try {
			if (buffer.getByte(0) == 0x02) {
				if (buffer.getByte(buffer.writerIndex() - 1) == 0x03) {
					// 0x02 STX
					buffer.skipBytes(1);
					int deviceSize = buffer.readInt();
					deviceId = buffer.toString(buffer.readerIndex(), deviceSize, StandardCharsets.UTF_8);

					if (DevUtils.isPrint(deviceId)) {
						log.info(buffer.toString());
						log.info(ByteBufUtil.prettyHexDump(buffer, 0, buffer.writerIndex()));
					}
					buffer.skipBytes(deviceSize);

					device.setDeviceId(deviceId);
					device.setTime(buffer.readLong());
					device.setCh1(buffer.readDouble());
					device.setCh2(buffer.readDouble());
					device.setCh3(buffer.readDouble());
					device.setCh4(buffer.readDouble());
					device.setCh5(buffer.readDouble());

					// 0x03 ETX
					buffer.skipBytes(1);
					isDevice = true;
				} else {
					isDevice = false;
					buffer.readerIndex(0);
				}
			} else {
				isDevice = false;
				buffer.clear();
			}
		} catch (Exception e) {
			isDevice = false;
			log.warn(e.getMessage());

			if (DevUtils.isTest()) {
				e.printStackTrace();
			}
		} finally {
			if (DevUtils.isTest()) {
//				log.info("-------------남은 HEX----------");
//				System.out.println(ByteBufUtil.prettyHexDump(buffer, 0, buffer.writerIndex()));
//				log.info(buffer.toString());
			}

			if (isDevice) {
				ctx.fireChannelRead(device);

				if (buffer.writerIndex() - buffer.readerIndex() == 0) {
					buffer.release();
				}
			}
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		buffer.clear();
		buffer.release();
	}
}