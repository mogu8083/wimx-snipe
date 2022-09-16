package com.ulalalab.snipe.common.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpResponseHandler extends SimpleChannelInboundHandler<HttpObject> {

    private static final Logger logger = LoggerFactory.getLogger(HttpResponseHandler.class);

    private HttpRequest request;
    StringBuilder responseData = new StringBuilder();

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        HttpResponse response = (HttpResponse) msg;

        if (msg instanceof HttpResponse) {

            System.out.println("STATUS: " + response.status());
            System.out.println("VERSION: " + response.protocolVersion());
            System.out.println();

            response.headers().set(CONTENT_TYPE, "application/json; charset=UTF-8");
            //response.headers().set(CONTENT_LENGTH, response.content().readableBytes());

            if (!response.headers().isEmpty()) {
                for (CharSequence name: response.headers().names()) {
                    for (CharSequence value: response.headers().getAll(name)) {
                        System.out.println("HEADER: " + name + " = " + value);
                    }
                }
            }
            System.out.println("CONTENT [");
        }

        if (msg instanceof HttpContent) {
            HttpContent content = (HttpContent) msg;

            //System.out.print(content.content().toString(StandardCharsets.UTF_8));

            if (content instanceof LastHttpContent) {
                System.out.println();
                System.out.println("] END OF CONTENT");
                ctx.writeAndFlush(response);
                ctx.close();
            }
        }
//        if (msg instanceof HttpRequest) {
//            HttpRequest request = this.request = (HttpRequest) msg;
//
//            if (HttpUtil.is100ContinueExpected(request)) {
//                writeResponse(ctx);
//            }
//            responseData.setLength(0);
//            responseData.append(this.formatParams(request));
//        }
//        //responseData.append(this.evaluateDecoderResult(request));
//
//        if (msg instanceof HttpContent) {
//            HttpContent httpContent = (HttpContent) msg;
//
//            responseData.append(this.formatBody(httpContent));
//            //responseData.append(RequestUtils.evaluateDecoderResult(request));
//
//            if (msg instanceof LastHttpContent) {
//                LastHttpContent trailer = (LastHttpContent) msg;
//                responseData.append(this.prepareLastResponse(request, trailer));
//                //responseData.append("test");
//                writeResponse(ctx, trailer, responseData);
//            }
//        }
    }

    private void writeResponse(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, CONTINUE, Unpooled.EMPTY_BUFFER);
        ctx.write(response);
    }

    private void writeResponse(ChannelHandlerContext ctx, LastHttpContent trailer,
                               StringBuilder responseData) {
        boolean keepAlive = HttpUtil.isKeepAlive(request);

        logger.info("##@@ keepAlive : " + keepAlive);

        FullHttpResponse httpResponse = new DefaultFullHttpResponse(HTTP_1_1,
                ((HttpObject) trailer).decoderResult().isSuccess() ? OK : BAD_REQUEST,
                Unpooled.copiedBuffer(responseData.toString(), StandardCharsets.UTF_8));

        httpResponse.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");



        if (keepAlive) {
            httpResponse.headers().setInt(CONTENT_LENGTH,
                    httpResponse.content().readableBytes());
            httpResponse.headers().set(HttpHeaderNames.CONNECTION,
                    HttpHeaderValues.KEEP_ALIVE);
        }

        ByteBuf buffer = Unpooled.copiedBuffer(responseData, StandardCharsets.UTF_8);

        httpResponse.content().writeBytes(buffer);
        buffer.release();

        if (!keepAlive) {
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

    private StringBuilder formatBody(HttpContent httpContent) {
        StringBuilder responseData = new StringBuilder();
        ByteBuf content = httpContent.content();
        if (content.isReadable()) {
            responseData
                    .append(content.toString(StandardCharsets.UTF_8).toUpperCase())
                    .append("\r\n");
        }
        return responseData;
    }

    private StringBuilder formatParams(HttpRequest request) {
        StringBuilder responseData = new StringBuilder();
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
        Map<String, List<String>> params = queryStringDecoder.parameters();
        if (!params.isEmpty()) {
            for (Map.Entry<String, List<String>> p : params.entrySet()) {
                String key = p.getKey();
                List<String> vals = p.getValue();
                for (String val : vals) {
                    responseData.append("Parameter: ").append(key.toUpperCase()).append(" = ")
                            .append(val.toUpperCase()).append("\r\n");
                }
            }
            responseData.append("\r\n");
        }
        return responseData;
    }

    private StringBuilder prepareLastResponse(HttpRequest request, LastHttpContent trailer) {
        StringBuilder responseData = new StringBuilder();
        responseData.append("Good Bye!\r\n");

        if (!trailer.trailingHeaders().isEmpty()) {
            responseData.append("\r\n");
            for (CharSequence name : trailer.trailingHeaders().names()) {
                for (CharSequence value : trailer.trailingHeaders().getAll(name)) {
                    responseData.append("P.S. Trailing Header: ");
                    responseData.append(name).append(" = ").append(value).append("\r\n");
                }
            }
            responseData.append("\r\n");
        }
        return responseData;
    }
}