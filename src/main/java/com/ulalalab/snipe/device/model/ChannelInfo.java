package com.ulalalab.snipe.device.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ChannelInfo {

    // 디바이스 ID
    private short deviceIndex;

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
    private boolean isInitSetting = false;

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
