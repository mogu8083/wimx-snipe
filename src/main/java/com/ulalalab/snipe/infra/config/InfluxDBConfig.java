package com.ulalalab.snipe.infra.config;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.influxdb.DefaultInfluxDBTemplate;
import org.springframework.data.influxdb.InfluxDBConnectionFactory;
import org.springframework.data.influxdb.InfluxDBProperties;
import org.springframework.data.influxdb.InfluxDBTemplate;
import org.springframework.data.influxdb.converter.PointConverter;

@Configuration
@EnableConfigurationProperties(InfluxDBProperties.class)
public class InfluxDBConfig {

    @Value("${spring.influxdb.url}")
    private String URL;

    @Value("${spring.influxdb.password}")
    private String PASSWORD;

    @Value("${spring.influxdb.database}")
    private String DATABASE;

    @Bean
    public InfluxDBConnectionFactory connectionFactory(
            @Qualifier("spring.influxdb-org.springframework.data.influxdb.InfluxDBProperties") final InfluxDBProperties properties) {
        return new InfluxDBConnectionFactory(properties);
    }

    @Bean
    public InfluxDBTemplate<Point> influxDBTemplate(final InfluxDBConnectionFactory connectionFactory) {
        return new InfluxDBTemplate<>(connectionFactory, new PointConverter());
    }

    @Bean
    public DefaultInfluxDBTemplate defaultTemplate(final InfluxDBConnectionFactory connectionFactory) {
        return new DefaultInfluxDBTemplate(connectionFactory);
    }

    @Bean
    public InfluxDB influxDBClient(
            @Qualifier("spring.influxdb-org.springframework.data.influxdb.InfluxDBProperties") final InfluxDBProperties properties) {

        InfluxDB influxDB = new InfluxDBConnectionFactory(properties).getConnection();
        influxDB.setDatabase(DATABASE);
        influxDB.setRetentionPolicy("autogen");
        influxDB.setConsistency(InfluxDB.ConsistencyLevel.ALL);

        return influxDB;
    }

//    @Bean
//    public InfluxDB influxDBUdpWriter(
//            @Qualifier("spring.influxdb-org.springframework.data.influxdb.InfluxDBProperties") final InfluxDBProperties properties) {
//
//        return new InfluxDBConnectionFactory(properties).getConnection();
//    }
}