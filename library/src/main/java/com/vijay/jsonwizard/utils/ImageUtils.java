package com.vijay.jsonwizard.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import androidx.collection.LruCache;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by vijay.rawat01 on 7/29/15.
 */
public class ImageUtils {

    private static final String TAG = "ImageUtils";
    private static LruCache<String, Bitmap> mBitmapLruCache = new LruCache<>(10000000);

    public static Bitmap loadBitmapFromFile(String path, int requiredWidth, int requiredHeight) {
        String key = path + ":" + requiredWidth + ":" + requiredHeight;
        Bitmap bitmap = mBitmapLruCache.get(key);
        if (bitmap != null) {
            Log.d("ImagePickerFactory", "Found in cache.");
            return bitmap;
        }
        // First decode with inJustDecodeBounds=true to check dimensions
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, requiredWidth, requiredHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(path, options);
        mBitmapLruCache.put(key, bitmap);
        return bitmap;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static int getDeviceWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        return display.getWidth();
    }

    public static boolean saveToFile(Bitmap bitmap, File file) {
        final int COMPRESSION_RATIO = 80;
        try {
            // Create folder if doesn't exist
            File folder = new File(file.getParent());
            if (!folder.exists()) {
                folder.mkdirs();
            }

            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_RATIO, fos);
            fos.flush();
            fos.close();

            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error compressing bitmap", e);
        }
        return false;
    }

    public static boolean compressAndSave(Bitmap uncompressedBitmap, int compressionRatio, String filePath) {

        try {
            if (compressionRatio > 0 && compressionRatio <= 100) {
                // Create folder if doesn't exist
                File file = new File(filePath);
                File folder = new File(file.getParent());
                if (!folder.exists()) {
                    folder.mkdirs();
                }

                FileOutputStream fos = new FileOutputStream(file);
                uncompressedBitmap.compress(Bitmap.CompressFormat.JPEG, compressionRatio, fos);
                fos.flush();
                fos.close();

                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error compressing bitmap", e);
        }
        return false;
    }

    public static Bitmap scaleToFit(Bitmap unscaledBitmap, int maxSize) {

        Bitmap scaledBitmap = null;
        // Only scale if necessary
        if ((unscaledBitmap.getWidth() > maxSize) || (unscaledBitmap.getHeight() > maxSize)) {

            Rect srcRect = calculateSrcRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), maxSize, maxSize,
                ScalingLogic.FIT);
            Rect destRect = calculateDestRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), maxSize, maxSize,
                ScalingLogic.FIT);
            scaledBitmap = Bitmap.createBitmap(destRect.width(), destRect.height(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(scaledBitmap);
            canvas.drawBitmap(unscaledBitmap, srcRect, destRect, new Paint(Paint.FILTER_BITMAP_FLAG));
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

    public static Rect calculateDestRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight, ScalingLogic logic) {
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

    public static enum ScalingLogic {
        CROP, FIT
    }
}
