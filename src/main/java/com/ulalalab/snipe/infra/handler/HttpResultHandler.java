//package com.ulalalab.snipe.infra.handler;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import com.ulalalab.snipe.common.service.CommonService;
//import com.ulalalab.snipe.device.model.Response;
//import com.ulalalab.snipe.device.service.CommandService;
//import com.ulalalab.snipe.device.service.DeviceService;
//import com.ulalalab.snipe.infra.channel.SpChannelGroup;
//import com.ulalalab.snipe.infra.manager.EventManager;
//import com.ulalalab.snipe.infra.util.BeansUtils;
//import io.netty.buffer.Unpooled;
//import io.netty.channel.*;
//import io.netty.handler.codec.http.*;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.util.StringUtils;
//
//import java.util.List;
//import static io.netty.handler.codec.http.HttpHeaderNames.*;
//import static io.netty.handler.codec.http.HttpResponseStatus.OK;
//import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
//
//@Slf4j(topic = "HTTP.HttpResultHandler")
//public class HttpResultHandler extends ChannelInboundHandlerAdapter {
//
//    private final SpChannelGroup spChannelGroup = EventManager.getInstance().getSpChannelGroup();
//
//    String uri = "";
//    HttpMethod method;
//    HttpRequest request;
//
//    private final DeviceService deviceService;
//    private final CommonService commonService;
//    private final CommandService commandService;
//
//    public HttpResultHandler() {
//        this.deviceService = (DeviceService) BeansUtils.getBean("deviceService");
//        this.commonService = (CommonService) BeansUtils.getBean("commonService");
//        this.commandService = (CommandService) BeansUtils.getBean("commandService");
//    }
//
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        //ByteBuf byteBuf;
//        //int contentLength = 0;
//        //boolean isUri = true;
//
//        log.info(msg.toString());
//
//        if (msg instanceof HttpRequest) {
//            request = (HttpRequest) msg;
//
//            uri = request.uri();
//            method = request.method();
//        } else if(msg instanceof HttpContent) {
//
//            // TODO : Body가 대량일 경우 작업 필요
//
//            if (msg instanceof LastHttpContent) {
//
//                // 장비
//                if(uri.startsWith("/device")) {
//
//                    // 장비 정보
//                    if (uri.equals("/device/info") && method == HttpMethod.POST) {
//                        Response responseString = new Response<>(spChannelGroup.getChannelInfoList());
//                        this.writeResponse(ctx, responseString);
//                    }
//
//                    // 장비 해제
//                    else if(uri.contains("/device/disconnect?") && method == HttpMethod.GET) {
//                        QueryStringDecoder decoder = new QueryStringDecoder(uri);
//                        short deviceId = Short.parseShort(this.getParam(decoder, "deviceId"));
//
//                        boolean result = spChannelGroup.channelDisconnect(deviceId);
//                        String responseMessage = deviceId + " 장비 연결이 해제되었습니다.";
//
//                        if(!result) {
//                            responseMessage = deviceId + " 해당 장비가 존재 하지 않습니다.";
//                        }
//
//                        Response responseString = new Response<>(responseMessage);
//                        this.writeResponse(ctx, responseString);
//                    }
//
//                    // 장비 Reboot
//                    else if(uri.contains("/device/reboot?") && method == HttpMethod.GET) {
//                        QueryStringDecoder decoder = new QueryStringDecoder(uri);
//                        String deviceId = this.getParam(decoder, "deviceId");
//                        Channel channel = spChannelGroup.getChannel(Integer.valueOf(deviceId));
//
//                        if(!StringUtils.hasText(deviceId) || channel == null) {
//                            Response responseString = new Response<>(deviceId + " -> 해당 장비 ID가 존재하지 않습니다.");
//                            this.writeResponse(ctx, responseString);
//                        } else {
//                            boolean isSend = commandService.reboot(channel);
//
//                            if(isSend) {
//                                Response responseString = new Response<>(deviceId + " -> 장비 재부팅을 요청하였습니다.");
//                                this.writeResponse(ctx, responseString);
//                            }
//                        }
//                    }
//                }
//
//                // 서버 정보
//                else if (uri.equals("/server/info") && method == HttpMethod.GET) {
//                    Response responseString = new Response<>(commonService.serverInfo());
//                    this.writeResponse(ctx, responseString);
//                }
//
//                else {
//                    Response responseString = new Response<>(Response.Code.FAIL, "API Not Found");
//                    this.writeResponse(ctx, responseString);
//                }
//            }
//        }
//    }
//
//    @Override
//    public void channelReadComplete(ChannelHandlerContext ctx) {
//        //ctx.flush();
//        //ctx.close();
//    }
//
//
//
//    /**
//     * Response 생성
//     * @param ctx
//     * @param responseString
//     * @return ChannelFuture
//     */
//    private void writeResponse(ChannelHandlerContext ctx, Response responseString) throws Exception {
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.registerModule(new JavaTimeModule());
//
//        String result = mapper.writeValueAsString(responseString);
//        FullHttpResponse response = this.getResponse(result);
//
//        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
//
//        //return ctx.write(response).;
//    }
//
//    /**
//     * Response Get
//     */
//    private FullHttpResponse getResponse(String resultResponse) {
//        FullHttpResponse response = new DefaultFullHttpResponse(
//                HTTP_1_1, OK, Unpooled.wrappedBuffer(resultResponse.getBytes()));
//
//        HttpHeaders headers = response.headers();
//        headers.set(CONTENT_TYPE, "application/json; charset=UTF-8");
//        headers.set(CONTENT_LENGTH, response.content().readableBytes());
//        //headers.set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
//
//        return response;
//    }
//
//    private String getParam(QueryStringDecoder decoder, String paramString) {
//        List<String> param = decoder.parameters().get(paramString);
//        if (param == null) {
//            return null;
//        }
//        return param.get(0);
//    }
//}