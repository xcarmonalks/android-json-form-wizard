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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.annotation.KeepName;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.barcode.barcodescanning.BarcodeGraphic;
import com.vijay.jsonwizard.barcode.barcodescanning.BarcodeScannerProcessor;
import com.vijay.jsonwizard.barcode.common.CameraImageGraphic;
import com.vijay.jsonwizard.barcode.common.CameraSource;
import com.vijay.jsonwizard.barcode.common.CameraSourcePreview;
import com.vijay.jsonwizard.barcode.common.GraphicOverlay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Live preview demo for ML Kit APIs. */
@KeepName
public final class LivePreviewActivity extends AppCompatActivity
        implements OnRequestPermissionsResultCallback,
        CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private static final String TAG = "LivePreviewActivity";
    private static final int PERMISSION_REQUESTS = 1;
    private static final String PARAM_BARCODE = "barcode";

    private CameraSource cameraSource = null;
    private CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;
    private BarcodeScannerProcessor barcodeScannerProcessor;
    private TextView editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_live_preview);

        preview = findViewById(R.id.preview_view);
        if (preview == null) {
            Log.d(TAG, "Preview is null");
        }
        graphicOverlay = findViewById(R.id.graphic_overlay);
        if (graphicOverlay == null) {
            Log.d(TAG, "graphicOverlay is null");
        }

        ToggleButton facingSwitch = findViewById(R.id.facing_switch);
        facingSwitch.setOnCheckedChangeListener(this);

        final Button submit = findViewById(R.id.submit);
        submit.setOnClickListener(this);

        editText = findViewById(R.id.selectedCode);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                submit.setEnabled(true);
            }
        });
        if (allPermissionsGranted()) {
            createCameraSource();
        } else {
            getRuntimePermissions();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(TAG, "Set facing");
        if (cameraSource != null) {
            if (isChecked) {
                cameraSource.setFacing(CameraSource.CAMERA_FACING_FRONT);
            } else {
                cameraSource.setFacing(CameraSource.CAMERA_FACING_BACK);
            }
        }
        preview.stop();
        startCameraSource();
    }

    private void createCameraSource() {
        // If there's no existing cameraSource, create one.
        if (cameraSource == null) {
            cameraSource = new CameraSource(this, graphicOverlay);
        }

        try {
            Log.i(TAG, "Using Barcode Detector Processor");
            barcodeScannerProcessor = new BarcodeScannerProcessor();
            cameraSource.setMachineLearningFrameProcessor(barcodeScannerProcessor);
        } catch (Exception e) {
            Log.e(TAG, "Can not create image processor: ", e);
            Toast.makeText(getApplicationContext(), "Can not create image processor: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() {
        if (cameraSource != null) {
            try {
                if (preview == null) {
                    Log.d(TAG, "resume: Preview is null");
                }
                if (graphicOverlay == null) {
                    Log.d(TAG, "resume: graphOverlay is null");
                }
                preview.start(cameraSource, graphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        createCameraSource();
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        preview.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
        }
    }

    private String[] getRequiredPermissions() {
        try {
            PackageInfo info =
                    this.getPackageManager()
                            .getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (Exception e) {
            return new String[0];
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                return false;
            }
        }
        return true;
    }

    private void getRuntimePermissions() {
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                allNeededPermissions.add(permission);
            }
        }

        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Log.d(TAG, "dispatchTouchEvent: event");
            float x = event.getRawX();
            float y = event.getRawY();

            int[] coordinates = new int[2];
            graphicOverlay.getLocationOnScreen(coordinates);

            x -= coordinates[0];
            y -= coordinates[1];

            BarcodeGraphic graph = getGraphAtPos(graphicOverlay, x, y);
            if (graph != null) {
                CameraImageGraphic lastImage = barcodeScannerProcessor.getLastImage();
                cameraSource.stop();
                String barcodeValue = graph.getBarcode().getDisplayValue();
                Log.d(TAG, "dispatchTouchEvent: Selected = " + barcodeValue);
                graphicOverlay.highlight(graph);
                refreshOverlay(graphicOverlay, lastImage);
                editText.setText(barcodeValue);
            }

        }
        return super.dispatchTouchEvent(event);
    }

    private BarcodeGraphic getGraphAtPos(GraphicOverlay overlay, float x, float y) {
        BarcodeGraphic selected = null;
        for (GraphicOverlay.Graphic graph : overlay.getGraphics()) {
            if (graph instanceof BarcodeGraphic) {
                BarcodeGraphic barcodeGraphic = (BarcodeGraphic) graph;

                if (barcodeGraphic.contains(x, y)) {
                    Log.d("GraphicOverlay", "getGraphAtPos: winner!");
                    selected = barcodeGraphic;
                }
            }
        }
        return selected;
    }

    private void refreshOverlay(GraphicOverlay overlay, CameraImageGraphic cameraImage) {
        List<GraphicOverlay.Graphic> graphics = new ArrayList<>(overlay.getGraphics());
        overlay.clear();

        for (GraphicOverlay.Graphic graphic : graphics) {
            overlay.add(graphic);
        }
        overlay.postInvalidate();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        Log.i(TAG, "Permission granted!");
        if (allPermissionsGranted()) {
            createCameraSource();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private static boolean isPermissionGranted(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission granted: " + permission);
            return true;
        }
        Log.i(TAG, "Permission NOT granted: " + permission);
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v != null && v.getId() == R.id.submit) {
            Log.d(TAG, "onClick: Submit!!!");
            Intent result = new Intent();
            result.putExtra(PARAM_BARCODE, editText.getText().toString());
            setResult(Activity.RESULT_OK, result);
            finish();
        }
    }

}

