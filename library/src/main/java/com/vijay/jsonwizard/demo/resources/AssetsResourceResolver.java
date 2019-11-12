package com.vijay.jsonwizard.demo.resources;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class AssetsResourceResolver implements ResourceResolver {

    private static final String TAG = "AssetsResourceResolver";

    private static String moveAssetToCache(Context context, String assetName, String assetFolderName) {

        File f = new File(context.getCacheDir() + File.separator + assetName);
        if (!f.exists()) {
            try (InputStream is = context.getAssets().open(assetFolderName + File.separator + assetName);
                 FileOutputStream fos = new FileOutputStream(f);) {

                byte[] buffer = new byte[1024];
                int length;

                while ((length = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }
                fos.flush();

            } catch (Exception e) {
                Log.e(TAG, "moveAssetToCache: Error moving asset " + assetFolderName + " to cache", e);
                return null;
            }
        }
        return f.getAbsolutePath();
    }

    @Override
    public String resolvePath(Context context, String id) {
        String imagePath = id;
        if (!TextUtils.isEmpty(imagePath)) {
            imagePath = moveAssetToCache(context, imagePath, "imagenes");
        }
        return imagePath;
    }
}
