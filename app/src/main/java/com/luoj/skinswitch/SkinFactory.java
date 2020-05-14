package com.luoj.skinswitch;


import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.luoj.lib_skin_loader.SkinLoader;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SkinFactory implements LayoutInflater.Factory2 {

    private static final String TAG = "SkinFactory";
    String[] ANDROID_VIEW_PKG_NAME = {
            "android.widget.",
            "android.view.",
            ""
    };

    List<String> supportAttrNameList = Arrays.asList(new String[]{
            "background"
    });

    ArrayList<SkinView> skinViews = new ArrayList<>();

    public void apply() {
//        Log.d(TAG, "apply: " + skinViews.size());
        for (SkinView v : skinViews) {
            v.apply();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable View view, @NonNull String s, @NonNull Context context, @NonNull AttributeSet attributeSet) {
        Log.d(TAG, "onCreateView -> " + s);

        View v = null;

        if (s.contains(".")) {
            v = onCreateView(s, context, attributeSet);
        } else {

            for (String n : ANDROID_VIEW_PKG_NAME) {
                v = onCreateView(n + s, context, attributeSet);
                if (null != v) break;
            }

        }

        //缓存需要换肤的view和属性
        SkinView skinView = findSkinView(v, attributeSet);
        if (null != skinView) {
            skinViews.add(skinView);
        }

        return v;
    }

    private SkinView findSkinView(View v, AttributeSet attributeSet) {
        SkinView skinView = null;
        ArrayList<SkinAttrInfo> skinAttrInfos = null;

        //根据自定义标记确定是否为换肤view
        boolean isSkinView = false;
        for (int i = 0; i < attributeSet.getAttributeCount(); i++) {

            String attrValue = attributeSet.getAttributeValue(i);
            String attrName = attributeSet.getAttributeName(i);

            Log.d(TAG, String.format("i->%d  , name->%s , value->%s", i, attrName, attrValue));
            if (TextUtils.equals(attrName, "is_skin_view") && TextUtils.equals(attrValue, "true")) {
                Log.d(TAG, "found skin view.");
                isSkinView = true;
            }
        }

        //查找支持的换肤属性
        if (isSkinView) {
            for (int i = 0; i < attributeSet.getAttributeCount(); i++) {

                String attrValue = attributeSet.getAttributeValue(i);
                String attrName = attributeSet.getAttributeName(i);

                if (supportAttrNameList.contains(attrName)) {
                    Log.d(TAG, "found support attr -> " + attrName);
                    if (null == skinAttrInfos) {
                        skinAttrInfos = new ArrayList<>();
                    }

                    int resId = Integer.parseInt(attrValue.substring(1));
                    String typeName = v.getResources().getResourceTypeName(resId);
                    String entryName = v.getResources().getResourceEntryName(resId);
                    skinAttrInfos.add(new SkinAttrInfo(attrName, typeName, entryName, resId));
                }
            }
        }

        if (null != skinAttrInfos && !skinAttrInfos.isEmpty()) {
            skinView = new SkinView(v, skinAttrInfos);
        }

        return skinView;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String s, @NonNull Context context, @NonNull AttributeSet attributeSet) {
        Log.d(TAG, "create " + s);
        View v = null;
        try {
            Class<?> aClass = Class.forName(s);
            Constructor<?> constructor = aClass.getConstructor(Context.class, AttributeSet.class);
            v = (View) constructor.newInstance(context, attributeSet);
        } catch (Exception e) {
//            e.printStackTrace();
        }
        if (null != v) Log.d(TAG, "create success");
        return v;
    }

    class SkinAttrInfo {
        String attrName;
        String typeName;
        String entryName;
        int resId;

        public SkinAttrInfo(String attrName, String typeName, String entryName, int resId) {
            this.attrName = attrName;
            this.typeName = typeName;
            this.entryName = entryName;
            this.resId = resId;
        }
    }

    class SkinView {
        View view;
        List<SkinAttrInfo> attrInfoList;

        public SkinView(View view, List<SkinAttrInfo> attrInfoList) {
            this.view = view;
            this.attrInfoList = attrInfoList;
        }

        public void apply() {
            for (SkinAttrInfo info : attrInfoList) {
                if (TextUtils.equals(info.attrName, "background")) {
                    if (TextUtils.equals(info.typeName, "color")) {
                        view.setBackgroundColor(SkinLoader.getInstance().getColor(info.resId));
                    } else if (TextUtils.equals(info.typeName, "drawable") || TextUtils.equals(info.typeName, "mipmap")) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            view.setBackground(SkinLoader.getInstance().getDrawable(info.resId));
                        } else {
                            view.setBackgroundDrawable(SkinLoader.getInstance().getDrawable(info.resId));
                        }
                    }
                }
            }
        }
    }

}
