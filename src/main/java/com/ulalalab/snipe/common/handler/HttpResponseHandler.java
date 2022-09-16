package com.ulalalab.snipe.common.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpResponseHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(HttpResponseHandler.class);

    private ByteBufToBytes reader;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;

//            System.out.println("messageType:"+ request.headers().get("messageType"));
//            System.out.println("businessType:"+ request.headers().get("businessType"));

            if (HttpUtil.isContentLengthSet(request)) {
                reader = new ByteBufToBytes((int) HttpUtil.getContentLength(request));
            }
        }

        if (msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) msg;
            ByteBuf content = httpContent.content();
            reader.reading(content);
            content.release();
            if (reader.isEnd()) {
                String resultStr = new String(reader.readFull());
                System.out.println("Client said:" + resultStr);
                FullHttpResponse response = new DefaultFullHttpResponse(
                        HTTP_1_1, OK, Unpooled.wrappedBuffer("I am ok"
                        .getBytes()));
                response.headers().set(CONTENT_TYPE, "text/plain");
                response.headers().set(CONTENT_LENGTH,
                        response.content().readableBytes());
                response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                ctx.write(response);
                ctx.flush();
            }
        }
    }

    public class ByteBufToBytes {
        private ByteBuf temp;
        private boolean end = true;
        public ByteBufToBytes(int length) {
            temp = Unpooled.buffer(length);
        }
        public void reading(ByteBuf datas) {
            datas.readBytes(temp, datas.readableBytes());
            if (this.temp.writableBytes() != 0) {
                end = false;
            } else {
                end = true;
            }
        }
        public boolean isEnd() {
            return end;
        }
        public byte[] readFull() {
            if (end) {
                byte[] contentByte = new byte[this.temp.readableBytes()];
                this.temp.readBytes(contentByte);
                this.temp.release();
                return contentByte;
            } else {
                return null;
            }
        }
        public byte[] read(ByteBuf datas) {
            byte[] bytes = new byte[datas.readableBytes()];
            datas.readBytes(bytes);
            return bytes;
        }
    }
}