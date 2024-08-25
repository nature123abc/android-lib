package com.nature.demo.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CheckBox;
import android.widget.RadioButton;

import com.blankj.utilcode.util.ObjectUtils;
import com.dk.common.Data2Byte;
import com.dk.common.DateUtils;
import com.dk.lib_dk.utils.socket.TcpSocket;
import com.dk.lib_dk.view.comm.BTcpActivity;
import com.nature.demo.R;
import com.nature.demo.databinding.ActivitySocket2Binding;
import com.xuexiang.xui.widget.edittext.MultiLineEditText;
import com.xuexiang.xui.widget.spinner.editspinner.EditSpinner;
import com.xuexiang.xui.widget.spinner.editspinner.EditSpinnerAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.rxjava3.disposables.Disposable;

public class Socket2Activity extends BTcpActivity<ActivitySocket2Binding> {

    EditSpinner msIp;

    EditSpinner msPort;

    EditSpinner msCmd;

    MultiLineEditText metTx;

    MultiLineEditText metRx;

    RadioButton rbtHex;

    RadioButton rbrHex;

    CheckBox cbAutoEnt;

    CheckBox cbShowTime;

    CheckBox cbShowTx;
    String cmd = "";
    Disposable disposable;
    Set<String> setIp;
    Set<String> setPort;
    Set<String> setCmd;
    TcpSocket tcpSocket;




    private void connEvent() {
        ip = msIp.getText().trim();
        String strPort = msPort.getText().trim();
        if (ObjectUtils.isEmpty(ip) || ObjectUtils.isEmpty(strPort)) {
            showMessage("IP地址或端口不能为空");
            return;
        }
        int port = 0;
        try {
            port = Integer.valueOf(strPort);
        } catch (NumberFormatException e) {
            showMessage("端口有误");
            return;
        }
        showLoadingDialog();
        int finalPort = port;
        pingIp(ip, data1 -> {
            connect(ip, finalPort, data -> {
                cancleLoading();
                showMessage("连接成功");
                tcpSocket = (TcpSocket) binder.obtSocket();
                handleObserveEvent(binder.listenReceiveAll(null,null,null), data2 -> {
                    showMessage("返回" + Data2Byte.bytes2AssiiStr(data2));
                }, null, false);
            }, null);
        });
    }

    private void close() {
        //close( );
        //关闭监听
        if (null != disposable && disposable.isDisposed()) {
            disposable.dispose();
            disposable = null;
        }
    }

    private void sendEvent() {
        setAdapterData();
        cmd = metTx.getContentText();
        if (ObjectUtils.isEmpty(tcpSocket)) {
            showMessage("请先连接设备");
            return;
        }
        if (ObjectUtils.isEmpty(cmd)) {
            showMessage("发送命令不能为空");
            return;
        }
        sendCmd();
    }

    private void sendCmd() {
        showRx(false,cmd);
        byte[] temp;
        try {
            temp = getBinding().rbtHex1.isChecked()?Data2Byte.hexStringToBytes(cmd):Data2Byte.assiiStr2Bytes(cmd);
        } catch (Exception e) {
            showMessage("命令有误");
            return;
        }
        handleCompletableEvent(binder.sendCmd(temp), data -> {

        });
    }


    private void showRx(boolean isR,String str) {
        StringBuffer sb = new StringBuffer();
        String exit = metRx.getContentText();
        if (exit.length() < 1024) {
            sb.append(exit);
            sb.append("\r\n");
        }
        if (cbShowTx.isChecked()) {//显示发送
            sb.append("发送:");
            sb.append(cmd);
            sb.append(";");
            return;
        }
        if(!isR){
            return;
        }
        sb.append("接收:");
        sb.append(str);
        sb.append(";");

        if (cbShowTime.isChecked()) {
            sb.append(DateUtils.date2Str(new Date(), DateUtils.yyyyMMddHHmmssSSS.get()));
        }
        if (cbAutoEnt.isChecked()) {
            sb.append("\r\n");
        }
        metRx.setContentText(sb.toString());
    }

