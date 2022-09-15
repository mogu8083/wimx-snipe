package com.ulalalab.snipe.common.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
//@Scope(value = "prototype")
@ChannelHandler.Sharable
public class ProcessHandler extends ChannelInboundHandlerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(ProcessHandler.class);
	private static Long receive = 0L;
	private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	//private int DATA_LENGTH = 1024;
	//private ByteBuf buff;

//	@Autowired
//	private RedisTemplate<String, Object> redisTemplate;
//
//	@Autowired
//	private JdbcTemplate jdbcTemplate;

	// 핸들러가 생성
//	@Override
//	public void handlerAdded(ChannelHandlerContext ctx) {
//		buff = ctx.alloc().buffer(DATA_LENGTH);
//	}
//
//	// 핸들러가 제거될 때 호출되는 메소드
//	@Override
//	public void handlerRemoved(ChannelHandlerContext ctx) {
//		buff = null;
//	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		channelGroup.add(channel);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object packet) {
		String msg = (String) packet;
		logger.info("받은 데이터 [" + (receive++) + ", 클라이언트 Count : " + channelGroup.size() + "] => " + msg);
	}

	// 수신 데이터 처리 완료
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
//		ctx.writeAndFlush(Unpooled.EMPTY_BUFFER) // 대기중인 메시지를 플러시하고 채널을 닫음
//			.addListener(ChannelFutureListener.CLOSE);
		//logger.info(this.getClass() + " => channelReadComplete");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error(this.getClass() + " / " +  cause.getCause());
		cause.printStackTrace();
		ctx.close();
	}
}