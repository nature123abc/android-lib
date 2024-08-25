package com.nature.demo.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ObjectUtils;
import com.dk.common.DoubleUtils;
import com.dk.error.CommonException;
import com.dk.lib_dk.db.DbUtils;
import com.dk.lib_dk.utils.back.SuccessBack;
import com.dk.lib_dk.utils.socket.BTSocket;
import com.nature.demo.base.Contents;
import com.nature.demo.dao.BaseReferInfoDao;
import com.nature.demo.databinding.ActivityCalibswBinding;
import com.nature.demo.utils.SwCarData;
import com.nature.demo.utils.db.BaseReferInfo;
import com.nature.demo.utils.db.DbManager;
import com.nature.demo.utils.view.BBlueActivity;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.spinner.materialspinner.MaterialSpinnerAdapter;

import java.util.ArrayList;
import java.util.List;

import me.rosuh.filepicker.config.FilePickerManager;

public class CalibswActivity extends BBlueActivity<ActivityCalibswBinding> {

    @Override
    public void serviceConnected() {

    }

    List<BluetoothDevice> listDivs;
    BluetoothDevice select;
    BaseReferInfo parm;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FilePickerManager.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                var list = FilePickerManager.INSTANCE.obtainData();
                if (ObjectUtils.isNotEmpty(list)) {
                    var path = list.get(0);
                    getBinding().edtPath.setText(path);
                    try {
                        DbManager.getInstance().initDb(CalibswActivity.this, path);
                    } catch (Exception e) {
                        handelError(e);
                        return;
                    }
                    obtDbInfo();

                }
            }
        }
    }

    private void obtDbInfo() {
        List<BaseReferInfo> ls = DbUtils.query(BaseReferInfo.class, BaseReferInfoDao.Properties.Id.isNotNull());
        if (ls.size() != 1) {
            handelError(new CommonException("小车参数不唯一"));
            return;
        }
        parm = ls.get(0);
        getBinding().edtCarSn.setText(parm.meaInstrumentID);
        getBinding().edtTempParm.setText(formatStr(parm.materialRef, 7));
        getBinding().initCD.setText(formatStr(parm.lenCD, 3));
        getBinding().initAE.setText(formatStr(parm.lenAE, 3));
        getBinding().initBF.setText(formatStr(parm.lenBF, 3));
        getBinding().initH.setText(formatStr(parm.lenH, 3));
        getBinding().initR.setText("" + parm.wheelResolution);
        getBinding().initAC.setText(formatStr(parm.lenAC, 3));
        getBinding().initBD.setText(formatStr(parm.lenBD, 3));
        getBinding().initAB.setText(formatStr(parm.lenAB, 3));
        getBinding().initEO.setText(formatStr(parm.lenEO, 3));
        getBinding().initFO.setText(formatStr(parm.lenFO, 3));
        getBinding().initAT.setText(formatStr(parm.distSensorAT, 3));
        getBinding().initBT.setText(formatStr(parm.distSensorBT, 3));
        getBinding().initCT.setText(formatStr(parm.distSensorCT, 3));
        getBinding().initDT.setText(formatStr(parm.distSensorDT, 3));
        getBinding().initKL.setText(formatStr(parm.leftDistSensorKL, 6));
        getBinding().initKR.setText(formatStr(parm.rightDistSensorKR, 6));
        getBinding().initXT.setText(formatStr(parm.angleSensorXT, 5));
        getBinding().initYT.setText(formatStr(parm.angleSensorYT, 5));
        getBinding().initKX.setText(formatStr(parm.angleSensorKX, 6));
        getBinding().initKY.setText(formatStr(parm.angleSensorKY, 6));
        getBinding().initTemp.setText(formatStr(parm.temperature, 1));
    }

    private String formatStr(Double val, int count) {
        return DoubleUtils.formatStr(val, count, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBinding().btnSave.setOnClickListener(v -> {
            saveEvent();
        });
        getBinding().btnSelectDb.setOnClickListener(v -> {
            FilePickerManager
                    .from(this)
                    .showCheckBox(true)
                    .maxSelectable(1)
                    .setCustomRootPath(Contents.SystemPath)
                    .forResult(FilePickerManager.REQUEST_CODE);
        });



        getBinding().msBle.setOnClickListener(v -> initBlueList());
        getBinding().msBle.setOnItemSelectedListener((view, position, id, item) -> {
            int index = (int) id;
            if (0 == index) select = null;
            select = listDivs.get((int) id);
        });

        getBinding().btnMeaAT.setOnClickListener(v -> {
            measureData(data -> {
                getBinding().initAT.setText(formatStr(data.wyA, 5));
            });
        });
        getBinding().btnMeaCT.setOnClickListener(v -> {
            measureData(data -> {
                getBinding().initCT.setText(formatStr(data.wyA, 5));
            });
        });
        getBinding().btnMeaBT.setOnClickListener(v -> {
            measureData(data -> {
                getBinding().initBT.setText(formatStr(data.wyB, 5));
            });
        });
        getBinding().btnMeaDT.setOnClickListener(v -> {
            measureData(data -> {
                getBinding().initDT.setText(formatStr(data.wyB, 5));
            });
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

    static final String cmd = "./com3s 4 1 SSS 0 1 30";

    private void measureData(SuccessBack<SwCarData> successBack) {
        showLoading();
        sendCmd2Sw(cmd,str -> {
            getBinding().txtInfo.setText(str);
            cancleLoading();
            SwCarData carData = null;
            try {
                carData = new SwCarData(str);
                carData.init();
            } catch (Exception e) {
                handelError(e);
                return;
            }
            successBack.getData(carData);
        });
    }

    private void initBlueList() {
        listDivs = new ArrayList<>(BTSocket.getDevs());
        List<String> ble = getDriveName(listDivs);
        MaterialSpinnerAdapter adapter = new MaterialSpinnerAdapter(this, ble);
        getBinding().msBle.setAdapter(adapter);
        getBinding().msBle.setItems(ble);
    }

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

    private void saveEvent() {
        if (ObjectUtils.isEmpty(parm)) {
            showMessage("先打开数据库");
            return;
        }
        try {
            parm.meaInstrumentID = getBinding().edtCarSn.getText().toString().trim();
            parm.materialRef = Double.valueOf(getBinding().edtTempParm.getText().toString().trim());
            parm.lenCD = Double.valueOf(getBinding().initCD.getText().toString().trim());
            parm.lenAE = Double.valueOf(getBinding().initAE.getText().toString().trim());
            parm.lenBF = Double.valueOf(getBinding().initBF.getText().toString().trim());
            parm.lenH = Double.valueOf(getBinding().initH.getText().toString().trim());
            parm.wheelResolution = Integer.valueOf(getBinding().initR.getText().toString().trim());
            parm.lenAC = Double.valueOf(getBinding().initAC.getText().toString().trim());
            parm.lenBD = Double.valueOf(getBinding().initBD.getText().toString().trim());
            parm.lenAB = Double.valueOf(getBinding().initAB.getText().toString().trim());
            parm.lenEO = Double.valueOf(getBinding().initEO.getText().toString().trim());
            parm.lenFO = Double.valueOf(getBinding().initFO.getText().toString().trim());
            parm.distSensorAT = Double.valueOf(getBinding().initAT.getText().toString().trim());
            parm.distSensorBT = Double.valueOf(getBinding().initBT.getText().toString().trim());
            parm.distSensorCT = Double.valueOf(getBinding().initCT.getText().toString().trim());
            parm.distSensorDT = Double.valueOf(getBinding().initDT.getText().toString().trim());
            parm.leftDistSensorKL = Double.valueOf(getBinding().initKL.getText().toString().trim());
            parm.rightDistSensorKR = Double.valueOf(getBinding().initKR.getText().toString().trim());
            parm.angleSensorXT = Double.valueOf(getBinding().initXT.getText().toString().trim());
            parm.angleSensorYT = Double.valueOf(getBinding().initYT.getText().toString().trim());
            parm.angleSensorKX = Double.valueOf(getBinding().initKX.getText().toString().trim());
            parm.angleSensorKY = Double.valueOf(getBinding().initKY.getText().toString().trim());
            parm.temperature = Double.valueOf(getBinding().initTemp.getText().toString().trim());
        } catch (NumberFormatException e) {
            showMessage(e.getMessage());
            return;
        }

        DbUtils.edit(BaseReferInfo.class, parm);
        showMessage("保存成功");
        obtDbInfo();
    }

    @Override
    protected String getTitleMsg() {
        return "小车参数标定";
    }
}