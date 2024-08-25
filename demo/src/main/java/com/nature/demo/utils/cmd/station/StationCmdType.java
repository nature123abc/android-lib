package com.nature.demo.utils.cmd.station;

public enum StationCmdType {
    //测量当前瞄准方向， %R1Q,17017:2
    // 返回 %R1P,0,0:0, dHz, dV, dDist,DistMode
    测量(1, "BAP_MeasDistAngle")
    //自动转面   %R1Q,9028:
    , 换面(2, "AUT_ChangeFace")
    //获取仪器编号   %R1Q,5003:
    // 返回  %R1P,0,0:0, SerialNo
    , 编号(3, "CSV_GetInstrumentNo")
    //棱镜常数  %R1Q,2023:
    //%R1P,0,0:0,PrismCorr
    , 棱镜常数(4, "TMC_GetPrismCorr")
    //%R1Q,5004:
    //返回  %R1P,0,0:0,Name
    , 仪器类型(5, "CSV_GetInstrumentName")
    // 转向%R1Q,9027:Hz,V   需要有角度
    , 转向(6, "AUT_MakePositioning");
    int cmdType;
    String cmdValue;


    StationCmdType(int cmdType, String cmdValue) {
        this.cmdType = cmdType;
        this.cmdValue = cmdValue;

    }

}
