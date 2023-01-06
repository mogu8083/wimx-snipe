package com.ulalalab.snipe.infra.util;

import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public final class DevUtils {

    public final static boolean isTest = false;

    public static boolean isPrint2(int deviceId) {
        List<Integer> list = Arrays.asList(5001, 1);

        for(Integer tmpDeviceId : list) {
            if(tmpDeviceId==deviceId) {
                return true;
            }
        }
        return false;
    }

    public static boolean isTest() {
        return isTest;
    }
}