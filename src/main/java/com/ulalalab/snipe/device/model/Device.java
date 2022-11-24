package com.ulalalab.snipe.device.model;

import com.ulalalab.snipe.infra.util.LocalDateUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

@Getter
@Setter
public class Device {

    // UTC Timestamp
    private long time;

    private LocalDateTime cvtTime;

    // 장비 이름
    private String deviceId;

    // 채널 1
    private Double ch1;

    // 채널 2
    private Double ch2;

    // 채널 3
    private double ch3;

    // 채널 4
    private double ch4;

    // 채널 5
    private double ch5;

    public String getCvtTime() {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(this.time), TimeZone.getDefault().toZoneId());
        return LocalDateUtils.getLocalDateTimeString(localDateTime, LocalDateUtils.DATE_TIME_FORMAT);
    }

    public void setInitValue() {
        this.deviceId = null;
    }
}
