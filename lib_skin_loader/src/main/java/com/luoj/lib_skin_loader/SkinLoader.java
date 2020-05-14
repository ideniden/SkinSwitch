package com.luoj.lib_skin_loader;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import java.lang.reflect.Method;

public class SkinLoader {

    private static SkinLoader mInstancfe = new SkinLoader();
    private Context context;
    private Resources resources;
    private String packageName;

    private SkinLoader() {
    }

    public static SkinLoader getInstance() {
        return mInstancfe;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void loadSkinFile(String filePath) {
        PackageInfo packageInfo = context.getPackageManager().getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
        packageName = packageInfo.packageName;
        AssetManager assetManager = null;
        try {
            assetManager = AssetManager.class.newInstance();

            Method addAssetPath = assetManager.getClass().getDeclaredMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, filePath);

            resources = new Resources(assetManager,
                    context.getResources().getDisplayMetrics(),
                    context.getResources().getConfiguration());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isSkinResExist() {
        return resources != null;
    }

    /**
     * 从皮肤包中查找对应资源id
     *
     * @param resId 当前应用资源id
     * @return 皮肤包中资源id，如果存在的话
     */
    public int getSkinResId(int resId) {
        if (!isSkinResExist()) {
            return resId;
        }

        String resourceTypeName = context.getResources().getResourceTypeName(resId);
        String resourceEntryName = context.getResources().getResourceEntryName(resId);

        int identifier = resources.getIdentifier(resourceEntryName, resourceTypeName, packageName);

        if (identifier == 0) {
            return resId;
        }

        return identifier;
    }

    public int getColor(int resId) {
        return resources.getColor(getSkinResId(resId));
    }

    public int getDrawableId(int resId) {
        return getSkinResId(resId);
    }

    public Drawable getDrawable(int resId) {
        if (!isSkinResExist()) {
            return ContextCompat.getDrawable(context, resId);
        }

        String resourceTypeName = context.getResources().getResourceTypeName(resId);
        String resourceEntryName = context.getResources().getResourceEntryName(resId);

        int identifier = resources.getIdentifier(resourceEntryName, resourceTypeName, packageName);

        if (identifier == 0) {
            return ContextCompat.getDrawable(context, resId);
        }

        return resources.getDrawable(identifier);
    }

}
