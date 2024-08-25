package com.nature.demo.utils.cmd.radar;

public class RadarCmd {
    public static String getCmd(Radarype cmdType, String cmdInfo) {
        String start = "./com_2_";
        String cmd = "";

        switch (cmdType) {
            case 启动雷达:
                cmd = "laser_start " + cmdInfo + "#";
                break;
            case 停止雷达:
                cmd = "laser__stop " + cmdInfo + "#";
                break;
            case 获取雷达数据:
                cmd = "laser_getall";
                break;
            default:
                return "error";
        }
        return start + cmd;
    }
}
