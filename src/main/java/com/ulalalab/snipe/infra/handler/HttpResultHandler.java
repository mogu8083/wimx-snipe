package com.ulalalab.snipe.infra.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ulalalab.snipe.device.model.Device;
import com.ulalalab.snipe.device.model.Response;
import com.ulalalab.snipe.device.service.DeviceService;
import com.ulalalab.snipe.infra.manage.ChannelManager;
import com.ulalalab.snipe.infra.manage.InfluxDBManager;
import com.ulalalab.snipe.infra.util.BeansUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.influxdb.InfluxDBTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@Slf4j(topic = "HTTP.HttpResponseHandler")
public class HttpResultHandler extends ChannelInboundHandlerAdapter {

    String uri = "";
    HttpMethod method;
    HttpRequest request;

    private ChannelManager channelManager = ChannelManager.getInstance();

    private final DeviceService deviceService;

    public HttpResultHandler() {
        this.deviceService = (DeviceService) BeansUtils.getBean("deviceService");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf;
        int contentLength = 0;
        boolean isUri = true;

        log.info(msg.toString());

        if (msg instanceof HttpRequest) {
            request = (HttpRequest) msg;

            uri = request.uri();
            method = request.method();
//            if(uri.equals("/device/info")) {
//                if(method==HttpMethod.POST) {
//                    //QueryStringDecoder querString = new QueryStringDecoder(uri);
//                } else {
//                    isUri = false;
//                }
//            } else {
//                isUri = false;
//            }

//            if(isUri) {
//                contentLength = (int) HttpUtil.getContentLength(request);
//                byteBuf = Unpooled.buffer(contentLength);
//            }
        } else if(msg instanceof HttpContent) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            if (uri.equals("/device/info") && method == HttpMethod.POST) {
                Response responseString = new Response<>(channelManager.getChannelGroup());
                String result = mapper.writeValueAsString(responseString);
                FullHttpResponse response = this.getResponse(result);
                ctx.writeAndFlush(response);
                ctx.close();
            } else {
                Response responseString = new Response<>(Response.Code.FAIL, "API Not Found");
                String result = mapper.writeValueAsString(responseString);
                FullHttpResponse response = this.getResponse(result);
                ctx.writeAndFlush(response);
                ctx.close();
            }
        } else {
            ObjectMapper mapper = new ObjectMapper();
            Response responseString = new Response<>(Response.Code.FAIL, "API Not Found22");
            String result = mapper.writeValueAsString(responseString);
            FullHttpResponse response = this.getResponse(result);
            ctx.writeAndFlush(response);
            ctx.close();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
        ctx.close();
    }

    /**
     * Response Get
     */
    private FullHttpResponse getResponse(String resultResponse) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, OK, Unpooled.wrappedBuffer(resultResponse.getBytes()));

        response.headers().set(CONTENT_TYPE, "application/json; charset=UTF-8");
        response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
        response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        return response;
    }
}