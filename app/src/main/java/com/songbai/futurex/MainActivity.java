package com.songbai.futurex;

import android.os.Bundle;

import com.songbai.futurex.activity.BaseActivity;

import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }
}
