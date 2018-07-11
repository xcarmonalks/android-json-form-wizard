package com.vijay.jsonwizard.presenters;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.Switch;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.CheckBox;
import com.vijay.jsonwizard.customviews.RadioButton;
import com.vijay.jsonwizard.expressions.JsonExpressionResolver;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.i18n.JsonFormBundle;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.mvp.MvpBasePresenter;
import com.vijay.jsonwizard.utils.CarouselAdapter;
import com.vijay.jsonwizard.utils.DateUtils;
import com.vijay.jsonwizard.utils.ImagePicker;
import com.vijay.jsonwizard.utils.ImageUtils;
import com.vijay.jsonwizard.utils.JsonFormUtils;
import com.vijay.jsonwizard.utils.ValidationStatus;
import com.vijay.jsonwizard.views.JsonFormFragmentView;
import com.vijay.jsonwizard.viewstates.JsonFormFragmentViewState;
import com.vijay.jsonwizard.widgets.CarouselFactory;
import com.vijay.jsonwizard.widgets.DatePickerFactory;
import com.vijay.jsonwizard.widgets.EditTextFactory;
import com.vijay.jsonwizard.widgets.ImagePickerFactory;
import com.vijay.jsonwizard.widgets.SpinnerFactory;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import fr.ganfra.materialspinner.MaterialSpinner;

/**
 * Created by vijay on 5/14/15.
 */
public class JsonFormFragmentPresenter extends MvpBasePresenter<JsonFormFragmentView<JsonFormFragmentViewState>> {
    private static final String TAG = "FormFragmentPresenter";
    private static final int RESULT_LOAD_IMG = 1;
    private String mStepName;
    private JSONObject mStepDetails;
    private String mCurrentKey;
    private int mVisualizationMode;
    private JsonFormInteractor mJsonFormInteractor = JsonFormInteractor.getInstance();

    public void addFormElements() {
        switch (mVisualizationMode){
            case JsonFormConstants.VISUALIZATION_MODE_READ_ONLY :
                addFormReadOnlylements();
                break;
            default:
                addFormEditionElements();
        }
    }

