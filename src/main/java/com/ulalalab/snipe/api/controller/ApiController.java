package com.ulalalab.snipe.api.controller;//package com.ulalalab.snipe.device.service;

import com.ulalalab.snipe.api.model.Response;
import com.ulalalab.snipe.api.service.CommandService;
import com.ulalalab.snipe.api.service.SystemService;
import com.ulalalab.snipe.device.model.SpChannelGroup;
import com.ulalalab.snipe.infra.manager.InstanceManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ApiController {

    private final SpChannelGroup spChannelGroup = InstanceManager.getInstance().getSpChannelGroup();

    private final CommandService commnadService;
    private final SystemService systemService;

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
     * 장비 업데이트
     */
    @PutMapping("/device/update/{deviceIndex}")
    public Mono<Response> deviceUpdate(@PathVariable short deviceIndex) throws Exception {
        return Mono.just(commnadService.update(deviceIndex));
    }

    /**
     * 장비 Reboot
     */
    @PutMapping("/device/reboot/{deviceIndex}")
    public Mono<Response> deviceReboot(@PathVariable short deviceIndex) throws Exception {
        return Mono.just(commnadService.reboot(deviceIndex));
    }

    /**
     * 서버 정보
     */
    @GetMapping("/server/info")
    public Mono<Response> serverInfo() throws Exception {
        return Mono.just(systemService.serverInfo());
    }
}