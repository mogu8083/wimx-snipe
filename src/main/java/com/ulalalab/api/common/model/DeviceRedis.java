package com.ulalalab.api.common.model;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@RedisHash(value = "device")
@Builder
public class DeviceRedis {

    // 장비 이름
    @Id
    private String deviceId;
    private Double ch1;
    private Double ch2;
    private Double ch3;
    private Double ch4;
    private Double ch5;
}
