package com.dk.lib_dk.utils.ui;

/**
 * Created by qq on 2017/9/4.
 */

import android.content.Context;
import android.widget.Toast;

/**
 * @version v1.0
 * @类描述： Toast工具类，避免同一个
 * @项目名称：
 * @包名：
 * @类名称：CollectDataActivity1
 * @创建人：
 * @创建时间： 2017/9/4.
 * @修改人：hq
 * @修改时间：
 * @修改备注：
 * @bug [nothing]
 * @Copyright
 * @mail
 * @see [nothing]
 */

public class ToastUtil {
    public static String oldMsg;
    public static long time;
    public static long detlTime = 2000;

    /**
     * /避免Toast重复显示
     *
     * @param context
     * @param msg
     * @param duration
     */
    public static void showToast(Context context, String msg, int duration) {
        if (!msg.equals(oldMsg)) { // 当显示的内容不一样时，即断定为不是同一个Toast
            Toast.makeText(context, msg, duration).show();
            time = System.currentTimeMillis();
        } else {
            // 显示内容一样时，只有间隔时间大于2秒时才显示
            if (System.currentTimeMillis() - time > detlTime) {
                Toast.makeText(context, msg, duration).show();
                time = System.currentTimeMillis();
            }
        }
        oldMsg = msg;
    }

    public static void showToast(Context context, String msg) {
        showToast(context, msg, Toast.LENGTH_LONG);
    }
}
