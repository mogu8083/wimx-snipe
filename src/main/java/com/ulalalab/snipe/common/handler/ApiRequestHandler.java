package com.ulalalab.snipe.common.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.rtsp.RtspResponseStatuses;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaderNames.*;

public class ApiRequestHandler extends SimpleChannelInboundHandler<FullHttpMessage> {

    private static final Logger logger = LoggerFactory.getLogger(ApiRequestHandler.class);

    private HttpRequest request;
    private JSONObject jsonObject;
    private HttpPostRequestDecoder decoder;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpMessage msg) throws Exception {
        jsonObject = new JSONObject();
        jsonObject.put("test", "111");

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK
            , Unpooled.copiedBuffer(jsonObject.toString(), CharsetUtil.UTF_8));

        response.headers().set(CONTENT_TYPE, "application/json; charset=UTF-8");
        response.headers().set(CONTENT_LENGTH, jsonObject.toString().getBytes().length);
        response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);

        ctx.write(response);
        //ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        logger.info("Http 요청 처리 완료");
        ctx.flush();
    }
}