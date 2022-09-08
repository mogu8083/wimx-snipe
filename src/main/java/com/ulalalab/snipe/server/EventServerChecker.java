package com.ulalalab.snipe.server;

import com.ulalalab.snipe.common.instance.GlobalInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventServerChecker {

    private static final Logger logger = LoggerFactory.getLogger(EventServerChecker.class);

    @Autowired
    private EventServer eventServer;

    public void start() {
        Thread thread = new Thread();
        thread.start();

        try {
            while(true) {
                Thread.sleep(5000);

                logger.info("Event Server 실행 유무 : " + GlobalInstance.eventServerFlag);

                // 이벤트 서버가 실행중인지 체크
                if(!GlobalInstance.eventServerFlag) {
                    eventServer.start();
                }
            }
        } catch(Exception e) {
            logger.info(this.getClass() + " Error => " + e.getMessage());
        }
    }
}