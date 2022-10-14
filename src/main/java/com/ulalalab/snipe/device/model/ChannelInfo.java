package com.ulalalab.snipe.device.model;

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class ChannelInfo {

    // 디바이스 ID
    private String deviceId;

    // 디바이스 시리얼
    private String serial;

    // 접속 시간
    private LocalDateTime connectTime;

    // 접속 IP
    private String remoteAddress;

    public ChannelInfo(Channel channel) {
        this.connectTime = LocalDateTime.now();
        this.remoteAddress = channel.remoteAddress().toString();
    }
}
