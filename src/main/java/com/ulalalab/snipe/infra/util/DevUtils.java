package com.ulalalab.snipe.infra.util;

import org.springframework.context.ApplicationContext;

import java.util.Set;

public final class DevUtils {

    public final static boolean isTest = false;

    public static boolean isPrint(String deviceId) {
        Set<String> list = Set.of("WX-1S", "WX-1A", "WX-1Z");

        for(String tmpDeviceId : list) {
            if(deviceId.contains(tmpDeviceId)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isTest() {
        return isTest;
    }
}