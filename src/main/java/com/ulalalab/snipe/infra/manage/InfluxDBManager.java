package com.ulalalab.snipe.infra.manage;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InfluxDBManager {

    @Value("${spring.influxdb.udp-port}")
    private int UDP_PORT;

    @Autowired
    private InfluxDB influxDBClient;


    public InfluxDB getInfluxDB() {
        return this.influxDBClient;
    }

    public void udpWrite(Point point) {
        this.influxDBClient.write(UDP_PORT, point);
    }

    public void write(Point point) {
        this.influxDBClient.write(point);
    }
}