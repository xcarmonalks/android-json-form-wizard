package com.vijay.jsonwizard.demo.resources;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MixedStrategyResourceResolver implements ResourceResolver{

    private static final String TAG = "MixedResourceResolver";

    @Override
    public String resolvePath(Context context, String id) {
        String path = null;
        File f = new File(context.getFilesDir().getAbsolutePath() + File.separator + "binary/" +  id);
        path = f.exists() && f.isFile() ? f.getAbsolutePath() : null;
        if(TextUtils.isEmpty(path)){
            path = moveAssetToCache(context, id, "imagenes");
        }
        return path;
    }

    private static String moveAssetToCache(Context context, String assetName,
                                           String assetFolderName) {
        File f = new File(context.getCacheDir() + File.separator + assetName);
        if (!f.exists()) {
            try (InputStream is = context.getAssets()
                    .open(assetFolderName + File.separator + assetName);
                 FileOutputStream fos = new FileOutputStream(f);) {

                byte[] buffer = new byte[1024];
                int length;

                while ((length = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }
                fos.flush();

            } catch (Exception e) {
                Log.e(TAG, "moveAssetToCache: Error moving asset " + assetFolderName + " to cache",
                        e);
                return null;
            }
        }
        return f.exists() && f.isFile() ? f.getAbsolutePath() : null;
    }

}
