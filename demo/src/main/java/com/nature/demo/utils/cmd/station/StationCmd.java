package com.nature.demo.utils.cmd.station;

public class StationCmd {
    public static String getCmd(StationCmdType cmdType, double hz, double v) {

        switch (cmdType) {
            case 测量:
                return "%R1Q,17017:2";
            case 换面:
                return "%R1Q,9028:";
            case 编号:
                return "%R1Q,5003:";
            case 棱镜常数:
                return "%R1Q,2023:";
            case 仪器类型:
                return "%R1Q,5004:";
            case 转向:
                return "%R1Q,9027:" + hz + "," + v;
            default:
                return "error";
        }
    }

    public static String getCmd(StationCmdType cmdType) {
        return getCmd(cmdType, 0.0, Math.PI / 2);

    }
}
