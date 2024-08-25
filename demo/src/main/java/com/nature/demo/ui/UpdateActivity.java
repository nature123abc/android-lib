package com.nature.demo.ui;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.View;

import com.blankj.utilcode.util.ObjectUtils;
import com.dk.error.CommonException;
import com.dk.lib_dk.utils.socket.BTSocket;
import com.nature.demo.databinding.ActivityUpdateBinding;
import com.nature.demo.utils.view.BBlueActivity;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.spinner.materialspinner.MaterialSpinnerAdapter;

import java.util.ArrayList;
import java.util.List;

public class UpdateActivity extends BBlueActivity<ActivityUpdateBinding> {

    @Override
    public void serviceConnected() {

    }


    List<BluetoothDevice> listDivs;
    BluetoothDevice select;

    protected List<String> getDriveName(List<BluetoothDevice> listDivs) {
        List<String> ble = new ArrayList<>();
        ble.add("");
        for (BluetoothDevice d : listDivs) {
            @SuppressLint("MissingPermission") String name = d.getName();
            if (ObjectUtils.isEmpty(name)) name = d.getAddress();
            ble.add(name);
        }
        return ble;
    }

    private void initBlueList() {
        listDivs = new ArrayList<>(BTSocket.getDevs());
        List<String> ble = getDriveName(listDivs);
        MaterialSpinnerAdapter adapter = new MaterialSpinnerAdapter(this, ble);
        getBinding().msBle.setAdapter(adapter);
        getBinding().msBle.setItems(ble);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBinding().msBle.setOnClickListener(v -> initBlueList());
        getBinding().msBle.setOnItemSelectedListener((view, position, id, item) -> {
            int index = (int) id;
            if (0 == index) select = null;
            select = listDivs.get((int) id);
        });


        getBinding().btnBlue.setOnClickListener(v -> {
            updateSoftware();
        });

        titleBar.addAction(new TitleBar.TextAction("连接蓝牙") {
            @Override
            public void performAction(View view) {
                if (null != select)
                    connect(select.getAddress(), data -> {
                        showMessage("连接成功");
                        cancleLoading();
                    });
            }
        });
    }

    private void updateSoftware() {
        getBinding().metRemark.setContentText("");
        showLoading();
        sendCmd2Sw("umount /mnt", data -> {
            getBinding().metRemark.setContentText(getBinding().metRemark.getContentText().trim() + data);
            sleep(1000);
            sendCmd2Sw("mount -t vfat /dev/sda1 /mnt >>zlog", data1 -> {//mount -t vfat /media/usbhd-sda4 /mnt >>zlog
                getBinding().metRemark.setContentText(getBinding().metRemark.getContentText().trim() + data1);
                sleep(2000);
                sendCmd2Sw("cat /mnt/test", data2 -> {
                    getBinding().metRemark.setContentText(getBinding().metRemark.getContentText().trim() + data2);
                    sleep(2000);
                    if (data2.contains("Sucessfullly")) {
                        sendCmd2Sw("tar xvf /mnt/zzz2.tar", data3 -> {
                            getBinding().metRemark.setContentText(getBinding().metRemark.getContentText().trim() + data);
                            showMessage("更新成功");
                            cancleLoading();
                        });
                    } else {
                        handelError(new CommonException("错误,请重新插入U盘"));
                        sendCmd2Sw("umount /mnt", data3 -> {
                            getBinding().metRemark.setContentText(getBinding().metRemark.getContentText().trim() + data3);
                            cancleLoading();
                        });
                    }
                });
            });
        });
    }

    @Override
    protected String getTitleMsg() {
        return "更新程序";
    }
}