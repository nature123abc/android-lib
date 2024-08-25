package com.nature.demo.ui;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.View;

import com.blankj.utilcode.util.ObjectUtils;
import com.nature.demo.R;
import com.nature.demo.base.BsBluetoothActivity;
import com.nature.demo.databinding.ActivityConnonBleBinding;
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

public class ConnonBleActivity extends BsBluetoothActivity<ActivityConnonBleBinding> {



    MaterialSpinner msBle;


    EditSpinner esCmd;

    SuperButton btnBlue;

    MultiLineEditText metRemark;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        titleBar.setTitle("一般蓝牙");
        titleBar.addAction(new TitleBar.TextAction("断开蓝牙") {
            @Override
            public void performAction(View view) {
                stopConnect();
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
                "%R1Q,17017:2",
                "%R1Q,9028:",
                "%R1Q,5003:",
                "%R1Q,2023:",
                "%R1Q,5004:",
                "%R1Q,9027:0,0"
        };
        return Arrays.asList(temp);
    }

    private void initButton() {
        btnBlue.setOnClickListener(v -> {
            sendEvent();
        });
    }

    private void sendEvent() {
        if (ObjectUtils.isEmpty(select)) {
            showMessage("请先选择蓝牙设备");
            return;
        }
        String cmd = esCmd.getEditText().getText().toString().trim();
        cmd += "\r\n";
        if (ObjectUtils.isEmpty(cmd)) {
            showMessage("输入发送命令");
            return;
        }

        sendMessage(select.getAddress(), cmd, true, response -> {
            metRemark.setContentText(response);
        });
    }


    BluetoothDevice select;

    private void initmsBle() {
        msBle.setOnClickListener(v -> initBlueList());
        msBle.setOnItemSelectedListener((view, position, id, item) -> {
            int index = (int) id;
            if (0 == index) select = null;
            select = listDivs.get((int) id);
        });

    }

    List<BluetoothDevice> listDivs;

    private void initBlueList() {
        listDivs = getDrives();
        List<String> ble = getDriveName(listDivs);
        MaterialSpinnerAdapter adapter = new MaterialSpinnerAdapter(ConnonBleActivity.this, ble);
        msBle.setAdapter(adapter);

        msBle.setItems(ble);

    }
}