package com.vijay.jsonwizard.demo.resources;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class DataFolderResourceResolver implements ResourceResolver{

    @Override
    public String resolvePath(Context context, String id) {
        File f = new File(context.getFilesDir().getAbsolutePath() + File.separator + "binary/" +  id);
        return f.exists() && f.isFile() ? f.getAbsolutePath() : null;
    }
}
