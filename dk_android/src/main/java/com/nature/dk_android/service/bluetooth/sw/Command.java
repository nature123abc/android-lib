package com.nature.dk_android.service.bluetooth.sw;


import com.dk.common.DateUtils;

/**
 * Created by hq on 2017/12/3.
 *
 * @描述：发送命名类
 */

public class Command {
    public static String getCmd(String cmdType, String cmdInfo) {
        String cmd = "";
        if (cmdType.equals("startNohup")) {//启动动态带灯带继电器 nohup  echo '1' >/sys/class/leds/beep/brightness &&./test6 && ./com2 1 2 DDD 1 1 1  >>nohup.out  &
            cmd = "nohup  echo '1' >/sys/class/leds/beep/brightness && usleep 300000 &&./test6 && ./com2 " + cmdInfo + "  >>nohup.out  &";
        } else if (cmdType.equals("stopNohup")) {//./c1allend2  结束动态带灯带继电器
            cmd = "./c1onlyend";
        } else if (cmdType.equals("startIns")) {// 启动惯导./gdcomA 33345 333
            cmd = "./gdcomA " + cmdInfo;
        } else if (cmdType.equals("stopIns")) {//停止惯导   ./gdcomENDA
            cmd = "./gdcomENDA";//./c1c2 SW210401_HSTS_220712085519 000_0#
        } else if (cmdType.equals("startRadar0")) {//第一次Set的启动  ./c1c2 SW151001_2019SHGXJT0100_1907081 003_99220#
            cmd = "./c1c2 " + cmdInfo + "#";
        } else if (cmdType.equals("startRadar")) {//已经设置过的启动脚本  ./c1c2b SW151001_2019SHGXJT0100_1907081 003_99220#
            cmd = "./c1c2b " + cmdInfo + "#";
        } else if (cmdType.equals("stopRadar")) {//结束雷达测试带灯带继电器  ./c1allend _003_99220#
            cmd = "./c1allend2  _" + cmdInfo + "#";
        } else if (cmdType.equals("stopRadarIns")) {//停止雷达惯导 ./c1allendnofinish _003_99220#  && ./gdcomENDA
            // ./c1allendnofinish _003_99220#  && ./gdcomENDA&&./c1synctime>/dev/null
            // cmd = "./c1allendnofinish _" + cmdInfo + "#  && ./gdcomENDA&&./c1synctime>/dev/null";
            //./c1allend22 _003_99220# && ./gdcomENDA&& sleep 1 &&./c1synctime>/dev/null
            //./c1allend22 _003_99220# && ./gdcomENDA2&& sleep 1 && ./finend
            cmd = "./c1allend22 _" + cmdInfo + "#  && ./gdcomENDA2&& sleep 1 && ./finend";
        } else if (cmdType.equals("getstatic")) {// 获取静态采集
            //cmd = "./getstatic";
            cmd = "./getstatic2";
            // cmd = "./com1 4 1 SSS 0 1 30";
        }
        return cmd;
    }

    public static String getCmd(String cmdType) {
        return getCmd(cmdType, "");
    }

    /**
     * 获取测量命令
     *
     * @param CommandType 命令类型
     * @param RowID       测站id
     * @param StaID       测站id
     * @param SecID       侧端id
     * @param T           温度
     * @param AddInfo     其他命令字
     * @return
     */
    public static String buildMicroControllerCommand(String CommandType, int RowID, int StaID, int SecID, int T, String AddInfo) { //动态测量
        String CommandContent = "";
        if (CommandType.equals("CmdReadStaticInfo")) {                                           //1)读取静态传感器数据命令
            //CommandContent = "./com3s 4 1 SSS 0 1 30";                                           //2012-04-26精态测量命令./com1 4 1 SSS 0 1 30 修改成:./com3s 5 1 DDD 0 1 27
            CommandContent = "./com1 4 1 SSS 0 1 30";                                           //2012-04-26精态测量命令./com1 4 1 SSS 0 1 30 修改成:./com3s 5 1 DDD 0 1 27
        } else if (CommandType.equals("CmdStartDMeasure")) {                                     //2)开始动态数据测量写入命令字(序号\站号1\段号1) nohup./com1改成nohup./com2
            CommandContent = "nohup ./com2 " + RowID + " 2 DDD " + StaID + " " + SecID + " " + T + " >>nohup.out &";
        } else if (CommandType.equals("CmdStopMeasure")) {                                       //3)停止测量数据
            CommandContent = "touch 111 ";
        } else if (CommandType.equals("CmdInitStartDM")) {                                       //4)状态复位
            CommandContent = "rm 111";
        } else if (CommandType.equals("InitDate")) {
            CommandContent = "date -s " + AddInfo;
        } else if (CommandType.equals("CmdReadLastLineData")) {                                 //读取最后一行
            CommandContent = "tail -1 nohup.out";
        } else {
            CommandContent = "测试";
        }
        return CommandContent;
    }

