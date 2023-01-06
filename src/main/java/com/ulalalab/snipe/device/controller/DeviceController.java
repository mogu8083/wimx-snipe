package com.ulalalab.snipe.device.controller;//package com.ulalalab.snipe.device.service;

import com.ulalalab.snipe.device.model.Response;
import com.ulalalab.snipe.device.service.CommandService;
import com.ulalalab.snipe.infra.channel.SpChannelGroup;
import com.ulalalab.snipe.infra.manager.InstanceManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DeviceController {

    private final SpChannelGroup spChannelGroup = InstanceManager.getInstance().getSpChannelGroup();

    private final CommandService commnadService;

    /**
     * 장비 전체 목록
     */
    @GetMapping("/device/info")
	public Mono<Response> deviceInfo() {
        return Mono.just(new Response<>(spChannelGroup.getChannelInfoList()));
    }

    /**
     * 장비 연결 해제
     */
    @PutMapping("/device/disconnect/{deviceIndex}")
    public Mono<Response> deviceDisconnect(@PathVariable short deviceIndex) throws Exception {
        boolean result = spChannelGroup.channelDisconnect(deviceIndex);

        String responseMessage = deviceIndex + " 장비 연결이 해제되었습니다.";

        if(!result) {
            responseMessage = deviceIndex + " 해당 장비가 존재 하지 않습니다.";
        }
        return Mono.just(new Response<>(responseMessage));
    }

    /**
     * 장비 Reboot
     */
    @PutMapping("/device/reboot/{deviceIndex}")
    public Mono<Response> deviceReboot(@PathVariable short deviceIndex) throws Exception {
        String responseMessage = "";
        boolean isResult = commnadService.reboot(deviceIndex);
        Response response = null;

        if(isResult) {
            response = new Response(deviceIndex + " 장비 재부팅을 요청하였습니다.");
        } else {
            response = new Response(Response.Code.FAIL, "장비 재부팅을 실패하였습니다.");
        }
        return Mono.just(response);
    }
}