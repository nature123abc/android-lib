package com.nature.demo.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;

import com.blankj.utilcode.util.ObjectUtils;
import com.dk.common.DateUtils;
import com.dk.lib_dk.utils.socket.TcpSocket;
import com.dk.lib_dk.view.comm.BTcpActivity;
import com.nature.demo.R;
import com.nature.demo.databinding.ActivitySocketBinding;
import com.xuexiang.xui.widget.edittext.MultiLineEditText;
import com.xuexiang.xui.widget.spinner.editspinner.EditSpinner;
import com.xuexiang.xui.widget.spinner.editspinner.EditSpinnerAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

public class SocketActivity extends BTcpActivity<ActivitySocketBinding> {
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

    CheckBox cbReat;

    EditText edtTime;

    String cmd = "";
    Disposable disposable;
    Set<String> setIp;
    Set<String> setPort;
    Set<String> setCmd;
    TcpSocket tcpSocket;
    Observable<byte[]> listenR;
    ExecutorService executor = Executors.newSingleThreadExecutor();


    public void clickMethod(View view) {
        switch (view.getId()) {
            case R.id.btnSend:  //
                sendEvent();
                break;
            case R.id.btnConn:  //
                connEvent();
                break;
            case R.id.btnClear:  //
                clearEvent();
                break;
        }
    }

    private void clearEvent() {
        metRx.setContentText("");
    }

    private void connEvent() {
        String ip = msIp.getText().trim();
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
            connect(ip, finalPort);
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
        if (cbReat.isChecked()) {//自动重新发送
            int time = Integer.valueOf(edtTime.getText().toString().trim());
            executor.execute(() -> {
                while (cbReat.isChecked()) {
                    try {
                        Thread.sleep(time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    sendCmd();
                }
            });
        } else {
            sendCmd();
        }
    }

    private void sendCmd() {
        sendTcpCmd(rbtHex.isChecked(), cmd, true, data -> {
            showRx(data);
        }, throwable -> {
            handelError(throwable);
        });
    }


    private void showRx(String str) {
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
        rbtHex = getBinding().rbtHex;
        rbrHex = getBinding().rbrHex;
        cbAutoEnt = getBinding().cbAutoEnt;
        cbShowTime = getBinding().cbShowTime;
        cbShowTx = getBinding().cbShowTx;
        cbReat = getBinding().cbReat;
        edtTime = getBinding().edtTime;
    }

    private Set<String> obtCmd() {
        Set<String> sets = new HashSet<>();
        sets.add("01 04 00 00 00 02 71 CB");
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
        return sets;
    }

    private Set<String> obtPort() {
        Set<String> sets = new HashSet<>();
        sets.add("8888");
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