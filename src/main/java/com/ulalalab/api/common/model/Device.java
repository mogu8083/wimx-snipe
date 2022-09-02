package com.ulalalab.api.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name="ulalalab_a")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private LocalDateTime time;

    // 장비 이름
    @Column
    private String deviceId;

    @Column
    private Double ch1;

    @Column
    private Double ch2;

    @Column
    private Double ch3;

    @Column
    private Double ch4;

    @Column
    private Double ch5;

    @Column
    private Double ch6;

    @Column
    private Double ch7;

    @Column
    private Double ch8;

    @Column
    private Double ch9;

    @Column
    private Double ch10;

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private LocalDateTime time;
//
//    // 장비 ID
//    private String deviceId;
//
//    // 채널 1
//    private Double ch1;
//
//    // 채널 2
//    private Double ch2;
//
//    // 채널 3
//    private Double ch3;
//
//    // 채널 4
//    private Double ch4;
}
