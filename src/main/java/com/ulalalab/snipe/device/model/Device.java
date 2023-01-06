package com.ulalalab.snipe.device.model;

import com.ulalalab.snipe.infra.util.LocalDateUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;

@Getter
@Setter
@ToString
public class Device {

    private String cvtTime;

    // 디바이스 보낼때 시간 Unix Time
    private int timestamp;

    // Transaction Id
    private int transactionId;

    // 장비 Index
    private DeviceCodeEnum deviceCode;

    // 장비 Index
    private short deviceIndex;

    // 디바이스 등록 년
    private byte deviceRegYear;

    // 디바이스 등록 월
    private byte deviceRegMonth;

    // 디바이스 펌웨어 버전
    private float version;

    // 무선 통신 디바이스의 신호 세기
    private byte rssi;

    // Data Length
    private int dataLength;

    // 채널 데이터 목록
    private List<Float> channelDataList;

    public String getCvtTime() {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(this.timestamp), TimeZone.getDefault().toZoneId());
        return LocalDateUtils.getLocalDateTimeString(localDateTime, LocalDateUtils.DATE_TIME_FORMAT);
    }
}
