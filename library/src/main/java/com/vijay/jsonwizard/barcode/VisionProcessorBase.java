/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vijay.jsonwizard.barcode;

import android.graphics.Bitmap;
import androidx.annotation.GuardedBy;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.mlkit.vision.common.InputImage;
import com.vijay.jsonwizard.barcode.common.BitmapUtils;
import com.vijay.jsonwizard.barcode.common.CameraImageGraphic;
import com.vijay.jsonwizard.barcode.common.FrameMetadata;
import com.vijay.jsonwizard.barcode.common.GraphicOverlay;
import com.vijay.jsonwizard.barcode.common.VisionImageProcessor;

import java.nio.ByteBuffer;

/**
 * Abstract base class for vision frame processors. Subclasses need to implement {@link
 * #onSuccess(Object, GraphicOverlay)} to define what they want to with the detection results and
 * {@link #detectInImage(InputImage)} to specify the detector object.
 *
 * @param <T> The type of the detected feature.
 */
public abstract class VisionProcessorBase<T> implements VisionImageProcessor {

    protected static final String MANUAL_TESTING_LOG = "LogTagForTest";
    private static final String TAG = "VisionProcessorBase";

    private final ScopedExecutor executor;

    // Whether this processor is already shut down
    private boolean isShutdown;

    // To keep the latest images and its metadata.
    @GuardedBy("this")
    private ByteBuffer latestImage;

    @GuardedBy("this")
    private FrameMetadata latestImageMetaData;
    // To keep the images and metadata in process.
    @GuardedBy("this")
    private ByteBuffer processingImage;

    @GuardedBy("this")
    private FrameMetadata processingMetaData;

    protected VisionProcessorBase() {
        executor = new ScopedExecutor(TaskExecutors.MAIN_THREAD);
    }

    // -----------------Code for processing single still image----------------------------------------
    @Override
    public void processBitmap(Bitmap bitmap, final GraphicOverlay graphicOverlay) {
        requestDetectInImage(
                InputImage.fromBitmap(bitmap, 0),
                graphicOverlay,
                /* originalCameraImage= */ null
                /* shouldShowFps= */);
    }

    // -----------------Code for processing live preview frame from Camera1 API-----------------------
    @Override
    public synchronized void processByteBuffer(
            ByteBuffer data, final FrameMetadata frameMetadata, final GraphicOverlay graphicOverlay) {
        latestImage = data;
        latestImageMetaData = frameMetadata;
        if (processingImage == null && processingMetaData == null) {
            processLatestImage(graphicOverlay);
        }
    }

    private synchronized void processLatestImage(final GraphicOverlay graphicOverlay) {
        processingImage = latestImage;
        processingMetaData = latestImageMetaData;
        latestImage = null;
        latestImageMetaData = null;
        if (processingImage != null && processingMetaData != null && !isShutdown) {
            processImage(processingImage, processingMetaData, graphicOverlay);
        }
    }

    private void processImage(
            ByteBuffer data, final FrameMetadata frameMetadata, final GraphicOverlay graphicOverlay) {
        // If live viewport is on (that is the underneath surface view takes care of the camera preview
        // drawing), skip the unnecessary bitmap creation that used for the manual preview drawing.
        Bitmap bitmap = BitmapUtils.getBitmap(data, frameMetadata);

        requestDetectInImage(
                InputImage.fromByteBuffer(
                        data,
                        frameMetadata.getWidth(),
                        frameMetadata.getHeight(),
                        frameMetadata.getRotation(),
                        InputImage.IMAGE_FORMAT_NV21),
                graphicOverlay,
                bitmap
                /* shouldShowFps= */)
                .addOnSuccessListener(executor, new OnSuccessListener<T>() {
                    @Override
                    public void onSuccess(T results) {
                        VisionProcessorBase.this.processLatestImage(graphicOverlay);
                    }
                });
    }

    // -----------------Common processing logic-------------------------------------------------------
    private Task<T> requestDetectInImage(
            final InputImage image,
            final GraphicOverlay graphicOverlay,
            @Nullable final Bitmap originalCameraImage) {
        return detectInImage(image)
                .addOnSuccessListener(
                        executor,
                        new OnSuccessListener<T>() {
                            @Override
                            public void onSuccess(T results) {

                                graphicOverlay.clear();
                                if (originalCameraImage != null) {
                                    graphicOverlay.add(new CameraImageGraphic(graphicOverlay, originalCameraImage));
                                }

                                VisionProcessorBase.this.onSuccess(results, graphicOverlay);
                                graphicOverlay.postInvalidate();
                            }
                        })
                .addOnFailureListener(
                        executor,
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                graphicOverlay.clear();
                                graphicOverlay.postInvalidate();
                                String error = "Failed to process. Error: " + e.getLocalizedMessage();
                                Toast.makeText(
                                        graphicOverlay.getContext(),
                                        error + "\nCause: " + e.getCause(),
                                        Toast.LENGTH_SHORT)
                                        .show();
                                Log.d(TAG, error);
                                e.printStackTrace();
                                VisionProcessorBase.this.onFailure(e);
                            }
                        });
    }

    @Override
    public void stop() {
        executor.shutdown();
        isShutdown = true;
    }

    protected abstract Task<T> detectInImage(InputImage image);

    protected abstract void onSuccess(@NonNull T results, @NonNull GraphicOverlay graphicOverlay);

    protected abstract void onFailure(@NonNull Exception e);
}
