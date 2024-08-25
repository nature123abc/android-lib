package com.nature.demo.ui;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.ObjectUtils;
import com.dk.lib_dk.utils.back.SuccessBack;
import com.nature.demo.R;
import com.nature.demo.base.BsBluetoothActivity;
import com.nature.demo.databinding.ActivityDynamic2Binding;
import com.nature.dk_android.service.bluetooth.sw.Command;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.edittext.MultiLineEditText;
import com.xuexiang.xui.widget.spinner.materialspinner.MaterialSpinner;
import com.xuexiang.xui.widget.spinner.materialspinner.MaterialSpinnerAdapter;

import java.util.List;


public class DynamicActivity2 extends BsBluetoothActivity<ActivityDynamic2Binding> {



    MaterialSpinner msBle;


    TextView txtInfo;



    MultiLineEditText metRemark;

    List<BluetoothDevice> listDivs;
    BluetoothDevice select;


    public void btnClick(View view) {
        switch (view.getId()) {
            case R.id.btnStartInsRadar:
                if (!checkBluetooth()) return;
                StartInsRadar();
                break;
            case R.id.btnEndInsRadar:
                if (!checkBluetooth()) return;
                EndInsRadar();
                break;
            case R.id.btnStaticInsRadar:
                if (!checkBluetooth()) return;
                StaticInsRadar();
                break;

            case R.id.btnClear:
                metRemark.setContentText("");
                break;

            case R.id.btnRadarStatus:
                status();
                break;

        }
    }

    String cmdConn = "./sickruncmd2 /home/pi/src/sickconn#";
    private void status() {
        setStatus("获取雷达状态...");
        sendMessage(cmdConn, data1 -> {//启动雷达
            showLog(data1);
        });
    }


    private String getInsCodeStation() {
        return 12452 + " " + Command.getCommonStationId(1); //./gdcomA 33345 333
    }

    private void StaticInsRadar() {
        setStatus("获取惯导静态...");
        sendMessage(Command.getGdComAA("GINF", ""), data -> {//获取惯导
            showLog(data);
            setStatus("获取温度静态...");
            sendMessage(Command.getCommand("CmdgetWD"), data1 -> {//获取温度
                showLog(data1);
                metRemark.setContentText(metRemark.getContentText() + data1);
                int order = 0;
                double temperature = 24;
                try {
                    String[] currSensorInfo = data1.split(",");
                    if (currSensorInfo.length < 2) {
                        showMessage("采集数据错误" + ":" + data1);
                        return;
                    }
                    temperature = Double.valueOf(currSensorInfo[1]);
                    if (Math.abs(temperature) > 80) {
                        showMessage("测量温度为: " + temperature + " 超出限差，请重新测量");
                        return;
                    }
                } catch (Exception e) {
                    order = 0;
                }
                setStatus("获取小车静态...");
                sendMessage(Command.getCmd("getstatic"), data2 -> {//获取惯导
                    showLog(data2);
                });
            });
        });
    }

    private void setStatus(String s) {
        txtInfo.setText(s);
    }

    private void EndInsRadar() {
        setStatus("停止小车...");
        String cmd1 = Command.getCmd("stopNohup");
        sendMessage(cmd1, data -> {//
            showLog(data);
            setStatus("停止惯导...");
            sendMessage(Command.getCmd("stopIns"), data1 -> {//停止惯导
                showLog(data1);
                setStatus("停止雷达...");
                sendMessage("./sickstop", data2 -> {//停止雷达
                    showLog(data2);
                });
            });
        });
    }

    private void StartInsRadar() {
        setStatus("启动惯导...");
        sendMessage(Command.getCmd("startIns", getInsCodeStation()), data -> {//启动惯导
            showLog(data);
            setStatus("获取雷达状态...");
            sendMessage(cmdConn, data1 -> {//启动雷达
                showLog(data1);
                setStatus("启动雷达...");
                sendMessage("/sickstart2 34300#", data2 -> {//启动雷达
                    showLog(data2);
                    String info = "" + 1 + " 2 DDD " + 1 + " " + 1 + " " + 25 + "";
                    String cmd2 = Command.getCmd("startNohup", info);
                    setStatus("启动小车...");
                    sendMessage(cmd2, data3 -> {//启动小车
                        showLog(data3);
                    });
                });
            });
        });
    }

    private void showLog(String data) {
        metRemark.setContentText(metRemark.getContentText() + data);
        setStatus("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titleBar.setTitle("小车动态");
        titleBar.addAction(new TitleBar.TextAction("断开蓝牙") {
            @Override
            public void performAction(View view) {
                stopConnect();
            }
        });

        initmsBle();
        initBlueList();
    }


    private boolean checkBluetooth() {
        if (ObjectUtils.isEmpty(select)) {
            showMessage("请先选择蓝牙设备");
            return false;
        }
        return true;
    }

    private void sendMessage(String cmd, SuccessBack<String> o) {
        sendMessage(select.getAddress(), cmd, true, o, false);
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
        MaterialSpinnerAdapter adapter = new MaterialSpinnerAdapter(DynamicActivity2.this, ble);
        msBle.setAdapter(adapter);
        msBle.setItems(ble);
    }
}