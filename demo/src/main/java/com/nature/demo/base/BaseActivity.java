package com.nature.demo.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.dk.lib_dk.view.base.BindingActivity;
import com.nature.demo.R;
import com.xuexiang.xui.widget.actionbar.TitleBar;

public class BaseActivity<V extends ViewBinding> extends BindingActivity<V> {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (this.findViewById(R.id.titlebar) != null) {
            TitleBar titleBar = this.findViewById(R.id.titlebar);
            titleBar.getLeftText().setOnClickListener(v -> {
                this.onBackPressed();
            });
        }
    }

    @Override
    protected String getTitleMsg() {
        return "";
    }


}
