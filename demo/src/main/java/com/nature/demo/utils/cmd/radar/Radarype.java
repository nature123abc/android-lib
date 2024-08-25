package com.nature.demo.utils.cmd.radar;

public enum Radarype {
    //小车编号  cat sn
    启动雷达(1, "startRadar")

    //小车版本号  cat comvers
    , 停止雷达(2, "stopRadar")

    , 获取雷达数据(3, "geRadarData");
    int cmdType;
    String cmdValue;


    Radarype(int cmdType, String cmdValue) {
        this.cmdType = cmdType;
        this.cmdValue = cmdValue;

    }

}
