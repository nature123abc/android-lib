package com.nature.demo.ui;

import android.os.Bundle;

import com.blankj.utilcode.util.ObjectUtils;
import com.dk.error.CommonException;
import com.dk.lib_dk.utils.ui.DoubleClickUtils;
import com.nature.demo.base.BSwActivity;
import com.nature.demo.databinding.ActivityOutputBinding;
import com.nature.demo.utils.cmd.car.Command;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OutputActivity extends BSwActivity<ActivityOutputBinding> {

    @Override
    public void serviceConnected() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getBinding().btnStartOutput.setOnClickListener(view -> {
            if (!DoubleClickUtils.isFastDoubleClick()) {
                outputData();
            }
        });

        getBinding().linOutput.setOnClickListener(view -> {
            if (!DoubleClickUtils.isFastDoubleClick()) {
                //setPicker(getResources().getStringArray(R.array.select_output_position_items));
                List<String> infos = new ArrayList<>();
                infos.add("U盘");
                infos.add("SD卡");
                setPicker(infos);
            }
        });
    }

    private void setPicker(List<String> info) {
        showPicker(info, "", (i, s) -> {
            getBinding().txtPosition.setText(s);
        });
    }

    public static String getDateFormat(String fmt) {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(fmt);//可以方便地修改日期格式
        return dateFormat.format(now);
    }


    private void outputData() {
        final String position = getBinding().txtPosition.getText().toString().trim();
        if (ObjectUtils.isEmpty(position)) {
            showMessage("请先选择导出数据位置");
            return;
        }
        showLoadingDialog();
        String SystemDateTime = getDateFormat("yyyyMMddHHmm");

        String name = SystemDateTime;

        showLoadingDialog();
        if (position.equals("U盘")) {
            sendMessage(Command.getCommand("umount"), data -> {
                sendMessage(Command.getCommand("mount"), data1 -> {
                    sendMessage(Command.getCommand("clear1"), data2 -> {
                        if ((data2).contains("Sucessfullly")) {                  //清空U盘成功，发送 导出命令
                            sendMessage(Command.getCommand("outPutData", name), data3 -> {
                                cancleLoadingDialog();
                                if ((data3).contains("finishusb")) {        //数据导出成功
                                    showMessage("数据导出成功");
                                } else {
                                    showMessage("数据导出失败");
                                }
                            });
                        } else {
                            handelError(new CommonException("数据导出失败"));
                        }
                    });
                });
            });
        } else {
            sendMessage(Command.getCommand("umount_sd"), data -> {
                sendMessage(Command.getCommand("mount_sd"), data1 -> {
                    sendMessage(Command.getCommand("clear_sd"), data2 -> {
                        if (((String) data2).contains("Sucessfullly")) {                  //清空U盘成功，发送 导出命令
                            sendMessage(Command.getCommand("outPutData_sd", name), data3 -> {
                                cancleLoadingDialog();
                                if (((String) data3).contains("finishsd")) {        //数据导出成功
                                    showMessage("数据导出成功");
                                } else {
                                    showMessage("数据导出失败");
                                }

                            });
                        } else {
                            cancleLoadingDialog();
                            handelError(new CommonException("数据导出失败"));
                        }
                    });
                });
            });
        }
    }


    @Override
    protected String getTitleMsg() {
        return "导出数据";
    }
}