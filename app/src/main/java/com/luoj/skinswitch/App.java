package com.luoj.skinswitch;

import android.app.Application;
import android.os.Environment;

import com.luoj.lib_skin_loader.SkinLoader;

import java.io.File;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SkinLoader.getInstance().setContext(this);
        SkinLoader.getInstance().loadSkinFile(Environment.getExternalStorageDirectory() + File.separator + "skin.apk");
    }

}
