package com.ulalalab.snipe.device.controller;//package com.ulalalab.snipe.device.service;

import com.ulalalab.snipe.device.model.Response;
import com.ulalalab.snipe.infra.manage.ChannelManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DeviceController {

    private ChannelManager channelManager = ChannelManager.getInstance();


    /**
     * 클라이언트 전체 목록
     */
    @GetMapping("/device/info")
	public Response deviceInfo() {
        return new Response<>(channelManager.getChannelGroup());
    }

    /**
     * 해당 클라이언트 연결 해제
     */
    @GetMapping("/device/disconnect")
    public void deviceDisconnect(@RequestParam String deviceId) throws Exception {
        channelManager.removeChannel(deviceId);
    }

    /**
     * 장비 계산식 변경 PUSH
     */
//    @GetMapping("/device/calculate/push")
//    public Response calculatePush(@RequestParam String deviceId) throws Exception {
//        int resultCnt = channelManager.calculatePush(deviceId);
//        //Mono mono;
//        Response response;
//
//        if(resultCnt == 0) {
//            response = new Response(Response.Code.FAIL, deviceId + " 해당 장비가 없습니다.");
//        } else {
//            response = new Response(deviceId + " 해당 장비에 계산식 적용 요청을 하였습니다.");
//        }
//        return response;
//    }
}