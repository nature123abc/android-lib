package com.nature.demo.ui;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.View;

import com.blankj.utilcode.util.ObjectUtils;
import com.dk.common.DateUtils;
import com.dk.common.DoubleUtils;
import com.nature.demo.R;
import com.nature.demo.base.BsBluetoothActivity;
import com.nature.demo.databinding.ActivityBluetoothBinding;
import com.nature.demo.utils.cmd.car.Command;
import com.xuexiang.xui.utils.ResUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.edittext.MultiLineEditText;
import com.xuexiang.xui.widget.spinner.editspinner.EditSpinner;
import com.xuexiang.xui.widget.spinner.editspinner.EditSpinnerAdapter;
import com.xuexiang.xui.widget.spinner.materialspinner.MaterialSpinner;
import com.xuexiang.xui.widget.spinner.materialspinner.MaterialSpinnerAdapter;
import com.xuexiang.xui.widget.textview.supertextview.SuperButton;

import java.util.ArrayList;
import java.util.List;

public class BluetoothActivity extends BsBluetoothActivity<ActivityBluetoothBinding> {

    MaterialSpinner msBle;

    EditSpinner esCmd;

    SuperButton btnBlue;

    SuperButton btnClear;

    MultiLineEditText metRemark;


    BluetoothDevice select;

    List<BluetoothDevice> listDivs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initmsBle();
        initBlueList();

        initEsCmd();