    private void setAdapterData() {
        String sIp = msIp.getText().trim();
        if (ObjectUtils.isNotEmpty(sIp)) {
            setIp.add(sIp);
        }
        String sPort = msIp.getText().trim();
        if (ObjectUtils.isNotEmpty(sPort)) {
            setIp.add(sPort);
        }
        String sCmd = metTx.getContentText().trim();
        if (ObjectUtils.isNotEmpty(sCmd)) {
            setCmd.add(sCmd);
        }
        setAdapter(msIp, new ArrayList<>() {{
            addAll(setIp);
        }});
        setAdapter(msPort, new ArrayList<>() {{
            addAll(setPort);
        }});
        setAdapter(msCmd, new ArrayList<>() {{
            addAll(setCmd);
        }});
        getBinding().rbtASCII1.setOnCheckedChangeListener((w,v)->{
            getBinding().rbtHex1.setChecked(!v);
        });
        getBinding().rbtHex1.setOnCheckedChangeListener((w,v)->{
            getBinding().rbtASCII1.setChecked(!v);
        });


        getBinding().rbrASCII2.setOnCheckedChangeListener((w,v)->{
            getBinding().rbrHex2.setChecked(!v);
        });
        getBinding().rbrHex2.setOnCheckedChangeListener((w,v)->{
            getBinding().rbrASCII2.setChecked(!v);
        });
    }

    @Override
    public void serviceConnected() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        setAdapter(msIp, new ArrayList<>() {{
            addAll(setIp = obtIp());
        }});
        setAdapter(msPort, new ArrayList<>() {{
            addAll(setPort = obtPort());
        }});
        setAdapter(msCmd, new ArrayList<>() {{
            addAll(setCmd = obtCmd());
        }});
        msCmd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                metTx.setContentText(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void initView() {
        msCmd = getBinding().msCmd;
        msIp = getBinding().msIp;
        msPort = getBinding().msPort;
        metTx = getBinding().metTx;
        metRx = getBinding().metRx;
        cbAutoEnt = getBinding().cbAutoEnt;
        cbShowTime = getBinding().cbShowTime;
        cbShowTx = getBinding().cbShowTx;

        getBinding().btnSend.setOnClickListener(view -> {
            sendEvent();
        });
        getBinding().btnConn.setOnClickListener(view -> {
            connEvent();
        });
        getBinding().btnClear.setOnClickListener(view -> {
            metRx.setContentText("");
        });
    }

    private Set<String> obtCmd() {
        Set<String> sets = new HashSet<>();
        sets.add("selectresult:0" + "\0");
        sets.add("07 03 00 00 00 08 44 6A");
        sets.add("07 03 00 00 00 08 44 6A");
        sets.add("%R1Q,5003:2\r\n");
        sets.add("%R1Q,9028:\r\n");
        sets.add("01040000000131CA");
        sets.add("010400020001900A");
        sets.add("$CMD,SET,OUTPUTRATE,COM1,000*FF");
        sets.add("@gdAA_ZSXPOKIMXNCBAWTKJDEQSHFIEICMECAKIU GINF#");
        return sets;
    }

    private void setAdapter(EditSpinner ms, List<String> toArray) {
        ms.setAdapter(new EditSpinnerAdapter(toArray)
                .setTextColor(getResources().getColor(R.color.black))
                .setTextSize(32)
                .setBackgroundSelector(R.drawable.selector_custom_spinner_bg)
        );
    }

    private Set<String> obtIp() {
        Set<String> sets = new HashSet<>();
        sets.add("10.10.100.254");
        sets.add("127.0.0.1");
        sets.add("192.168.2.11");
        sets.add("192.168.2.34");
        return sets;
    }

    private Set<String> obtPort() {
        Set<String> sets = new HashSet<>();
        sets.add("8000");
        return sets;
    }


    @Override
    protected String getTitleMsg() {
        return "";
    }

    @Override
    protected void onDestroy() {
        close();
        super.onDestroy();
    }
}