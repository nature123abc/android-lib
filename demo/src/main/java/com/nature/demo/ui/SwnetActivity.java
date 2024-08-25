package com.nature.demo.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.dk.common.Data2Byte;
import com.dk.lib_dk.view.comm.BTcpActivity;
import com.nature.demo.R;
import com.nature.demo.base.BSwActivity;
import com.nature.demo.databinding.ActivitySocketBinding;
import com.nature.demo.databinding.ActivitySwnetBinding;

public class SwnetActivity extends BSwActivity<ActivitySwnetBinding> {

    @Override
    public void serviceConnected() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBinding().btnBlue.setOnClickListener(view -> {
            showLoading();
            String sn = getBinding().edtSn.getText().toString().trim();
            sn = "./setsn " + sn;
            handleSingeEvent(binder.sendCmd2ReceiveSW(Data2Byte.assiiStr2Bytes(sn)), data -> {
                cancleLoading();
                showMessage(Data2Byte.bytes2AssiiStr(data));
            });
        });
    }


    @Override
    protected String getTitleMsg() {
        return "网络";
    }
}