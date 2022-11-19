package com.ulalalab.snipe.infra.handler;

import com.ulalalab.snipe.device.model.Device;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

//@Component
@Slf4j(topic = "HTTP.HttpResponseHandler")
public class HttpResponse2Handler extends ChannelInboundHandlerAdapter {

    private ByteBuf byteBuf;
    private int contentLength = 0;
    private boolean isUri = true;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        log.info(msg.toString());

        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            contentLength = (int) HttpUtil.getContentLength(request);
            byteBuf = Unpooled.buffer(contentLength);

            String uri = request.uri();
            HttpMethod method = request.method();

            if(uri.equals("/device/info")) {
                if(method==HttpMethod.POST) {
                    //QueryStringDecoder querString = new QueryStringDecoder(uri);
                } else {
                    isUri = false;
                }
            } else {
                isUri = false;
            }

            if(!isUri) {
                ctx.close();
            }
        } else if(msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) msg;
            ByteBuf content = httpContent.content();

            byteBuf.writeBytes(content);

            if(byteBuf.writableBytes() == 0) {
                Device device = new Device();
                device.setDeviceId("WX-1S");
                device.setTime(System.currentTimeMillis());
                device.setCh1(50.0);
                device.setCh2(50.0);
                device.setCh3(50.0);
                device.setCh4(50.0);
                device.setCh5(50.0);

                JSONObject jsonObject = new JSONObject();

                FullHttpResponse response = this.getResponse(jsonObject);
                log.info("Http response : " +  response.content().toString());

                ctx.write(response);
                ctx.fireChannelRead(device);
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    /**
     * Response Get
     */
    private FullHttpResponse getResponse(JSONObject jsonObject) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, OK, Unpooled.wrappedBuffer(jsonObject.toString().getBytes()));

        response.headers().set(CONTENT_TYPE, "application/json; charset=UTF-8");
        response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
        response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        return response;
    }
}