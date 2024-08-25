package com.nature.demo.utils.cmd.car;

public enum CarCmdType {
    //小车编号  cat sn
    小车编号(1, "getCarNumber")
    //小车版本号  cat comvers
    , 小车版本号(2, "getVersion")
    , 设置时间(3, "setDataTime")
    , 获取时间(4, "getDataTime")
    , 静态测量(5, "readStaticInfo")



    , umount(7, "umount")
    , mount(8, "mount")
    , clear1(9, "clear1")
    , umount2(10, "umount2")

    , mount2(11, "mount2")
    , clear2(12, "clear2")
    , outPutData(13, "outPutData")
    , 读取最后一行(14, "readLine")
    , 亮灯(15, "test6")

    , 关灯(16, "test7")
    , 停止测量(17, "CmdStopMeasure")
    , 初始化(18, "InitBegin")
    , 清空小车数据(19, "InitClear")
    , 获取温度(20, "CmdgetWD")


    , umount_sd(21, "umount_sd")
    , mount_sd(22, "mount_sd")
    , clear_sd(23, "clear_sd")
    , outPutData_sd(24, "outPutData_sd")
    , 动态测量(25, "CmdStartDMeasure")
    ;
    int cmdType;
    String cmdValue;


    CarCmdType(int cmdType, String cmdValue) {
        this.cmdType = cmdType;
        this.cmdValue = cmdValue;

    }

}
