package com.luoj.skinswitch;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.view.LayoutInflaterCompat;

public class BaseActivity extends Activity {

    protected SkinFactory factory2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        factory2 = new SkinFactory();
        LayoutInflaterCompat.setFactory2(getLayoutInflater(),factory2);
    }

    public void apply(){
        factory2.apply();
    }

}