    /**
     * 获取测量命令
     *
     * @param cmdType 命令类型
     * @return
     */
    public static String getCommand(String cmdType, String fileName) {
        String cmd = "";
        if (cmdType.equals("getCarNumber")) {                                                            //获取小车序号
            cmd = "cat sn";
        } else if (cmdType.equals("getVersion")) {                     //获取版本号
            cmd = "cat comvers";
        } else if (cmdType.equals("setDataTime")) {                     //设置时间  date -s "当前时间"  如date -s "2011-09-28 15:58"
            String time = DateUtils.getDateYMDHMS();
            cmd = "date -s  '" + time + "'";
        } else if (cmdType.equals("getDataTime")) {                      //获取时间
            cmd = "date";
        } else if (cmdType.equals("readStaticInfo")) {                    //静态测量
            cmd = "./com3s 4 1 SSS 0 1 30";
        } else if (cmdType.equals("startDMeasure")) {                    //动态测量
            cmd = "./com3s 4 1 SSS 0 1 30";
        } else if (cmdType.equals("umount")) {                                                                   //发送umount 命令U盘
            cmd = "umount /mnt";
        } else if (cmdType.equals("mount")) {                                                                   //发送mount 命令U盘
            cmd = "mount -t vfat /dev/sda1 /mnt >>zlog";
        } else if (cmdType.equals("clear1")) {                                                                  //清空U盘导出
            cmd = "cat /mnt/test";
        } else if (cmdType.equals("umount2")) {                                                                 //发送 umount 命令Sd
            cmd = "umount /mnt2";
        } else if (cmdType.equals("mount2")) {                                                                  //发送mount 命令sd
            cmd = "mount -t vfat /dev/mmcblk0p1 /mnt2/ ";
        } else if (cmdType.equals("clear2")) {                                                                  //清空SD
            cmd = "cat /mnt2/test";
        } else if (cmdType.equals("outPutData")) {
            cmd = "./finishusb2" + " R" + fileName + " 2>&1 | tee -a zlog";      //20160709 ./finishusb2  R222" + fileName + " 2>&1 | tee -a zlog
        } else if (cmdType.equals("readLine")) {
            cmd = "tail -1 nohup.out";      //
        } else if (cmdType.equals("test6")) {
            cmd = "./test6";      //
        } else if (cmdType.equals("test7")) {
            cmd = "./test7";      //
        } else if (cmdType.equals("CmdStopMeasure")) {                                       //3)停止测量数据
            cmd = "touch 111 ";
        } else if (cmdType.equals("InitBegin")) {
            cmd = "./begin";
        } else if (cmdType.equals("InitClear")) {
            cmd = "./clear";
        } else if (cmdType.equals("CmdgetWD")) {//获取温度
            cmd = "./com_2_wd_p";
        } else if (cmdType.equals("umount_sd")) {//导出SD1
            cmd = "umount /mnt2";
        } else if (cmdType.equals("mount_sd")) {//导出SD2
            cmd = "mount -t vfat /dev/mmcblk0p1 /mnt2/ ";
        } else if (cmdType.equals("clear_sd")) {//导出SD3
            cmd = "cat /mnt2/test";
        } else if (cmdType.equals("outPutData_sd")) {//导出SD4
            cmd = "./finishsd" + " R" + fileName + " 2>&1 | tee -a zlog";
        } else if (cmdType.equals("echo1")) {//断开485
            cmd = ("echo '1' >/sys/class/leds/beep/brightness");
        } else if (cmdType.equals("echo0")) {//连接485
            cmd = ("echo '0' >/sys/class/leds/beep/brightness");
        } else if (cmdType.equals("synctime")) {//同步时间
            cmd = ("./c1synctime");
        } else {
            cmd = "测试";
        }
        return cmd;
    }

