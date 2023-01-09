package com.ulalalab.snipe.api.service;

import com.ulalalab.snipe.api.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Properties;

@Slf4j
@Service
public class SystemService {

    /**
     * 서버 정보
     */
    public Response serverInfo() {
        Properties properties = System.getProperties();
        properties.remove("java.class.path");
        properties.remove("java.library.path");

        return new Response(properties);
    }
}