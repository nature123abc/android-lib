package com.dk.lib_dk.utils.sys;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class UuidUtils {
    public static void main(String[] args) {
        rand();
    }

    public static long rand() {
        return getId();
    }

    public static long getId() {
        long ltime = Long.valueOf(new SimpleDateFormat("yyMMddHHmmssSSS").format(new Date())) * 10000;//15位 + 5位
        return ltime + getRandomForIntegerBounded(10000, 99999);
    }

    public static int getRandomForIntegerBounded(int min, int max) {
        int intBounded = min + ((int) (new Random().nextFloat() * (max - min)));
        return intBounded;
    }
}
