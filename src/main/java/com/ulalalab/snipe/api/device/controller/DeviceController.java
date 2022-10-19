package com.ulalalab.snipe.api.device.controller;//package com.ulalalab.snipe.device.service;

import com.ulalalab.snipe.device.model.Response;
import com.ulalalab.snipe.infra.manage.ChannelManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/device")
public class DeviceController {

    private ChannelManager channelManager = ChannelManager.getInstance();

    /**
     * 클라이언트 전체 목록
     */
    @GetMapping("/info")
	public Mono deviceInfo() {
//        return Flux.just(new Response<>(channelManager.getChannelGroup()))
//                .doOnNext(e->log.info("ttttttttttttttttttt"));
        return Mono.just(new Response<>(channelManager.getChannelGroup()));
    }

    /**
     * 해당 클라이언트 연결 해제
     * @param deviceId
     */
    @GetMapping("/disconnect")
    public void deviceDisconnect(@RequestParam String deviceId) throws Exception {
        channelManager.removeChannel(deviceId);
    }

    /**
     * 장비 계산식 변경 PUSH
     * @param deviceId
     */
    @GetMapping("/calculate/push")
    public Mono calculatePush(@RequestParam String deviceId) throws Exception {
        int resultCnt = channelManager.calculatePush(deviceId);
        Mono mono = null;

        if(resultCnt == 0) {
            mono = Mono.just(new Response(Response.Code.FAIL, deviceId + " 해당 장비가 없습니다."));
        } else {
            mono = Mono.just(new Response(deviceId + " 해당 장비에 계산식 적용 요청을 하였습니다."));
        }
        return mono;
    }
}