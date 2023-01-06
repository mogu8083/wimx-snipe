package com.ulalalab.snipe.infra.manager;

import lombok.RequiredArgsConstructor;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InfluxDBManager {

    @Value("${spring.influxdb.url}")
    private String URL;

    @Value("${spring.influxdb.password}")
    private String PASSWORD;

    @Value("${spring.influxdb.database}")
    private String DATABASE;

    @Value("${spring.influxdb.udp-port}")
    private int UDP_PORT;

    private final InfluxDB influxDBClient;

    public InfluxDB getInfluxDB() {
        return this.influxDBClient;
    }

    public void udpWrite(Point point) {
        this.influxDBClient.write(UDP_PORT, point);
    }

    public void udpWrite(String record) {
        this.influxDBClient.write(UDP_PORT, record);
    }

    public void write(Point point) {
        this.influxDBClient.write(point);
    }

    public void write(BatchPoints batchPoints) {
        this.influxDBClient.write(batchPoints);
    }

    public BatchPoints getBatchPoints() {
        BatchPoints batchPoints = BatchPoints.database(DATABASE)
                .retentionPolicy("autogen")
                .consistency(InfluxDB.ConsistencyLevel.ALL)
                .build();
        return batchPoints;
    }
}