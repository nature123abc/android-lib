package com.nature.demo.utils.cmd.car;


import com.dk.common.DateUtils;

public class CarCmd {
    public static String getCmd(CarCmdType cmdType) {
        String cmd = "";
        switch (cmdType) {
            case 小车编号:
                cmd = "cat sn";
                break;
            case 小车版本号:
                cmd = "cat comvers";
                break;
            case 设置时间:
                String time = DateUtils.getDateYMDHMS();
                cmd = "date -s  '" + time + "'";
                break;
            case 获取时间:
                cmd = "date";
                break;

            case 静态测量:
                cmd = "./com3s 4 1 SSS 0 1 30";
                break;
            case umount:
                cmd = "umount /mnt";
                break;
            case mount:
                cmd = "mount -t vfat /dev/sda1 /mnt >>zlog";
                break;
            case clear1:
                cmd = "cat /mnt/test";
                break;
            case umount2:
                cmd = "umount /mnt2";
                break;

            case mount2:
                cmd = "mount -t vfat /dev/mmcblk0p1 /mnt2/ ";
                break;
            case clear2:
                cmd = "cat /mnt2/test";
                break;

            case outPutData:
                cmd = outputUpData("_" + DateUtils.getDate("yyMMddHHmm"));
                break;

            case 读取最后一行://读取最后一行
                cmd = "tail -1 nohup.out";
                break;

            case 亮灯:
                cmd = "./test6";      //
                break;

            case 关灯:
                cmd = "./test7";      //
                break;

            case 停止测量:
                cmd = "touch 111 ";
                break;

            case 初始化:
                cmd = "./begin";
                break;
            case 清空小车数据:
                cmd = "./clear";
                break;

            case 获取温度:
                cmd = "./com_2_wd_p";
                break;
            case umount_sd:
                cmd = "umount /mnt2";
                break;
            case mount_sd:
                cmd = "mount -t vfat /dev/mmcblk0p1 /mnt2/ ";
                break;
            case clear_sd:
                cmd = "cat /mnt2/test";
                break;
            case outPutData_sd:
                cmd = outputSdData("_" + DateUtils.getDate("yyMMddHHmm"));
                break;
            case 动态测量:
                cmd = dynamicMeasure(1,1,1,24);
                break;
            default:
                return "error";
        }
        return cmd;
    }

    /**
     *
     * @param RowID       流水号
     * @param StaID       测站id
     * @param SecID       侧端id
     * @param T           温度
     * @return
     */
    public static String dynamicMeasure(int RowID, int StaID, int SecID, int T) {
        return "nohup ./com2 " + RowID + " 2 DDD " + StaID + " " + SecID + " " + T + " >>nohup.out &";
    }

    public static String outputUpData(String fileName) {
        return "./finishusb2" + " R" + fileName + " 2>&1 | tee -a zlog";
    }

    public static String outputSdData(String fileName) {
        return "./finishsd" + " R" + fileName + " 2>&1 | tee -a zlog";
    }
}
