package com.ulalalab.snipe.common.service;

import com.ulalalab.snipe.device.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Slf4j
@Service
public class TaskExecutorService {

    @Async("taskExecutor")
    public void task(Runnable runnable) {

    }
}
