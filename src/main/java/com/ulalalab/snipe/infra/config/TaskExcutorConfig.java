//package com.ulalalab.snipe.infra.config;
//
//import org.influxdb.InfluxDB;
//import org.influxdb.dto.Point;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.influxdb.DefaultInfluxDBTemplate;
//import org.springframework.data.influxdb.InfluxDBConnectionFactory;
//import org.springframework.data.influxdb.InfluxDBProperties;
//import org.springframework.data.influxdb.InfluxDBTemplate;
//import org.springframework.data.influxdb.converter.PointConverter;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//
//@EnableAsync
//@Configuration
//public class TaskExcutorConfig {
//
//    @Bean(name = "taskExecutor")
//    public ThreadPoolTaskExecutor taskExecutor() {
//        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
//
//        taskExecutor.setCorePoolSize(16);
//        taskExecutor.setMaxPoolSize(32);
//        //taskExecutor.setQueueCapacity(100);
//        taskExecutor.initialize();
//        taskExecutor.setKeepAliveSeconds(5);
//
//        return taskExecutor;
//    }
//}