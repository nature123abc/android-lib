package com.nature.demo.ui;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.View;

import com.blankj.utilcode.util.ObjectUtils;
import com.dk.common.DateUtils;
import com.dk.error.CommonException;
import com.dk.lib_dk.utils.back.FailedCallback;
import com.dk.lib_dk.utils.back.SuccessBack;
import com.nature.demo.R;
import com.nature.demo.base.BsBluetoothActivity;
import com.nature.demo.databinding.ActivityDataoutputBinding;
import com.nature.dk_android.service.bluetooth.sw.Command;
import com.xuexiang.xui.utils.ResUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.edittext.MultiLineEditText;
import com.xuexiang.xui.widget.spinner.editspinner.EditSpinner;
import com.xuexiang.xui.widget.spinner.editspinner.EditSpinnerAdapter;
import com.xuexiang.xui.widget.spinner.materialspinner.MaterialSpinner;
import com.xuexiang.xui.widget.spinner.materialspinner.MaterialSpinnerAdapter;
import com.xuexiang.xui.widget.textview.supertextview.SuperButton;

import java.util.Arrays;
import java.util.List;

public class DataoutputActivity extends BsBluetoothActivity<ActivityDataoutputBinding> {

    MaterialSpinner msBle;

    EditSpinner esCmd;

    SuperButton btnBlue;

    MultiLineEditText metRemark;

    List<BluetoothDevice> listDivs;
    BluetoothDevice select;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        titleBar.setTitle("导出数据");
        titleBar.addAction(new TitleBar.TextAction("连接蓝牙") {
            @Override
            public void performAction(View view) {
                if (ObjectUtils.isEmpty(select)) {
                    showMessage("先选择蓝牙信息");
                    return;
                }
                connect(select.getAddress(), data -> {

                });
            }
        });


        initmsBle();
        initBlueList();

        initEsCmd();

        initButton();
    }

    private void initView() {
        msBle = getBinding().msBle;
        esCmd = getBinding().esCmd;
        btnBlue = getBinding().btnBlue;
        metRemark = getBinding().metRemark;
    }

    private void initEsCmd() {


        esCmd.setAdapter(new EditSpinnerAdapter(setList())
                .setTextColor(ResUtils.getColor(R.color.black))
                .setTextSize(32)
                .setBackgroundSelector(R.drawable.selector_custom_spinner_bg)
        );
    }

    private List setList() {
        String[] temp = new String[]{
                "SD卡",
                "U盘"
        };
        return Arrays.asList(temp);
    }

    int index = 0;

    private void initButton() {
        btnBlue.setOnClickListener(v -> {
            sendEvent();
        });
        getBinding().btnTemp.setOnClickListener(v -> {
            metRemark.setContentText("");
            if (ObjectUtils.isEmpty(select)) {
                showMessage("请先选择蓝牙设备");
                return;
            } //cp /nohup_Z3438.out /media/usbhd-sda4/
            index = 0;
            showLoading();
            outPutMedia("ls /media", data -> {
                showMessage("导出成功");
                cancleLoading();
            }, throwable -> {
                outPutMedia("ls /dev/sd*", data -> {
                    showMessage("导出成功");
                    cancleLoading();
                }, throwable1 -> {
                    showMessage("导出失败");
                    cancleLoading();
                });
            });
        });
    }

    private void outPutMedia(String dir, SuccessBack<Boolean> successBack, FailedCallback failedCallback) {
        sendMessage(dir, ss -> {
            if (ss.contains(" No such file or directory")) {
                failedCallback.onError(new CommonException("没有找到指定目录"));
                return;
            }
            var sss = ss.split("\t");
            sss[0]="musbhd-sda4";//ls /media
//[1;34musbhd-sda4[0m

            forCopy(sss, data1 -> {
                successBack.getData(true);
            }, failedCallback);
        });
    }

    private void forCopy(String[] sss, SuccessBack<Boolean> successBack, FailedCallback failedCallback) {
        var s = sss[index];
        if (s.startsWith("musbhd-sd")) {
            copyData(s, data1 -> {
                successBack.getData(true);
            }, throwable -> {
                index++;
                if (index >= sss.length) {
                    failedCallback.onError(new CommonException("没有找到指定文件"));
                }
                forCopy(sss, successBack, failedCallback);
            });
        }else {
            index++;
            forCopy(sss, successBack, failedCallback);
        }
    }

    private void copyData(String path, SuccessBack<Boolean> successBack, FailedCallback failedCallback) {
        sendMessage("cat test", d -> {
            if (d.contains("Sucessfullly")) {
                sendMessage("cp /nohup*.out /media/" + path + "/" + DateUtils.date2Str(DateUtils.yyyymmddhhmmss.get()) + ".txt", dd -> {
                    successBack.getData(true);
                });
            } else {
                failedCallback.onError(new CommonException("没有test文件"));
            }
        });

    }

    private void sendEvent() {
        metRemark.setContentText("");
        if (ObjectUtils.isEmpty(select)) {
            showMessage("请先选择蓝牙设备");
            return;
        }
        String cmd = esCmd.getEditText().getText().toString().trim();

        if (ObjectUtils.isEmpty(cmd)) {
            showMessage("选择需要导出的位置");
            return;
        }
        if (cmd.equals("SD卡")) {
            showLoadingDialog();
            sendMessage(Command.getCommand("umount_sd"), data -> {
                showData(data);
                sleep(1000);
                sendMessage(Command.getCommand("mount_sd"), data1 -> {
                    showData(data1);
                    sleep(1000);
                    sendMessage(Command.getCommand("clear_sd"), data2 -> {
                        showData(data2);
                        sleep(1000);
                        if (((String) data2).contains("Sucessfullly")) {                  //清空U盘成功，发送 导出命令
                            sendMessage(Command.getCommand("outPutData_sd"), data3 -> {
                                cancleLoadingDialog();
                                showData(data3);
                                if (((String) data3).contains("finish")) {        //数据导出成功
                                    showMessage("数据导出成功");
                                } else {
                                    showMessage("数据导出失败");
                                }
                                showData(data3);
                            });
                        } else {
                            cancleLoadingDialog();
                            showMessage("数据导出失败");
                        }

                    });
                });
            });
        } else if (cmd.equals("U盘")) {
            showLoadingDialog();
            sendMessage(Command.getCommand("umount"), data -> {
                showData(data);
                sendMessage(Command.getCommand("mount"), data1 -> {
                    showData(data1);
                    sleep(1000);
                    sendMessage(Command.getCommand("clear1"), data2 -> {
                        showData(data2);
                        sleep(1000);
                        if (((String) data2).contains("Sucessfullly")) {                  //清空U盘成功，发送 导出命令
                            sendMessage(Command.getCommand("outPutData"), data3 -> {
                                showData(data3);
                                sleep(1000);
                                cancleLoadingDialog();
                                if (((String) data3).contains("finish")) {        //数据导出成功
                                    showMessage("数据导出成功");
                                } else {
                                    showMessage("数据导出失败");
                                }
                            });
                        } else {
                            cancleLoadingDialog();
                            showMessage("数据导出失败");
                        }
                    });
                });
            });
        }

    }

    private void showData(Object data) {
        metRemark.setContentText(metRemark.getContentText() + data);
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
        MaterialSpinnerAdapter adapter = new MaterialSpinnerAdapter(DataoutputActivity.this, ble);
        msBle.setAdapter(adapter);
        msBle.setItems(ble);
    }
}