package com.ulalalab.snipe.infra.util;

import java.util.Arrays;
import java.util.List;

public final class DevUtils {

    public static boolean isPrint(int deviceId) {
        List<Integer> list = Arrays.asList(5001, 1);

        for(Integer tmpDeviceId : list) {
            if(tmpDeviceId==deviceId) {
                return true;
            }
        }
        return false;
    }

    public static void printStackTrace(Exception e) {
        String profile = System.getProperty("spring.profiles.active");

        if("local-server".equals(profile)) {
            e.printStackTrace();
        }
    }
}