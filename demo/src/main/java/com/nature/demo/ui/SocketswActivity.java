package com.nature.demo.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;

import com.blankj.utilcode.util.ObjectUtils;
import com.dk.common.Data2Byte;
import com.dk.common.DateUtils;
import com.dk.lib_dk.view.comm.BTcpActivity;
import com.nature.demo.R;
import com.nature.demo.databinding.ActivitySocketBinding;
import com.nature.dk_android.service.bluetooth.sw.Command;
import com.xuexiang.xui.widget.edittext.MultiLineEditText;
import com.xuexiang.xui.widget.spinner.editspinner.EditSpinner;
import com.xuexiang.xui.widget.spinner.editspinner.EditSpinnerAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.rxjava3.disposables.Disposable;

public class SocketswActivity extends BTcpActivity<ActivitySocketBinding> {

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
        getBinding().btnConn.setOnClickListener(view -> {
            connEvent();
        });
        getBinding().btnSend.setOnClickListener(view -> {
            sendEvent();
        });
        getBinding().btnClear.setOnClickListener(view -> {
            clearEvent();
        });
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
            // connect(ip,finalPort);
        });
    }

    private void close() {
        //关闭监听
        if (null != disposable && disposable.isDisposed()) {
            disposable.dispose();
            disposable = null;
        }
    }

    private void sendEvent() {
        setAdapterData();
        cmd = metTx.getContentText();
        if (ObjectUtils.isEmpty(cmd)) {
            showMessage("发送命令不能为空");
            return;
        }
      /*  isConnect(data -> {
            if ((Boolean) data) {
                sendCmd();
            } else {
                showMessage("请先连接设备");
            }
        });*/
        sendCmd();
    }

    private void sendCmd() {
        showLoadingDialog();
        handleSingeEvent(binder.sendCmd2ReceiveSW(Data2Byte.assiiStr2Bytes(cmd)), data -> {
            cancleLoading();
            showRx(data);
        });
    }


    private void showRx(byte[] o) {
        String str = rbrHex.isChecked() ? Data2Byte.bytesToHexString(o) : Data2Byte.bytes2AssiiStr(o);
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

    private Set<String> obtCmd() {
        Set<String> cmds = new HashSet<>();
        cmds.add("01 04 00 00 00 02 71 CB");
        cmds.add("07 03 00 00 00 08 44 6A");
        cmds.add("@gdAA_ZSXPOKIMXNCBAWTKJDEQSHFIEICMECAKIU GINF#");
        cmds.add("cat sn");
        cmds.add("./com3s 4 1 SSS 0 1 30");
        cmds.add("./getstatic");

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
        return cmds;
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
        sets.add("192.168.2.11");
        sets.add("10.10.100.254");
        sets.add("127.0.0.1");
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