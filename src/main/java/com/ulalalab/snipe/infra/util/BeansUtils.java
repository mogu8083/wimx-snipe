package com.ulalalab.snipe.infra.util;

import com.ulalalab.snipe.infra.handler.ApplicationContextProvider;
import org.springframework.context.ApplicationContext;

public class BeansUtils<T> {

    public static Object getBean(String beanId) {
        return getApplicationContext().getBean(beanId);
    }

    public static Object getBean(String beanId, Object ... object) {
        return getApplicationContext().getBean(beanId, object);
    }

    private static ApplicationContext getApplicationContext() {
        ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();

        if( applicationContext == null ) {
            throw new NullPointerException("Spring의 ApplicationContext 초기화 안됨");
        }
        return applicationContext;
    }
}