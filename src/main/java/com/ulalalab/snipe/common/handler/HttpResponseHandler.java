package com.ulalalab.snipe.common.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpResponseHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(HttpResponseHandler.class);

    private ByteBufToBytes byteBuf;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;

            if (HttpUtil.isContentLengthSet(request)) {
                byteBuf = new ByteBufToBytes((int) HttpUtil.getContentLength(request));

                Unpooled.buffer((int) HttpUtil.getContentLength(request));
            }
        }

        try {
            // 데이터 가공
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("test", "1111");

            if (msg instanceof HttpContent) {
                HttpContent httpContent = (HttpContent) msg;
                ByteBuf content = httpContent.content();

                byteBuf.reading(content);
                content.release();

                if (byteBuf.isEnd()) {
                    String resultStr = new String(byteBuf.readFull());
                    logger.info("Request Body : " + resultStr);

                    FullHttpResponse response = new DefaultFullHttpResponse(
                            HTTP_1_1, OK, Unpooled.wrappedBuffer(jsonObject.toString().getBytes()));
                    response.headers().set(CONTENT_TYPE, "application/json; charset=UTF-8");
                    response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
                    response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);

                    ctx.write(response);
                    ctx.flush();
                    ctx.close();
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
            ctx.fireChannelRead(msg);
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