    public static String getCommand(String cmdType) {
        return getCommand(cmdType, "");
    }


    /**
     * 获取红外命令
     *
     * @param cmdType 命令类型
     * @param cmdInfo 其他命令字
     * @return
     */
    public static String getHwCom(String cmdType, String cmdInfo) {
        String cmd = "";
        if (cmdType.equals("hwstart")) {//启动红外
            cmd = "./c1start  " + cmdInfo + "#";
        } else if (cmdType.equals("hwstop")) {//停止红外
            cmd = "./c1end   " + "_" + cmdInfo + "#";
        } else if (cmdType.equals("hwsfile")) {//红外文件
            cmd = "./c1fn   " + cmdInfo + "#";
        } else if (cmdType.equals("current")) {//获取当前雷达静态数据
            cmd = "./c1current";
            cmd = "./c1current2";
        }
        return cmd;
    }


    public static String getGdComAA(String cmdType, String cmdInfo) {
        String start = "./gdcom2 ";
        return getGdCom(start, cmdType, cmdInfo);
    }

    public static String getGdComBB(String cmdType, String cmdInfo) {
        String start = "./gdcomBB ";
        return getGdCom(start, cmdType, cmdInfo);
    }

    static String getGdCom(String start, String cmdType, String cmdInfo) {
        String cmd = "";
        if (cmdType.equals("NPOS")) {//小车反向初始化命令 ./gcdom NPOS, 31.15.21, 121.36.22#
            cmd = "NPOS," + cmdInfo + "#";
        } else if (cmdType.equals("PPOS")) {//小车正向及起始里程计值命令  ./gcdom PPOS,123456,111#
            cmd = "PPOS," + cmdInfo + "#";
        } else if (cmdType.equals("STAT")) {//发送中间站号及里程计值命令    ./gcdom STAT, 23456,222#
            cmd = "STAT," + cmdInfo + "#";
        } else if (cmdType.equals("STOPON")) {//发送静态开始命令    ./gcdom STOP, ON#
            cmd = "STOP,ON" + cmdInfo + "#";
        } else if (cmdType.equals("STOPOFF")) {//发送静态结束命令    ./gcdom STOP, OFF#
            cmd = "STOP,OFF" + cmdInfo + "#";
        } else if (cmdType.equals("EXIT")) {//退出    ./gcdom EXIT,45678*FF #
            cmd = "EXIT," + cmdInfo + "#";
        } else if (cmdType.equals("GINF")) {//获取设备导航值及状态   ./gcdom GINF#
            cmd = "GINF" + cmdInfo + "#";
          /*  start = "";
            cmd = "./gdcomginf";*/
        } else if (cmdType.equals("lsFile")) {//查看惯导文件
            return "ls -la /media/usbhd-sdb1/*.bin";
        } else if (cmdType.equals("cpFile")) {// 拷贝文件
            return "./xucopy " + cmdInfo;
        }
        return start + cmd;
    }

    /**
     * 获取测站命令的测站好
     */
    public static String getCommonStationId(int staID) {
        if (staID < 0) {
            return staID + "";
        } else if (staID < 10) {
            return "00" + staID;
        } else if (staID < 100) {
            return "0" + staID;
        } else {
            return staID + "";
        }
    }

    public static String obtLinuxCmd(String type, String cmdInfo) {
        String cmd = "";
        if (type.equals("cmd_df_m")) {
            cmd = "df -m";//df命令的功能是用来检查linux服务器的文件系统的磁盘空间占用情况。
        } else if (type.equals("cmd_ls_la")) {//ls -la /media/usbhd-sdb1/*.bin  查看惯导中有几个文件
            cmd = "ls -la " + cmdInfo;
        } else if (type.equals("cmd_cp")) {//cp  /media/usbhd-sda1/IMULog_1.bin  /media/usbhd-sdb1/
            cmd = "cp " + cmdInfo;
        } else if (type.equals("cmd_rm")) {//cp  /media/usbhd-sda1/IMULog_1.bin  /media/usbhd-sdb1/
            cmd = "rm " + cmdInfo;
        }
        return cmd;
    }


}
