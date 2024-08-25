package com.nature.demo.utils.cmd.hw;

public class HwCmd {
    public static String getCmd(HwCmdType cmdType, String cmdInfo) {
        String cmd = "";
        switch (cmdType) {
            case 启动红外:
                cmd = "./c1start  " + cmdInfo + "#";
                break;
            case 停止红外:
                cmd = "./c1end  " + "_" + cmdInfo + "#";
                break;
            case 红外文件:
                cmd = "./c1fn  " + cmdInfo + "#";
                break;
            default:
                return "error";
        }
        return cmd;
    }
}
