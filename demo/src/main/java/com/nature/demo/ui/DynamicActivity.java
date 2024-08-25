package com.nature.demo.ui;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.blankj.utilcode.util.ObjectUtils;
import com.dk.lib_dk.utils.back.SuccessBack;
import com.nature.demo.R;
import com.nature.demo.base.BsBluetoothActivity;
import com.nature.demo.databinding.ActivityDynamicBinding;
import com.nature.demo.utils.cmd.car.CarCmd;
import com.nature.demo.utils.cmd.car.CarCmdType;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.edittext.MultiLineEditText;
import com.xuexiang.xui.widget.spinner.materialspinner.MaterialSpinner;
import com.xuexiang.xui.widget.spinner.materialspinner.MaterialSpinnerAdapter;

import java.util.List;


public class DynamicActivity extends BsBluetoothActivity<ActivityDynamicBinding> {


    MaterialSpinner msBle;
    MultiLineEditText metRemark;

    List<BluetoothDevice> listDivs;
    BluetoothDevice select;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
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

    private void initView() {
        getBinding().btnStart.setOnClickListener(view -> {
            if (!checkBluetooth()) return;
            startDynamic();
        });
        getBinding().btnEnd.setOnClickListener(view -> {
            if (!checkBluetooth()) return;
            endDynamic();
        });
        getBinding().btnStartRadar.setOnClickListener(view -> {
            if (!checkBluetooth()) return;
            startRadar();
        });
        getBinding().btnStartRadar1.setOnClickListener(view -> {
            if (!checkBluetooth()) return;
            startRadar1();
        });
        getBinding().btnEndRadar.setOnClickListener(view -> {
            if (!checkBluetooth()) return;
            endRadar();
        });
        getBinding().btnStartIns.setOnClickListener(view -> {
            if (!checkBluetooth()) return;
            startIns();
        });

        msBle = getBinding().msBle;
        metRemark = getBinding().metRemark;
    }

 public void btnClick(View view) {
        switch (view.getId()) {
            case R.id.btnStart:

                break;
            case R.id.btnEnd:
                if (!checkBluetooth()) return;
                endDynamic();
                break;
            case R.id.btnStartRadar:
                if (!checkBluetooth()) return;
                startRadar();
            case R.id.btnStartRadar1:
                if (!checkBluetooth()) return;
                startRadar1();
                break;
            case R.id.btnEndRadar:
                if (!checkBluetooth()) return;
                endRadar();
                break;
            case R.id.btnStartIns:
                if (!checkBluetooth()) return;
                startIns();
                break;
            case R.id.btnEndIns:
                if (!checkBluetooth()) return;
                endIns();
            case R.id.btnCalib:

                calibIns();
                break;
            case R.id.btnStartInsRadar:
                if (!checkBluetooth()) return;
                startInsRadar();
            case R.id.btnStartInsRadar1:
                if (!checkBluetooth()) return;
                startInsRadar1();
                break;
            case R.id.btnEndInsRadar:
                if (!checkBluetooth()) return;
                endInsRadar();
                break;
            case R.id.btnCommBlue:
                startActivity(new Intent(this, BluetoothActivity.class));
                break;
        }
    }

    private void calibIns() {
        showLoadingDialog();
        metRemark.setContentText("");
        sendMessage("./gdcom GINF#", data -> {//
            metRemark.setContentText(metRemark.getContentText()   + data);
            sleep();
            sendMessage("./com_2_wd_p", data1 -> {
                metRemark.setContentText(metRemark.getContentText() + "分割\r\n" + data1);
                sleep();
                sendMessage("./getstatic", data2 -> {
                    metRemark.setContentText(metRemark.getContentText() + "分割\r\n" + data2);
                    cancleLoadingDialog();
                });
            });
        });
    }

    private void sleep() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startInsRadar1() {
        //
        showLoadingDialog();
        metRemark.setContentText("");
        sendMessage("./c1c2b SW151001_2019SHGXJT0100_1907081 003_99220#", data -> {//启动雷达
            metRemark.setContentText(metRemark.getContentText() + data);
            sendMessage("./gdcomA 33345 333", data1 -> {
                metRemark.setContentText(metRemark.getContentText() + data1);
                sendMessage("nohup  echo '1' >/sys/class/leds/beep/brightness &&./test6 && ./com2 1 2 DDD 1 1 1  >>nohup.out  &", data2 -> {
                    metRemark.setContentText(metRemark.getContentText() + data2);
                    cancleLoadingDialog();
                });
            });
        });
    }

