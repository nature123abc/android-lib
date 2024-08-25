package com.dk.lib_dk.utils.tsp.gcom;

import com.dk.common.DoubleUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: ControlPlatform
 * @Desc:
 * @Author: hq
 * @Date: 2023/4/12
 * 全站仪GeoCom协议说明
 * [<LF>]%R1Q,<RPC>[,<Trid>]:[<P0>][,<P1>,...]<Term>
 * <LF>      | 初始换行清除接收器缓冲区
 * %R1Q      | GeoCOM request type 1
 * <RPC>     | 功能编号 0-65535
 * <Trid>    | 事务ID
 * :         | 协议头和以下参数之间的分隔符
 * <P0>,<P1> | 参数
 * <Term>    | 终止符字符串  \r\n
 */
public class GeoComCmd {
    public static int TMC_GetFace = 2026;//获取当前仪器face   没有参数  %R1Q,2026:
    public static int AUT_ChangeFace = 9028;//正倒镜   %R1Q,9028:    %R1Q,9028:PosMode,ATRMode,0
    public static int AUT_MakePositioning = 9027;//将望远镜转动到指定位置   %R1Q,9027:Hz,V,PosMode,ATRMode,
    public static int BAP_MeasDistanceAngle = 17017;//测量   %R1P,0,0:RC, dHz[double], dV[double], dDist[double],DistMode[long]

    //%R1Q,9037:3.2893745,4.6959417,0
    public static int AUT_FineAdjust = 9037; //  望远镜十字准线精确定位到目标棱镜上  %R1Q,9037: dSrchHz[d^-uble], dSrchV[double],0
    public static int AUS_GetUserLockState = 18008; //  判断是否是锁定模式，%R1Q,18008:
    public static int AUS_SetUserLockState = 18007; //  设置为锁定模式 %R1Q,18007: OnOff[long]  0表示取消，1表示锁定
    public static int AUS_SetUserAtrState = 18005; //  打开ATR  %R1Q,18005: On/Off[long]
    public static int AUT_LockIn = 9013; //锁定目标  %R1Q,9013:
    public static int EDM_Laserpointer = 1004; //打开关闭激光 %R1Q,1004: eLaserflong]


    public static int CSV_GetInstrumentNo = 5003;//获取仪器编号   %R1Q,5003:
    public static int TMC_GetPrismCorr = 2023;// 棱镜常数 %R1Q,2023:
    public static int CSV_GetInstrumentName = 5004;// 仪器类型  %R1Q,5004:           TS30 0,5”
    public static int TMC_GetStation = 2009;// 获取仪器测站坐标  %R1Q,2009:          %R1P,0,0:RC,E0[double],N0[double],H0[double],Hi[double]  %R1P,0,0:0,500,1000,0,0
    public static int TMC_GetCoordinate = 2082;// 获取测点坐标  %R1Q,2082:          %R1Q,2082: WaUTime[long]，Mode[long]
    //%%R1P,0,0:0,500.875758355408493,1002.075111156734124,-0.001195564546136,1564621,500.868351940436639,1002.078221335038734,0.019715079022963,2206733

    public static int TMC_GetAtmCorr = 2029;// %R1Q,2029:  获取气压温度等信息
    public static int TMC_DoMeasure = 2008;//%R1Q,2008: Command[long],Mode[long]  测量坐标数据，不返回
    public static int TMC_SetAtmCorr = 2028;// %R1Q,2028: Lambda[double],Pressure[do-uble], DryTemperature[double]，WetTemperature[double]
    // %R1Q,2028:0.000000658,1004.0,30.0,23.84024240475126

    //锁定目标步骤
    //1.测量数据：  %R1Q,17017:2
    //2.照准目标： %R1Q,9037:0.090051470073101,1.310251195223469,0
    //3.判断是否是锁定模式：%R1Q,18008:
    //如果不是锁定模式，则设置为锁定模式，%R1Q,18007:1
    //4.是锁定模式，则开始锁定   %R1Q,9013:

    //5.动态测量  %R1Q,2008:8,7


    //region 通用命令格式
    private static String obtParm(List<String> parms) {
        String pm = "";
        if (null == parms) {
            return pm;
        }
        for (int i = 0; i < parms.size(); i++) {
            String parm = parms.get(i);
            pm += parm;
            if (i < parms.size() - 1) {
                pm += ",";
            }
        }
        return pm;
    }

    public static String obtCmd(Integer code, Integer trid, List<String> parms) {
        String strId = "";
        if (null != trid) {
            strId += "," + trid;
        }
        return "%R1Q," + code + strId + ":" + obtParm(parms) + "\r\n";
    }

