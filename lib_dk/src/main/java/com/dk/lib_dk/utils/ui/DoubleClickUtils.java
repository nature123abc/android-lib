package com.dk.lib_dk.utils.ui;

/**
 * 需要定义一个全局变量 lastClickTime, 用来记录最后点击的时间.
 * 每次点击前需要进行判断, 用lastClickTime 和当前时间想比较，并且更新最后点击时间，若小于临界值，则算无效点击，不触发事件
 */
public class DoubleClickUtils {
    private static long lastClickTime = 0l;
    public static long detlTime = (long) (0.5 * 1000);

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (timeD < detlTime) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
