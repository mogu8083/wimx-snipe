package com.ulalalab.snipe.infra.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public final class RandomUtils {

    /**
     * Double 형 난수
     */
    public static double getDoubleRandom() {
        return Math.round(Math.random()*100)/10.0;
    }

    /**
     * Int 형 난수
     * @param i : 최대값
     */
    public static int getNumberRandom(int i) {
        return (int) (Math.random() * i) + 10;
    }

    /**
     * Boolean 형 난수
     */
    public static boolean getBooleanRandom() {
        Random random = new Random();
        return random.nextBoolean();
    }

//    public static double getRound(double value, int scale, RoundingMode mode) {
//        return BigDecimal.valueOf(value)
//                .setScale(scale, mode)
//                .doubleValue();
//    }
}