package com.nature.demo.utils.cmd.hw;

public enum HwCmdType {
    //小车编号  cat sn
    启动红外(1, "getCarNumber")

    //小车版本号  cat comvers
    , 停止红外(2, "getVersion")

    , 红外文件(3, "hwsfile");
    int cmdType;
    String cmdValue;


    HwCmdType(int cmdType, String cmdValue) {
        this.cmdType = cmdType;
        this.cmdValue = cmdValue;

    }

}
