//package com.ulalalab.api.common.service;
//
//import com.influxdb.client.InfluxDBClient;
//import com.influxdb.client.InfluxDBClientFactory;
//import com.influxdb.client.QueryApi;
//import com.influxdb.client.WriteApiBlocking;
//import com.influxdb.client.domain.WritePrecision;
//import com.influxdb.client.write.Point;
//import com.influxdb.query.FluxRecord;
//import com.influxdb.query.FluxTable;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.PostConstruct;
//import java.time.Instant;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class InfluxTestService {
//
//    //private final InfluxDBTemplate<Point> influxDBTemplate;
//
//    //@PostConstruct
//    public void list() throws InterruptedException {
//        String org =  "test-org";
//        String bucket =  "test-bucket";
//        char[] token = "Yb0tzSb7OLFlzNV8na1JO8hzpwOG-ugGb_UIP0e6na3HaRO-8gKAdd-o3bBDy67oIyzGx5hr62vApYQvC9oK_w==".toCharArray();
//        String username = "admin";
//        char[] password = "admin1234".toCharArray();
//
//        //InfluxDBClient influxDBClient = InfluxDBClientFactory.create("http://127.0.0.1:8086", username, password);
//        InfluxDBClient influxDBClient = InfluxDBClientFactory.create("http://127.0.0.1:8086", token, org, bucket);
//        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
//
//        //1. 포인트 객체로 데이터를 저장하는 방법
//        int i = 0 ;
//        while(i++<10) {
//            Thread.sleep(1000);
//            Point point = Point.measurement("wx_test_new")
//                    .addField("ch_1", Math.random())
//                    .addField("ch_2", Math.random())
//                    .addField("ch_3", Math.random())
//                    .addField("ch_4", Math.random())
//                    .time(Instant.now().toEpochMilli(), WritePrecision.MS);
//
//            writeApi.writePoint(point);
//        }
//        influxDBClient.close();
//    }
//
//    //@PostConstruct
//    public void search() {
//        String org =  "test-org";
//        String bucket =  "test-bucket";
//        char[] token = "Yb0tzSb7OLFlzNV8na1JO8hzpwOG-ugGb_UIP0e6na3HaRO-8gKAdd-o3bBDy67oIyzGx5hr62vApYQvC9oK_w==".toCharArray();
//        String username = "admin";
//        char[] password = "admin1234".toCharArray();
//
//        InfluxDBClient influxDBClient = InfluxDBClientFactory.create("http://127.0.0.1:8086", token, org, bucket);
//        String flux = "from(bucket: \"" + bucket + "\") " +
//                "|> range(start: 2022-08-23T00:00:00Z, stop: 2022-08-23T23:00:00Z) " +
//                "|> filter(fn: (r) => r[\"_field\"] == \"ch_1\" or r[\"_field\"] == \"ch_2\")" +
//                "|> yield(name: \"last\")";
//
//        QueryApi queryApi = influxDBClient.getQueryApi();
//
//        List<FluxTable> tables = queryApi.query(flux);
//
//        for (FluxTable fluxTable : tables) {
//            List<FluxRecord> records = fluxTable.getRecords();
//
//            for (FluxRecord fluxRecord : records) {
//                //System.out.println(fluxRecord.getTime() + ": " + fluxRecord.getValueByKey("_field"));
//                System.out.println(fluxRecord.getValueByKey("_field") + " : " + fluxRecord.getValueByKey("_value"));
//            }
//        }
//    }
//}