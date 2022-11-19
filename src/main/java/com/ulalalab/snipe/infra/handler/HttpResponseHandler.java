//package com.ulalalab.snipe.infra.handler;
//
//import com.ulalalab.snipe.device.model.Device;
//import io.netty.buffer.ByteBuf;
//import io.netty.buffer.Unpooled;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.ChannelInboundHandlerAdapter;
//import io.netty.handler.codec.http.*;
//import lombok.extern.slf4j.Slf4j;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.boot.configurationprocessor.json.JSONObject;
//import org.springframework.util.StringUtils;
//
//import java.net.URI;
//
//import static io.netty.handler.codec.http.HttpHeaderNames.*;
//import static io.netty.handler.codec.http.HttpResponseStatus.OK;
//import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
//
////@Component
//@Slf4j(topic = "HTTP.HttpResponseHandler")
//public class HttpResponseHandler extends ChannelInboundHandlerAdapter {
//
//    private ByteBufToBytes byteBuf;
//
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//
//        log.info("@@## : {}", msg.toString());
//
//        if (msg instanceof HttpRequest) {
//            HttpRequest request = (HttpRequest) msg;
//            URI uri = new URI(request.uri());
//
//            // 디바이스
//            if(uri.equals("/device/info")) {
//
//                // 정보
//                if(request.method()==HttpMethod.GET) {
//                    QueryStringDecoder querString = new QueryStringDecoder(uri);
//
//                }
//            }
//
//            if (HttpUtil.isContentLengthSet(request)) {
//                byteBuf = new ByteBufToBytes((int) HttpUtil.getContentLength(request));
//
//                Unpooled.buffer((int) HttpUtil.getContentLength(request));
//            }
//        }
//
//        try {
//            // 데이터 가공
//            JSONObject jsonObject = new JSONObject();
//
//            // TODO : Test
//            jsonObject.put("test", "1111");
//            //
//
//            if (msg instanceof HttpContent) {
//                HttpContent httpContent = (HttpContent) msg;
//                ByteBuf content = httpContent.content();
//
//                if(content.readableBytes() > 0) {
//                    byteBuf.reading(content);
//                }
//                content.release();
//
//                if (byteBuf!=null && byteBuf.isEnd()) {
//                    String resultStr = new String(byteBuf.readFull());
//                }
//
//                FullHttpResponse response = this.getResponse(jsonObject);
//
//                log.info("Http response : " +  response.content().toString());
//
//                Device device = new Device();
//                device.setDeviceId("WX-1S");
//                device.setTime(System.currentTimeMillis());
//                device.setCh1(50.0);
//                device.setCh2(50.0);
//                device.setCh3(50.0);
//                device.setCh4(50.0);
//                device.setCh5(50.0);
//
//                ctx.write(response);
//                ctx.fireChannelRead(device);
//            }
//        } catch(Exception e) {
//            e.printStackTrace();
//            ctx.close();
//        }
//    }
//
//    @Override
//    public void channelReadComplete(ChannelHandlerContext ctx) {
//        ctx.flush();
//    }
//
//    /**
//     * Response Get
//     * @param jsonObject : Json 형식 문자열
//     * @return FullHttpResponse
//     */
//    private FullHttpResponse getResponse(JSONObject jsonObject) {
//        FullHttpResponse response = new DefaultFullHttpResponse(
//                HTTP_1_1, OK, Unpooled.wrappedBuffer(jsonObject.toString().getBytes()));
//
//        response.headers().set(CONTENT_TYPE, "application/json; charset=UTF-8");
//        response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
//        response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
//        return response;
//    }
//
//    public class ByteBufToBytes {
//        private ByteBuf temp;
//        private boolean end = true;
//
//        public ByteBufToBytes(int length) {
//            temp = Unpooled.buffer(length);
//        }
//
//        public void reading(ByteBuf datas) {
//            datas.readBytes(temp, datas.readableBytes());
//            if (this.temp.writableBytes() != 0) {
//                end = false;
//            } else {
//                end = true;
//            }
//        }
//
//        public boolean isEnd() {
//            return end;
//        }
//
//        public byte[] readFull() {
//            if (end) {
//                byte[] contentByte = new byte[this.temp.readableBytes()];
//                this.temp.readBytes(contentByte);
//                this.temp.release();
//                return contentByte;
//            } else {
//                return null;
//            }
//        }
//
//        public byte[] read(ByteBuf datas) {
//            byte[] bytes = new byte[datas.readableBytes()];
//            datas.readBytes(bytes);
//            return bytes;
//        }
//    }
//}