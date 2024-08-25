package com.dk.lib_dk.utils;

/**
 * @ProjectName: AndroidLib
 * @Desc:
 * @Author: hq
 * @Date: 2023/11/8
 */
public class ToleranceParm {
    public static double ZERO_DATA = 6.0;               //方向的归零差

    public static double TOWC = 15.0;                   //2c测回内
    public static double INDEX = 15.0;                  //X测回内
    public static double DETL_SD = 1;                   //距离较差(mm)
    public static double DETL_TOWC = 9.0;               //2c测回内较差
    public static double DETL_INDEX = 12.0;             //X测回内较差

    public static double BT_HZ = 9.0;                   //测回间水平角值差
    public static double BT_HV = 9.0;                   //测回间竖直角值较差
    public static double BT_TOWC = 6.0;                 //2c测回内测回间
    public static double BT_INDEX = 12.0;               //X测回内测回间
    public static double BT_DETASD = 1;                 //距离较差测回间
}
