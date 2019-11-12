// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.vijay.jsonwizard.barcode.barcodescanning;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.vijay.jsonwizard.barcode.common.GraphicOverlay;
import com.vijay.jsonwizard.barcode.common.GraphicOverlay.Graphic;

/** Graphic instance for rendering Barcode position and content information in an overlay view. */
public class BarcodeGraphic extends Graphic {

    private static final int TEXT_COLOR = Color.WHITE;
    private static final float TEXT_SIZE = 54.0f;
    private static final float STROKE_WIDTH = 4.0f;

    private final Paint rectPaint;
    private final Paint barcodePaint;
    private final FirebaseVisionBarcode barcode;
    private RectF rect;
    private Rect textRect;

    BarcodeGraphic(GraphicOverlay overlay, FirebaseVisionBarcode barcode) {
        super(overlay);

        this.barcode = barcode;

        rectPaint = new Paint();
        rectPaint.setColor(TEXT_COLOR);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(STROKE_WIDTH);

        barcodePaint = new Paint();
        barcodePaint.setColor(TEXT_COLOR);
        barcodePaint.setTextSize(TEXT_SIZE);

        rect = new RectF(barcode.getBoundingBox());
        rect.left = translateX(rect.left);
        rect.top = translateY(rect.top);
        rect.right = translateX(rect.right);
        rect.bottom = translateY(rect.bottom);

        textRect = new Rect();
    }

    /**
     * Draws the barcode block annotations for position, size, and raw value on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        if (barcode == null) {
            throw new IllegalStateException("Attempting to draw a null barcode.");
        }

        // Draws the bounding box around the BarcodeBlock.

        canvas.drawRect(rect, rectPaint);

        // Renders the barcode at the bottom of the box.
        String text = barcode.getRawValue();

        //Text bounds to make them clickable too
        barcodePaint.getTextBounds(text, 0, text.length(), textRect);
        textRect.left += rect.left;
        textRect.right += rect.left;
        textRect.top += rect.bottom;
        textRect.bottom += rect.bottom;
        //To draw text bounds
        //canvas.drawRect(textRect, rectPaint);

        canvas.drawText(text, rect.left, rect.bottom, barcodePaint);

    }

    public void removeHighlight() {
        this.rectPaint.setColor(TEXT_COLOR);
        this.barcodePaint.setColor(TEXT_COLOR);
    }

    public void highlight() {
        this.rectPaint.setColor(Color.RED);
        this.barcodePaint.setColor(Color.RED);
    }

    public FirebaseVisionBarcode getBarcode() {
        return this.barcode;
    }

    public boolean contains(float x, float y) {
        return (rect.contains(x, y) || (textRect.contains((int) x, (int) y)));
    }
}
