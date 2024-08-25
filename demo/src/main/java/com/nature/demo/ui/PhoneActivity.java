package com.nature.demo.ui;

import android.os.Bundle;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.dk.error.CommonException;
import com.dk.lib_dk.view.base.BindingActivity;
import com.nature.demo.base.Contents;
import com.nature.demo.databinding.ActivityPhoneBinding;

/**
 * @ProjectName: AndroidLib
 * @Desc:
 * @Author: hq
 * @Date: 2023/9/22
 */
public class PhoneActivity extends BindingActivity<ActivityPhoneBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBinding().sbPhone.setOnClickListener(v -> {
            String imei = DeviceUtils.getAndroidID();
            String mac = DeviceUtils.getUniqueDeviceId();
            String s = imei + "&" + mac;
            getBinding().edtInfo.setText(s);
            requestPermisionFile(data -> {
                if (data) {
                    FileUtils.createOrExistsDir(Contents.PATH_PARM);
                    FileIOUtils.writeFileFromString(Contents.Parm, s);
                    showMessage("手机信息保存至：" + Contents.Parm);
                } else {
                    handelError(new CommonException("打开文件读取权限"));
                }
            });

        });
    }

    @Override
    protected String getTitleMsg() {
        return "获取手机信息";
    }
}