    private void startRadar1() {
        //./c1c2 SW151001_2019SHGXJT0100_1907081 003_99220#
        showLoadingDialog();
        metRemark.setContentText("");
        sendMessage("./c1c2b SW151001_2019SHGXJT0100_1907081 003_99220#", data -> {
            metRemark.setContentText(metRemark.getContentText() + data);
            sendMessage("nohup  echo '1' >/sys/class/leds/beep/brightness &&./test6 && ./com2 1 2 DDD 1 1 1  >>nohup.out  &", data1 -> {
                metRemark.setContentText(metRemark.getContentText() + data1);
                cancleLoadingDialog();
            });
        });
    }

    private void endInsRadar() {
        //./c1allend _003_99220#  && ./gdcomENDA
        showLoadingDialog();
        metRemark.setContentText("");
        sendMessage("./c1allendnofinish _003_99220#  && ./gdcomENDA", data -> {//启动雷达
            cancleLoadingDialog();
            metRemark.setContentText(metRemark.getContentText() + data);
        });
    }

    private void startInsRadar() {

        showLoadingDialog();
        metRemark.setContentText("");
        sendMessage("./c1c2 SW151001_2019SHGXJT0100_1907081 003_99220#", data -> {//启动雷达
            metRemark.setContentText(metRemark.getContentText() + data);
            sendMessage("./gdcomA 33345 333", data1 -> {
                metRemark.setContentText(metRemark.getContentText() + data1);
                sendMessage("nohup  echo '1' >/sys/class/leds/beep/brightness &&./test6 && ./com2 1 2 DDD 1 1 1  >>nohup.out  &", data2 -> {
                    metRemark.setContentText(metRemark.getContentText() + data2);
                    cancleLoadingDialog();
                });
            });
        });
    }

    private void endIns() {
        //3. 停止
        //./gdcomBBENDA
        showLoadingDialog();
        metRemark.setContentText("");
        sendMessage("./gdcomENDA", data -> {
            metRemark.setContentText(metRemark.getContentText() + data);
            cancleLoadingDialog();
        });
    }

    private void startIns() {
        //11 启动
        // ./gdcomBBA 33345 333


        //2. 启动动态
        //nohup  echo '1' >/sys/class/leds/beep/brightness &&./test6 && ./com2 1 2 DDD 1 1 1  >>nohup.out  &


        showLoadingDialog();
        metRemark.setContentText("");
        sendMessage("./gdcomA 33345 333", data -> {
            metRemark.setContentText(metRemark.getContentText() + data);
            sendMessage("nohup  echo '1' >/sys/class/leds/beep/brightness &&./test6 && ./com2 1 2 DDD 1 1 1  >>nohup.out  &", data1 -> {
                metRemark.setContentText(metRemark.getContentText() + data1);
                cancleLoadingDialog();
            });
        });
    }

    private void startRadar() {
        //./c1c2 SW151001_2019SHGXJT0100_1907081 003_99220#
        showLoadingDialog();
        metRemark.setContentText("");
        sendMessage("./c1c2 SW151001_2019SHGXJT0100_1907081 003_99220#", data -> {
            metRemark.setContentText(metRemark.getContentText() + data);
            sendMessage("nohup  echo '1' >/sys/class/leds/beep/brightness &&./test6 && ./com2 1 2 DDD 1 1 1  >>nohup.out  &", data1 -> {
                metRemark.setContentText(metRemark.getContentText() + data1);
                cancleLoadingDialog();
            });
        });
    }

    private void endRadar() {//结束雷达测试带灯带继电器
        showLoadingDialog();
        metRemark.setContentText("");
        sendMessage("./c1allend _003_99220#", data -> {
            metRemark.setContentText(metRemark.getContentText() + data);
            cancleLoadingDialog();
        });
    }




    private void endDynamic() {
        showLoadingDialog();
        metRemark.setContentText("");
        sendMessage(CarCmd.getCmd(CarCmdType.停止测量), data -> {
            metRemark.setContentText(metRemark.getContentText() + data);
            sendMessage(CarCmd.getCmd(CarCmdType.读取最后一行), data1 -> {
                metRemark.setContentText(metRemark.getContentText() + data1);
                sendMessage(CarCmd.getCmd(CarCmdType.关灯), data3 -> {
                    //停止
                    cancleLoadingDialog();
                    metRemark.setContentText(metRemark.getContentText() + data3);
                });
            });
        });
    }

    private void startDynamic() {


        metRemark.setContentText("");
        showLoadingDialog();

        sendMessage("./com_2_wd_p", data1 -> {
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
            sendMessage(CarCmd.dynamicMeasure(order, 1, 1, (int) temperature), data2 -> {
                metRemark.setContentText(data2);
                sendMessage(CarCmd.getCmd(CarCmdType.亮灯), data3 -> {
                    //启动成功
                    cancleLoadingDialog();
                    metRemark.setContentText(metRemark.getContentText() + data3);
                });
            });
        });

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
        MaterialSpinnerAdapter adapter = new MaterialSpinnerAdapter(DynamicActivity.this, ble);
        msBle.setAdapter(adapter);
        msBle.setItems(ble);
    }
}