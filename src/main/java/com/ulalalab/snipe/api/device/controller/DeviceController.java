package com.ulalalab.snipe.api.device.controller;//package com.ulalalab.snipe.device.service;

import com.ulalalab.snipe.infra.manage.ChannelManager;
import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

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
	public Object deviceInfo() {
        return channelManager.getChannelGroup();
    }

    /**
     * 해당 클라이언트 연결 해제
     * @param deviceId
     */
    @GetMapping("/disconnect")
    public Object deviceDisconnect(@RequestParam String deviceId) {
        channelManager.removeChannel(deviceId);
        return "sss";
    }
}