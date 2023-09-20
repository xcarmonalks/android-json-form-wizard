package com.vijay.jsonwizard.activities;

import static com.vijay.jsonwizard.state.StateContract.COL_JSON;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.utils.ImageUtils;

import java.io.File;


public class SignatureActivity extends AppCompatActivity{

    private Signature mSignature;
    private Bitmap bitmap;
    private LinearLayout mContent;
    private RelativeLayout mysignatureContainer;
    private View view;
    private View deleteButton;
    private View saveButton;
    private Boolean isTimestampVisible;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getExtras();
        setupView();
        setupButtons();

    }

    private void getExtras() {
        Bundle extras = getIntent().getExtras();
        isTimestampVisible = extras.getBoolean("timestamp");
    }


    private void setupView() {
        setContentView(R.layout.activity_signature);
        mysignatureContainer = findViewById(R.id.mysignatureContainer);
        mContent = findViewById(R.id.mysignature);
        mSignature = new Signature(this, null);
        mContent.addView(mSignature);
        setTimestampVisibility();
        view = mysignatureContainer;
    }

    private void setTimestampVisibility() {
        View timestamp = findViewById(R.id.timestampTv);
        if(isTimestampVisible){
            timestamp.setVisibility(View.VISIBLE);
        }else{
            timestamp.setVisibility(View.GONE);
        }
    }

    private void setupButtons(){
        deleteButton = findViewById(R.id.cleanBt);
        deleteButton.setOnClickListener(v -> mSignature.clear());
        saveButton = findViewById(R.id.saveBt);
        saveButton.setOnClickListener(v ->{
            if(hasSigned()){
                saveImage();
            }else{
                Toast.makeText(this, getString(R.string.signature_empty), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void saveImage() {
        String fileName = String.valueOf(System.currentTimeMillis());
        File file = new File(getExternalCacheDir(), fileName + ".jpg");
        bitmap = Bitmap.createBitmap(mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);
        final Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        bitmap = ImageUtils.scaleToFit(bitmap, 1600);
        //ImageUtils.compressAndSave(bitmap, 80, file.getAbsolutePath());
        ImageUtils.saveToFile(bitmap, file);
        Intent intent = new Intent();
        intent.putExtra("signatureFileName",fileName);
        setResult(100,intent);
        finish();
    }

    private boolean hasSigned() {
        return mSignature.lastTouchX != 0.0f && mSignature.lastTouchY != 0.0f;
    }




/*

   /* @Click(R.id.cleanBt)
    public void onCleanBtClicked() {

    }

    @Click(R.id.saveBt)
    public void onSaveBtClicked() {
        String formJson = getIntent().getStringExtra("json");
        if (formJson == null && getIntent().getParcelableExtra("jsonUri") != null) {
            Uri jsonUri = getIntent().getParcelableExtra("jsonUri");
            try (Cursor c = getContentResolver().query(jsonUri, null, null, null, null)) {
                if (c != null && c.moveToFirst() && c.getCount()>0) {
                    formJson = c.getString(c.getColumnIndex(COL_JSON));
                }
            } catch (Exception e) {
                Log.e("Test", "Could not resolve JsonForm URI: " + jsonUri, e);
            }
        }


        Intent intent = new Intent();
        intent.putExtra("json",formJson);
        setResult(RESULT_OK,intent);
        finish();
        *//*if ((mSignature.lastTouchX != 0.0f && mSignature.lastTouchY != 0.0f) || refuseSign) {
            String storedPath = directory + picName;
            final Intent intent = new Intent();
            boolean incorrectData=false;
            switch (origin) {
                case ORIGIN_CALLOUT:
                case ORIGIN_PENDING_SIGNATURE:
                    if (refuseSign) {
                        storedPath = null;
                    }
                    saveIntoCallout(storedPath, origin);
                    break;
                case ORIGIN_TECHNICIAN_SIGNATURE:
                    mSignature.save(view, storedPath);
                    intent.putExtra("PicName", picName);
                    break;
                case ORIGIN_FORM_SIGNATURE:
                    saveIntoForm(storedPath, origin);
                    break;
            }
            if(!incorrectData) {
                intent.putExtra("Path", storedPath);
                setResult(RESULT_OK, intent);
                finish();
            }
        } else {
            Toast.makeText(this, getString(R.string.signatureEmpty), Toast.LENGTH_LONG).show();
        }*//*
    }*/




    private void deleteOldSignature(String path) {
        File deletedPicture = new File(path);
        deletedPicture.delete();
    }

/*    private void saveIntoForm(String storedPath, int origin) {

        FormInstance formInstance = aviso.getFormInstances().get(formId);

        //Mirar si tiene formInstancesLocal y si no crearlo
        Map<String, FormInstanceLocal> formInstancesLocal;
        if (aviso.getFormInstancesLocal() != null) {
            formInstancesLocal = aviso.getFormInstancesLocal();
        } else {
            formInstancesLocal = new HashMap<String, FormInstanceLocal>();
            aviso.setFormInstancesLocal(formInstancesLocal);
        }

        //Buscar dentro de formInstancesLocal si hay algo del formulario que estamos modificando
        FormInstanceLocal formInstanceLocal = formInstancesLocal.get(formId);
        if (formInstanceLocal == null) {
            formInstanceLocal = new FormInstanceLocal(formInstance);
        }
        List<AdjuntoLocal> adjuntosLocalesForm = new ArrayList<>();
        if (formInstanceLocal.getAdjuntosLocales() != null) {
            adjuntosLocalesForm = formInstanceLocal.getAdjuntosLocales();
        }


        //Buscar entre los adjuntosLocales la firma para borrarla en caso de que esté
        for (int i = 0; i < adjuntosLocalesForm.size(); i++) {
            if (SIGNATURE_TYPE_CUS.equals(adjuntosLocalesForm.get(i).getFiletype())) {
                if (adjuntosLocalesForm.get(i).getPath() != null) {
                    deleteOldSignature(adjuntosLocalesForm.get(i).getPath());
                }
                adjuntosLocalesForm.remove(i);
            }
        }

        //Guardar el adjunto en el FormInstanceLocal y añadir el FormInstanceLocal actualizado al aviso
        if (storedPath != null) {
            if (mSignature.save(view, storedPath)) {
                final AdjuntoLocal adjunto = new AdjuntoLocal();
                adjunto.setTitulo(picName);
                adjunto.setPath(storedPath);
                adjunto.setFiletype(SIGNATURE_TYPE_CUS);
                adjuntosLocalesForm.add(adjunto);
                formInstanceLocal.setAdjuntosLocales(adjuntosLocalesForm);
                aviso.getFormInstancesLocal().put(formId, formInstanceLocal);
            } else {
                Toast.makeText(this, getString(R.string.signatureNotSaved), Toast.LENGTH_LONG).show();
            }
        }
        calloutPreferences.calloutJson().put(gson.toJson(aviso));
    }*/

/*    private void saveIntoCallout(String storedPath, int origin) {

        List<AdjuntoLocal> adjuntos = aviso.getListaLocalAdjuntos();
        if (adjuntos == null) {
            adjuntos = new ArrayList<>();
        }
        if (origin == ORIGIN_CALLOUT) {
            for (int i = 0; i < adjuntos.size(); i++) {
                AdjuntoLocal adjunto = adjuntos.get(i);
                if (SIGNATURE_TYPE.equals(adjuntos.get(i).getFiletype()) && !PicturePickUpScreens.CONTRACTSIGNATURE
                    .equals(adjunto.getPantallaRecogida())) {
                    if (adjuntos.get(i).getPath() != null) {
                        deleteOldSignature(adjuntos.get(i).getPath());
                    }
                    adjuntos.remove(i);
                }
            }
        }
        if (origin == ORIGIN_CONTRACT_SIGNATURE) {
            for (int i = 0; i < adjuntos.size(); i++) {
                AdjuntoLocal adjunto = adjuntos.get(i);
                if (SIGNATURE_TYPE.equals(adjunto.getFiletype()) && PicturePickUpScreens.CONTRACTSIGNATURE.equals(
                    adjunto.getPantallaRecogida())) {
                    if (adjuntos.get(i).getPath() != null) {
                        deleteOldSignature(adjuntos.get(i).getPath());
                    }
                    adjuntos.remove(i);
                }
            }
        }

        if (storedPath != null) {
            if (mSignature.save(view, storedPath)) {
                final AdjuntoLocal adjunto = new AdjuntoLocal();
                adjunto.setTitulo(picName);
                adjunto.setPath(storedPath);
                adjunto.setFiletype(SIGNATURE_TYPE);
                if (origin == ORIGIN_CALLOUT) {
                    adjunto.setPantallaRecogida(PicturePickUpScreens.SIGNATURE);
                } else if (origin == ORIGIN_CONTRACT_SIGNATURE) {
                    adjunto.setPantallaRecogida(PicturePickUpScreens.CONTRACTSIGNATURE);
                } else {
                    adjunto.setPantallaRecogida(PicturePickUpScreens.PENDINGSIGNATURE);
                    aviso.getVisitas().get(aviso.getVisitas().size() - 1).setNombreFirma(picName);
                }
                adjuntos.add(adjunto);
                aviso.setListaLocalAdjuntos(adjuntos);
                Log.d("Signature", Integer.toString(adjuntos.size()));
            } else {
                Toast.makeText(this, getString(R.string.signatureNotSaved), Toast.LENGTH_LONG).show();
            }
        }
        calloutPreferences.calloutJson().put(gson.toJson(aviso));
    }*/



/*    @Override
    public void onBackPressed() {
        switch (origin) {
            case ORIGIN_CALLOUT | ORIGIN_PENDING_SIGNATURE:
                Toast.makeText(this, getString(R.string.signatureRequired), Toast.LENGTH_LONG).show();
                break;

            case ORIGIN_TECHNICIAN_SIGNATURE:
                Toast.makeText(this, getString(R.string.signatureRequiredTech), Toast.LENGTH_LONG).show();
                break;
        }
    }*/


    public class Signature extends View {
        private static final float STROKE_WIDTH = 3f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private static final String TAG = "Signature";
        private final RectF dirtyRect = new RectF();
        private final Paint paint = new Paint();
        private final Path path = new Path();
        private float lastTouchX;
        private float lastTouchY;

        public Signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public boolean save(View v, String storedPath) {
            boolean result = false;
            Log.v("tag", "Width: " + v.getWidth());
            Log.v("tag", "Height: " + v.getHeight());
/*            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);
                final Canvas canvas = new Canvas(bitmap);
                v.draw(canvas);
                bitmap = ImageUtils.scaleToFit(bitmap, BuildConfig.IMAGE_MAX_SIZE);
                result = ImageUtils.compressAndSave(bitmap, BuildConfig.IMAGE_COMPRESSION_RATIO, storedPath);
            }*/
            return result;
        }

        public void clear() {
            path.reset();
            lastTouchX = 0.0f;
            lastTouchY = 0.0f;
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            final float eventX = event.getX();
            final float eventY = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:
                    resetDirtyRect(eventX, eventY);
                    final int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        final float historicalX = event.getHistoricalX(i);
                        final float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;
                default:
                    Log.d(TAG, "Ignored touch event: " + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH), (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                (int) (dirtyRect.right + HALF_STROKE_WIDTH), (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }}
    }


