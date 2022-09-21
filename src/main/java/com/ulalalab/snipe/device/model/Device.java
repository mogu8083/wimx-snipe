package com.ulalalab.snipe.device.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class Device {

    // UTC 시간
    private LocalDateTime time;

    // 장비 이름
    private String deviceId;

    // 채널 1
    private Double ch1;

    // 채널 2
    private Double ch2;

    // 채널 3
    private Double ch3;

    // 채널 4
    private Double ch4;

    // 채널 5
    private Double ch5;

    // 계산식
    private String source;
}
