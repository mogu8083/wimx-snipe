package com.ulalalab.snipe.device.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    // 마지막 패킷 수신 시간
    private LocalDateTime lastPacketTime;

    // 핸들러 목록
    private List<String> handlerList;

    // 디바이스 ID
    private boolean isInitSetting;

    public String getConnectTime() {
        return this.connectTime!=null ?
                DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(this.connectTime)
                : "";
    }

    public String getLastPacketTime() {
        return this.lastPacketTime!=null ?
                DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(this.lastPacketTime)
                : "";
    }
}
