package com.nature.demo.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter4.BaseQuickAdapter;
import com.dk.common.DateUtils;
import com.dk.lib_dk.utils.tsp.obs.BaseObser;
import com.dk.lib_dk.view.adapter.learn.LearnAdapter;
import com.nature.demo.R;

public class TestActivity extends AppCompatActivity {
    RecyclerView rv1;
    BaseQuickAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        rv1 = findViewById(R.id.rv1);
        adapter = new LearnAdapter(true);
        adapter.addOnItemChildClickListener(com.dk.lib_dk.R.id.bvDelt, (adapter1, view1, position1) -> {

        });

        rv1.setLayoutManager(new LinearLayoutManager(this));
        rv1.setAdapter(adapter);


        BaseObser obser = new BaseObser();
        obser.pointName = "";
        obser.sd = 0.0;
        obser.hz = 0.0;
        obser.v = 0.0;
        obser.chNum = 0;
        obser.leftOright =1;
        obser.dateTime = DateUtils.getDate();
        adapter.add(obser);
    }
}