    public static String obtCmd(Integer code, List<String> parms) {
        return obtCmd(code, null, parms);
    }

    public static String obtCmd(Integer code, Integer trid) {
        return obtCmd(code, trid, null);
    }

    public static String obtCmd(Integer code) {
        return obtCmd(code, null, null);
    }

    //endregion

    //region 锁定，模式切换，自动化测量
    //望远镜十字准线精确定位到目标棱镜上
    public static String obtAUT_FineAdjust(double hz, double v) {
        return obtCmd(GeoComCmd.AUT_FineAdjust, new ArrayList<>() {{
            add(DoubleUtils.formatStr(hz, 7));
            add(DoubleUtils.formatStr(v, 7));
            add("0");
        }});
    }

    //打开ATR
    public static String obtAUS_SetUserAtrState_On() {
        return obtCmd(GeoComCmd.AUS_SetUserAtrState, new ArrayList<>() {{
            add("1");
        }});
    }

    //设置为锁定模式
    public static String obtAUS_SetUserLockState_On() {
        return obtCmd(GeoComCmd.AUS_SetUserLockState, new ArrayList<>() {{
            add("1");
        }});
    }

    //设置为锁定模式
    public static String obtAUS_SetUserLockState_Off() {
        return obtCmd(GeoComCmd.AUS_SetUserLockState, new ArrayList<>() {{
            add("0");
        }});
    }

    //锁定目标
    public static String obtAUT_LockIn() {
        return obtCmd(GeoComCmd.AUT_LockIn);
    }

    //判断是否是锁定模式
    public static String obtAUS_GetUserLockState() {
        return obtCmd(GeoComCmd.AUS_GetUserLockState);
    }
    //endregion

    //region   测量
    //转向到指定位置
    public static String obtAUT_MakePositioning(double hz, double v) {
        String cmd = obtCmd(GeoComCmd.AUT_MakePositioning, new ArrayList<>() {{
            add("" + hz);
            add("" + v);
        }});
        return cmd;
    }

    //测量距离+角度
    public static String obtBAP_MeasDistanceAngle() {
        String cmd = obtCmd(GeoComCmd.BAP_MeasDistanceAngle, new ArrayList<>() {{
            add("2");
        }});
        return cmd;
    }

    //换向
    public static String obtBAP_AUT_ChangeFace() {
        return obtCmd(GeoComCmd.AUT_ChangeFace);
    }

    //查询当前盘向
    public static String obtTMC_GetFace() {
        return obtCmd(GeoComCmd.TMC_GetFace);
    }

    //endregion

    //region 建站

    public static String obtTMC_GetStation() {
        return obtCmd(GeoComCmd.TMC_GetStation, new ArrayList<>());
    }


    public static String obtTMC_GetCoordinate() {
        return obtCmd(GeoComCmd.TMC_GetCoordinate, new ArrayList<>() {{
            add("10000");
        }});
    }

    public static String obtTMC_DoMeasure() {
        return obtCmd(GeoComCmd.TMC_DoMeasure, new ArrayList<>() {{
        }});
    }

    //endregion

    //region 设备参数信息
    //获取仪器编号
    public static String obtCSV_GetInstrumentNo() {
        return obtCmd(GeoComCmd.CSV_GetInstrumentNo, new ArrayList<>());
    }

    //
    public static String obtCSV_GetInstrumentName() {
        return obtCmd(GeoComCmd.CSV_GetInstrumentName, new ArrayList<>());
    }

    public static String obtCSV_GetIntTemp() {
        return obtCmd(GeoComCmd.CSV_GetInstrumentName, new ArrayList<>());
    }

    public static String obtTMC_GetAtmCorr() {
        return obtCmd(GeoComCmd.TMC_GetAtmCorr, new ArrayList<>());
    }

    public static String obtTMC_SetAtmCorr(Double lambda, Double pressure, Double dryTemperature, Double wetTemperature) {
        return obtCmd(GeoComCmd.TMC_SetAtmCorr, new ArrayList<>() {{
            add("" + formData(lambda));
            add("" + pressure);
            add("" + dryTemperature);
            add("" + wetTemperature);
        }});
    }

    private static String formData(Double value) {
        DecimalFormat df = new DecimalFormat("0.##############");
        return df.format(value);
    }

    //打开关闭激光
    public static String obtEDM_LaserpointerOff() {
        return obtCmd(GeoComCmd.EDM_Laserpointer, new ArrayList<>() {{
            add("0");
        }});
    }

    //打开关闭激光
    public static String obtEDM_LaserpointerOn() {
        return obtCmd(GeoComCmd.EDM_Laserpointer, new ArrayList<>() {{
            add("1");
        }});
    }

    //endregion
}
