package com.ulalalab.snipe.infra.util;

import com.ulalalab.snipe.infra.handler.ApplicationContextProvider;
import org.springframework.context.ApplicationContext;

public class BeansUtils {
    public static Object getBean(String beanId) {

        ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();

        if( applicationContext == null ) {
            throw new NullPointerException("Spring의 ApplicationContext 초기화 안됨");
        }
        return applicationContext.getBean(beanId);
    }
}