        initButton();
    }

    private void initView() {
        msBle = getBinding().msBle;
        esCmd = getBinding().esCmd;
        btnBlue = getBinding().btnBlue;
        btnClear = getBinding().btnClear;
        metRemark = getBinding().metRemark;
    }


    private void initEsCmd() {
        //修复光标自动跳转到最后问他
       /* esCmd.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // Log.e("beforeTextChanged", s + "," + start + ";" + count + ";" + after);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                startIndex = esCmd.getEditText().getSelectionStart();
                //Log.e("onTextChanged", s + "," + start + ";" + before + ";" + count);
            }

            @Override
            public void afterTextChanged(Editable s) {
                esCmd.getEditText().setSelection(startIndex);
                // Log.e("onTextChanged", s.toString());
            }
        });*/

        adapterData = setList();

        esCmd.setAdapter(new EditSpinnerAdapter(adapterData)
                .setTextColor(ResUtils.getColor(R.color.black))
                .setTextSize(32)
                .setBackgroundSelector(R.drawable.selector_custom_spinner_bg)
        );
    }

    List<String> adapterData;

    private List<String> setList() {

        List<String> cmds = null;
        cmds = new ArrayList<>();
        cmds.add("cat sn");
        cmds.add("./com3s 4 1 SSS 0 1 30");
        cmds.add("./getstatic");
        cmds.add("touch 111 ");
        cmds.add("rm 111");
        cmds.add("./begin");
        cmds.add("./clear");
        cmds.add("ail -1 nohup.out");
        cmds.add("./test6");
        cmds.add("touch 111 ");
        cmds.add("tail -1 nohup.out");
        cmds.add("./test7");
        cmds.add("nohup ./com3s " + 1 + " 2 DDD " + 1 + " " + 1 + " " + 1 + " >>nohup.out &");
        cmds.add(Command.buildMicroControllerCommand("CmdStartDMeasure", 1, 1, 1, 1, ""));
        cmds.add(Command.buildMicroControllerCommand("CmdStartDMeasure2", 1, 1, 1, 1, ""));
        cmds.add("umount /mnt");
        cmds.add("mount -t vfat /dev/sda1 /mnt ");
        cmds.add("cat /mnt/test");
        cmds.add("./finishusb2 R20120821184550 2>&1 | tee -a zlog");
        cmds.add("mount -t vfat /dev/mmcblk0p1 /mnt2/ ");
        cmds.add("mount -t vfat /dev/sda1 /mnt >>zlog");
        cmds.add("./com1 4 1 SSS 0 1 30");
        cmds.add("daochu");
        cmds.add("umount /mnt2");
        cmds.add("mount -t vfat /dev/mmcblk0p1 /mnt2/ ");
        cmds.add("cat /mnt2/test");
        cmds.add("mount -t vfat /dev/sda1 /mnt >>zlog");
        cmds.add("nohup ./com5 " + 1 + " 2 DDD " + 1 + " " + 1 + " " + 1 + " >>nohup.out &");
        cmds.add("./com5 4 1 SSS 0 1 30");
        cmds.add("./sta %R1Q,9028:");
        cmds.add("./sta %R1Q,17017:2");
        cmds.add("./sta %R1Q,2082:10000");
        cmds.add("tail -1 nohup.out");
        cmds.add("nohup ./com2 " + 1 + " 2 DDD " + 1 + " " + 1 + " " + 1 + " >>nohup.out &");
        cmds.add("ate -s  '" + DateUtils.getDateYMDHMS() + "'");
        cmds.add("GET/M/WI32/WI330");
        cmds.add("GET/I/WI12");
        cmds.add("echo '1' >/sys/class/leds/beep/brightness");
        cmds.add("echo '0' >/sys/class/leds/beep/brightness");
        cmds.add("bmcommon $1FN_SW15001_2019SHGXJT0100_1907081833#");
        cmds.add("bmcommon $1Start_003_99220#");
        cmds.add("bmcommon $1End#");
        cmds.add("./com_2_wd_p");
        cmds.add("./com_2_bm_p");
        cmds.add("./com_5_angle");
        cmds.add("./com_1_wy_p");
        cmds.add("./sta " + "./spl\\ start");
        cmds.add("./sta " + "./car\\ set,rel," + 10.2 + "," + "2.0," + 0);
        cmds.add("./sta " + "./spl\\ reset");
        cmds.add("./sta " + "./car\\ get,stat");
        cmds.add("./sta " + "./car\\ set,mod,stop");
        cmds.add("./sta " + "./car\\ set,mod,free");
        cmds.add("./c1synctime");

        cmds.add("以下是红外");
        cmds.add("./c1fn  SW151001_2019SHGXJT0100_1907081833#");
        cmds.add("./c1start   003_99220#");
        cmds.add("./c1end   _003_99220#");
        cmds.add("./c1current");
        cmds.add("./c1synctime");

        cmds.add("以下是批处理");
        cmds.add("./c1c2 SW151001_2019SHGXJT0100_1907081 003_99220#");
        cmds.add("./c1c2b SW151001_2019SHGXJT0100_1907081 003_99220#");
        cmds.add("nohup  ./test6 && ./com2 1 2 DDD 1 1 1  >>nohup.out  &");
        cmds.add("nohup  echo '1' >/sys/class/leds/beep/brightness &&./test6 && ./com2 1 2 DDD 1 1 1  >>nohup.out  &");
        cmds.add("./c1allend _003_99220#");
        cmds.add("./c1allend2");


        //站号_编码器长度_编码器值_项目编号_小车编号#
        cmds.add("./com_2_laser_start 123_6_112223_2020SHGXWH0112_SW200101#");
        //站号_编码器长度_编码器值
        cmds.add("./com_2_laser__stop 123_6_112224#");
        cmds.add("./com_2_laser_getall");
        cmds.add("以下是惯导");
        cmds.add("./gdcom2 NPOS,31.17.51,121.26.17#");
        cmds.add("./gdcom2 PPOS,123456,111#");
        cmds.add("./gdcom2 STAT,23456,222#");
        cmds.add("./gcdom2 STOP, ON#");
        cmds.add("./gcdom2 STOP, OFF#");
        cmds.add("./gdcom2 EXIT,45678#");
        cmds.add("./gdcom2 GINF#");

        cmds.add("./gdcomBB NPOS,31.17.51,121.26.17#");
        cmds.add("./gdcomBB PPOS,123456,111#");
        cmds.add("./gdcomBB STAT,23456,222#");
        cmds.add("./gdcomBB STOP, ON#");
        cmds.add("./gdcomBB STOP, OFF#");
        cmds.add("./gdcomBB EXIT,45678#");
        cmds.add("./gdcomBB GINF#");
        cmds.add("./gdcomBBA 33345 333");
        cmds.add("以下是雷达");
        cmds.add("./sickset project_lineFeb23#");
        cmds.add("./sickstart 34300#");
        cmds.add("./sickstop");
        cmds.add("./sickconn");
        cmds.add("./sickruncmd2 /home/pi/src/sickconn#");
        cmds.add("./sickruncmd /root/lsxu2#");
        cmds.add("./sickruncmd /root/xucopy%IMULog_17.bin#");
        cmds.add("./sickruncmd /root/sdcopy#");
        cmds.add("./gdcom DELE,ALL#");
        cmds.add("./gdcom NAME,20230226184522#");
        cmds.add("**");

        cmds.add("nohup  echo '1' >/sys/class/leds/beep/brightness &&./test6 && ./com2 1 2 DDD 1 1 1  >>nohup.out  &");
        cmds.add("./gdcomBBENDA");
        cmds.add("ls -l /media/usbhd-sdb1/*.bin");
        cmds.add("mount");
        cmds.add("cp  /media/usbhd-sdb1/IMULog_1.bin /media/usbhd-sda1");

        return cmds;
    }

    private void initButton() {
        btnBlue.setOnClickListener(v -> {
            sendEvent();
        });
        btnClear.setOnClickListener(v -> {
            metRemark.setContentText("");
        });

        titleBar.addAction(new TitleBar.TextAction("连接蓝牙") {
            @Override
            public void performAction(View view) {
                if (ObjectUtils.isEmpty(select)) {
                    showMessage("请先选择蓝牙设备");
                    return;
                }
                connect(select.getAddress(), data -> {
                    showMessage("连接成功");
                }, null, true);
            }
        });


    }

    private void sendEvent() {
        if (ObjectUtils.isEmpty(select)) {
            showMessage("请先选择蓝牙设备");
            return;
        }
        String cmd = esCmd.getEditText().getText().toString().trim();
        //  cmd += "\r\n";
        if (ObjectUtils.isEmpty(cmd)) {
            showMessage("输入发送命令");
            return;
        }

        sendMessage(select.getAddress(), cmd, true, response -> {

            String data = response;
           /* if (response.contains("c1current")) {
                data = response.replace(";", "\r\n");
            }*/
            metRemark.setContentText(data);

            adapterData.add(0, cmd);

            esCmd.setAdapter(new EditSpinnerAdapter(adapterData)
                    .setTextColor(ResUtils.getColor(R.color.black))
                    .setTextSize(32)
                    .setBackgroundSelector(R.drawable.selector_custom_spinner_bg));
        });
    }


    private void initmsBle() {
        msBle.setOnClickListener(v -> initBlueList());
        msBle.setOnItemSelectedListener((view, position, id, item) -> {
            int index = (int) id;
            if (0 == index) select = null;
            select = listDivs.get((int) id);
        });

    }


    private void initBlueList() {
        listDivs = getDrives();
        List<String> ble = getDriveName(listDivs);

        MaterialSpinnerAdapter adapter = new MaterialSpinnerAdapter(BluetoothActivity.this, ble);
        msBle.setAdapter(adapter);

        msBle.setItems(ble);

    }


}