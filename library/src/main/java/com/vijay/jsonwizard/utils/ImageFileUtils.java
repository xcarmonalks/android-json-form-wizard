package com.vijay.jsonwizard.utils;


import static android.os.Environment.getExternalStorageDirectory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Base64;
import android.util.Log;

/**
 * Created by x.carmona on 18/01/17.
 */
public class ImageFileUtils {

    private static final String TAG = "ImageFileUtils";

    private static final int IMAGE_MAX_SIZE = 1600;
    private static final int IMAGE_COMPRESSION_RATIO = 80;

    public static String processImageFromFile(String filePath) {
        Bitmap tempImage;
        String content = null;
        Log.d(TAG, "Processing " + filePath);
        File imagefile = new File(filePath);
        try (FileInputStream fis = new FileInputStream(imagefile);) {
            tempImage = BitmapFactory.decodeStream(fis);
            // Process image
            Log.d(TAG, "Process scale to fit");
            tempImage = scaleToFit(tempImage, IMAGE_MAX_SIZE);
            Log.d(TAG, "Process compression");
            content = compressToBase64(tempImage, IMAGE_COMPRESSION_RATIO);
        } catch (Exception e) {
            Log.e(TAG, "Error processing image from file", e);
        }
        return content;
    }


    public static String compressToBase64(Bitmap uncompressedBitmap, int compressRatio) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        uncompressedBitmap.compress(CompressFormat.JPEG, compressRatio, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }

    public static Bitmap scaleToFit(Bitmap unscaledBitmap, int maxSize) {

        Bitmap scaledBitmap = null;
        // Only scale if necessary
        if ((unscaledBitmap.getWidth() > maxSize) || (unscaledBitmap.getHeight() > maxSize)) {

            Rect srcRect = calculateSrcRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(),
                    maxSize, maxSize,
                    ScalingLogic.FIT);
            Rect destRect = calculateDestRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(),
                    maxSize, maxSize,
                    ScalingLogic.FIT);
            scaledBitmap = Bitmap
                    .createBitmap(destRect.width(), destRect.height(), Config.ARGB_8888);
            Canvas canvas = new Canvas(scaledBitmap);
            canvas.drawBitmap(unscaledBitmap, srcRect, destRect,
                    new Paint(Paint.FILTER_BITMAP_FLAG));
        } else {
            scaledBitmap = unscaledBitmap;
        }
        return scaledBitmap;
    }

    public static Rect calculateSrcRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight,
            ScalingLogic scalingLogic) {
        if (scalingLogic == ScalingLogic.CROP) {
            final float srcAspect = (float) srcWidth / (float) srcHeight;
            final float dstAspect = (float) dstWidth / (float) dstHeight;

            if (srcAspect > dstAspect) {
                final int srcRectWidth = (int) (srcHeight * dstAspect);
                final int srcRectLeft = (srcWidth - srcRectWidth) / 2;
                return new Rect(srcRectLeft, 0, srcRectLeft + srcRectWidth, srcHeight);
            } else {
                final int srcRectHeight = (int) (srcWidth / dstAspect);
                final int scrRectTop = (int) (srcHeight - srcRectHeight) / 2;
                return new Rect(0, scrRectTop, srcWidth, scrRectTop + srcRectHeight);
            }
        } else {
            return new Rect(0, 0, srcWidth, srcHeight);
        }
    }

    public static Rect calculateDestRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight,
            ScalingLogic logic) {
        if (logic == ScalingLogic.FIT) {
            final float srcAspect = (float) srcWidth / (float) srcHeight;
            final float dstAspect = (float) dstWidth / (float) dstHeight;

            if (srcAspect > dstAspect) {
                return new Rect(0, 0, dstWidth, (int) (dstWidth / srcAspect));
            } else {
                return new Rect(0, 0, (int) (dstHeight * srcAspect), dstHeight);
            }
        } else {
            return new Rect(0, 0, dstWidth, dstHeight);
        }
    }

    public static Bitmap generateThumbNail(String path) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        bitmap = ImageFileUtils.scaleToFit(bitmap, 200);
        return bitmap;
    }

    public static File writeToFile(byte[] array, String imageName) {
        String path = getExternalStorageDirectory() + "/temp/";
        try {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdir();
            }

            File file = new File(dir, imageName);
            if (!file.exists() && file.createNewFile()) {
                Log.d(TAG, "New file created " + file.getAbsolutePath());
            }

            try (FileOutputStream stream = new FileOutputStream(path + imageName)) {
                stream.write(array);
            } catch (IOException e) {
                Log.e(TAG, "Error writing to file", e);
            }

            return file;
        } catch (Exception e) {
            Log.e(TAG, "Error creating file " + path + imageName, e);
            return null;
        }
    }

    public static boolean deleteTemporalDir(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files == null) {
                return true;
            }
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteTemporalDir(files[i]);
                } else {
                    if (files[i].delete()) {
                        Log.d(TAG, "Deleted file " + files[i].getAbsolutePath());
                    }
                }
            }
        }
        return (path.delete());
    }

    public static enum ScalingLogic {
        CROP, FIT
    }
}