    private void addFormEditionElements() {
        mStepName = getView().getArguments().getString("stepName");
        JSONObject step = getView().getStep(mStepName);
        JsonFormBundle bundle = getView().getBundle(getView().getContext().getResources().getConfiguration().locale);
        JsonExpressionResolver resolver = getView().getExpressionResolver();

        try {
            mStepDetails = new JSONObject(step.toString());
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        List<View> views = getStepFormElements(mStepName, mStepDetails, bundle, resolver);
        getView().addFormElements(views);
    }

    private void addFormReadOnlylements() {
        String stepName = getView().getArguments().getString("stepName");
        JSONObject step = getView().getStep(stepName);
        JsonFormBundle bundle = getView().getBundle(getView().getContext().getResources().getConfiguration().locale);
        JsonExpressionResolver resolver = getView().getExpressionResolver();

        List<View> views = getStepFormElements(stepName, step, bundle,resolver);
        if(step.has("next")){
            try{
                stepName = step.getString("next");
                step = getView().getStep(stepName);
                views.addAll(getStepFormElements(stepName, step, bundle,resolver));
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        getView().addFormElements(views);
    }

    private List<View> getStepFormElements(String stepName, JSONObject stepDetails, JsonFormBundle bundle, JsonExpressionResolver resolver){
        List<View> views = mJsonFormInteractor.fetchFormElements(stepName, getView().getContext(), stepDetails,
                getView().getCommonListener(), bundle, resolver, mVisualizationMode);
        return views;
    }

    @SuppressLint("ResourceAsColor")
    public void setUpToolBar() {
        if(mVisualizationMode == JsonFormConstants.VISUALIZATION_MODE_EDIT){
            if (!mStepName.equals(JsonFormConstants.FIRST_STEP_NAME)) {
                getView().setUpBackButton();
            }
            JsonFormBundle bundle = getView().getBundle(getView().getContext().getResources().getConfiguration().locale);
            getView().setActionBarTitle(bundle.resolveKey(mStepDetails.optString("title")));
            if (mStepDetails.has("next")) {
                getView().updateVisibilityOfNextAndSave(true, false);
            } else {
                getView().updateVisibilityOfNextAndSave(false, true);
            }
        } else {
            getView().setActionBarTitle(getView().getContext().getResources().getString(R.string.summary));
            getView().updateVisibilityOfNextAndSave(false, false);
        }
        setUpToolBarTitleColor();
    }

    public void setUpToolBarTitleColor() {
        getView().setToolbarTitleColor(R.color.white);
    }

    public void onBackClick() {
        getView().hideKeyBoard();
        getView().backClick();
    }

    private String resolveNextStep(JSONObject mStepDetails)  {
        try {
            JSONObject nextObject = mStepDetails.optJSONObject("next");
            if (nextObject != null) {
                JSONArray names = nextObject.names();
                for (int i = 0; i < names.length(); i++) {
                    if (isDefaultStep(nextObject, names.optString(i))) {
                        return names.optString(i);
                    } else {
                        JsonExpressionResolver resolver =  getView().getExpressionResolver();
                        String expression = nextObject.optString(names.optString(i));
                        if (resolver.isValidExpression(expression)) {
                            boolean eval = resolver
                                    .existsExpression(nextObject.optString(names.optString(i)),
                                            getCurrentValues());
                            if (eval) {
                                return names.optString(i);
                            }
                        } else {
                            Log.e(TAG,"resolveNextStep: Error evaluating next step - Expression is not valid");
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "resolveNextStep: Error evaluating next step",e);
        }
        return mStepDetails.optString("next");
    }

    private JSONObject getCurrentValues() throws JSONException {
        String currentJsonState =  getView().getCurrentJsonState();
        JSONObject currentJsonObject = new JSONObject(currentJsonState);
        JSONObject currentValues =  JsonFormUtils.extractDataFromForm(currentJsonObject);
        return currentValues;
    }

     private boolean isDefaultStep(JSONObject steps,String key) {
         try {
             return steps.getBoolean(key);
         } catch (JSONException e) {
             return false;
         }
     }

    public void onNextClick(LinearLayout mainView) {
        ValidationStatus validationStatus = writeValuesAndValidate(mainView);
        if (validationStatus.isValid()) {
            String nextStep = resolveNextStep(mStepDetails);
            JsonFormFragment next = JsonFormFragment.getFormFragment(nextStep);
            getView().hideKeyBoard();
            getView().transactThis(next);
        } else {
            getView().showToast(validationStatus.getErrorMessage());
        }
    }

    public ValidationStatus writeValuesAndValidate(LinearLayout mainView) {
        String type = (String) mainView.getTag(R.id.type);

        int childCount = mainView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = mainView.getChildAt(i);
            String key = (String) childAt.getTag(R.id.key);
            if (childAt instanceof MaterialEditText) {
                MaterialEditText editText = (MaterialEditText) childAt;
                if(editText.getTag(R.id.type).equals(JsonFormConstants.EDIT_TEXT)){
                    ValidationStatus validationStatus = EditTextFactory.validate(editText);
                    if (!validationStatus.isValid()) {
                        return validationStatus;
                    }
                    if(JsonFormConstants.EDIT_GROUP.equals(type)){
                        String parentKey = (String) mainView.getTag(R.id.key);
                        String childKey = (String) childAt.getTag(R.id.key);
                        getView().writeValue(mStepName, parentKey, JsonFormConstants.FIELDS_FIELD_NAME,
                                childKey, editText.getText().toString());
                    } else {
                        getView().writeValue(mStepName, key, editText.getText().toString());
                    }
                }else if(editText.getTag(R.id.type).equals(JsonFormConstants.DATE_PICKER)){
                    ValidationStatus validationStatus = DatePickerFactory.validate(editText);
                    if (!validationStatus.isValid()) {
                        return validationStatus;
                    }
                    Date date = DateUtils.parseDate(editText.getText().toString(), (String) editText.getTag(R.id.v_pattern));
                    if(JsonFormConstants.EDIT_GROUP.equals(type)){
                        String parentKey = (String) childAt.getTag(R.id.key);
                        String childKey = (String) childAt.getTag(R.id.childKey);
                        getView().writeValue(mStepName, parentKey, JsonFormConstants.FIELDS_FIELD_NAME,
                                childKey, DateUtils.toJSONDateFormat(date));
                    } else {
                        getView().writeValue(mStepName, key, DateUtils.toJSONDateFormat(date));
                    }
                }
            } else if (childAt instanceof ImageView) {
                ValidationStatus validationStatus = ImagePickerFactory.validate((ImageView) childAt);
                if (!validationStatus.isValid()) {
                    return validationStatus;
                }
                Object path = childAt.getTag(R.id.imagePath);
                if (path instanceof String) {
                    getView().writeValue(mStepName, key, (String) path);
                }
            } else if (childAt instanceof CheckBox) {
                String parentKey = (String) childAt.getTag(R.id.key);
                String childKey = (String) childAt.getTag(R.id.childKey);
                getView().writeValue(mStepName, parentKey, JsonFormConstants.OPTIONS_FIELD_NAME, childKey,
                        String.valueOf(((CheckBox) childAt).isChecked()));
            } else if (childAt instanceof RadioButton) {
                String parentKey = (String) childAt.getTag(R.id.key);
                String childKey = (String) childAt.getTag(R.id.childKey);
                if (((RadioButton) childAt).isChecked()) {
                    getView().writeValue(mStepName, parentKey, childKey);
                }
            } else if (childAt instanceof MaterialSpinner) {
                MaterialSpinner spinner = (MaterialSpinner) childAt;
                ValidationStatus validationStatus = SpinnerFactory.validate(spinner);
                if (!validationStatus.isValid()) {
                    spinner.setError(validationStatus.getErrorMessage());
                    return validationStatus;
                } else {
                    spinner.setError(null);
                }
            } else if (childAt instanceof DiscreteScrollView) {
            DiscreteScrollView dsv = (DiscreteScrollView) childAt;
            ValidationStatus validationStatus = CarouselFactory.validate(dsv);
            if (!validationStatus.isValid()) {
                return validationStatus;
            }
        }  else if (childAt instanceof LinearLayout) {
                writeValuesAndValidate((LinearLayout) childAt);
            }
        }
        return new ValidationStatus(true, null);
    }

    public void onSaveClick(LinearLayout mainView) {
        ValidationStatus validationStatus = writeValuesAndValidate(mainView);
        if (validationStatus.isValid()) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("json", getView().getCurrentJsonState());
            getView().finishWithResult(returnIntent);
        } else {
            Toast.makeText(getView().getContext(), validationStatus.getErrorMessage(), Toast.LENGTH_LONG);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_LOAD_IMG) {
            Context context = getView().getContext();
            Bitmap bitmap = ImagePicker.getImageFromResult(context, resultCode, data);
            //
            if(bitmap!=null) {
                File image = new File(context.getExternalCacheDir(), System.currentTimeMillis()+".jpg");
                ImageUtils.saveToFile(bitmap, image);
                getView().updateRelevantImageView(bitmap, image.getAbsolutePath(), mCurrentKey);
            }
        }
    }

    public void onClick(View v) {

        if(checkFormPermissions()) {
            String key = (String) v.getTag(R.id.key);
            String type = (String) v.getTag(R.id.type);
            if (JsonFormConstants.CHOOSE_IMAGE.equals(type)) {
                getView().hideKeyBoard();
           /*Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);*/
                Intent pickerIntent = ImagePicker.getPickImageIntent(v.getContext());
                mCurrentKey = key;
                getView().startActivityForResult(pickerIntent, RESULT_LOAD_IMG);
            }
        }
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (compoundButton instanceof CheckBox) {
            String parentKey = (String) compoundButton.getTag(R.id.key);
            String childKey = (String) compoundButton.getTag(R.id.childKey);
            getView().writeValue(mStepName, parentKey, JsonFormConstants.OPTIONS_FIELD_NAME, childKey,
                    String.valueOf(((CheckBox) compoundButton).isChecked()));
        } else if (compoundButton instanceof RadioButton) {
            if (isChecked) {
                String parentKey = (String) compoundButton.getTag(R.id.key);
                String childKey = (String) compoundButton.getTag(R.id.childKey);
                getView().unCheckAllExcept(parentKey, childKey);
                getView().writeValue(mStepName, parentKey, childKey);
            }
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String parentKey = (String) parent.getTag(R.id.key);
        if (position >= 0) {
            String value = (String) parent.getItemAtPosition(position + 1);
            getView().writeValue(mStepName, parentKey, value);
        }
    }

    public void onCurrentItemChanged(@Nullable CarouselAdapter.ViewHolder holder, int position) {
        if (holder != null) {
            ViewParent parent = holder.itemView.getParent();
            if (parent !=null && parent instanceof DiscreteScrollView){
                DiscreteScrollView dsvParent = (DiscreteScrollView) parent;
                String parentKey = (String) dsvParent.getTag(R.id.key);
                View view = holder.itemView.findViewById(R.id.value);
                if(view != null && view instanceof TextView){
                    TextView textView = (TextView) view;
                    String value = textView.getText().toString();
                    JsonFormFragmentView jffview = getView();
                    if(jffview!=null) {
                        jffview.writeValue(mStepName, parentKey, value);
                    }
                }
            }
        }
    }

    public void onSwitchOnOrOff(Switch v, boolean checked) {
        String key = (String) v.getTag(R.id.key);
        getView().writeValue(mStepName, key, String.valueOf(checked));
    }

    public void setCurrentKey(String key) {
        this.mCurrentKey = key;
    }

    public String getCurrentKey() {
        return mCurrentKey;
    }

    public String getStepName() {
        return mStepName;
    }

    public void setVisualizationMode(int visualizationMode){
        this.mVisualizationMode = visualizationMode;
    }

    private boolean checkFormPermissions(){

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };

        if(!hasPermissions(getView().getContext(), PERMISSIONS)){
            JsonFormFragment formFragment = (JsonFormFragment) getView();
            ActivityCompat.requestPermissions(formFragment.getActivity(), PERMISSIONS, PERMISSION_ALL);
        }else{
            return true;
        }
        return false;
    }

    private boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
