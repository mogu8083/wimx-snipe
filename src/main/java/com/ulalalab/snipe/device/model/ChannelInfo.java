package com.ulalalab.snipe.device.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ChannelInfo {

    // 디바이스 ID
    private String deviceId;

    // 접속 시간
    private LocalDateTime connectTime;

    // 접속 IP
    private String remoteAddress;

    // 로컬 IP
    private String localAddress;

    // 계산식 유무
    private boolean calculateFlag = false;

    // 핸들러 목록
    private List<String> handlerList;

//    public ChannelInfo(Channel channel) {
//        this.connectTime = LocalDateTime.now();
//        this.remoteAddress = channel.remoteAddress().toString();
//    }
}
