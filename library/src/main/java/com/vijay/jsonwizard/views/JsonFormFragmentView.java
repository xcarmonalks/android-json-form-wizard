package com.vijay.jsonwizard.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.vijay.jsonwizard.demo.resources.ResourceResolver;
import com.vijay.jsonwizard.expressions.JsonExpressionResolver;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.i18n.JsonFormBundle;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.mvp.MvpView;
import com.vijay.jsonwizard.mvp.ViewState;

import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

/**
 * Created by vijay on 5/14/15.
 */
public interface JsonFormFragmentView<VS extends ViewState> extends MvpView {
    Bundle getArguments();

    void setActionBarTitle(String title);

    Context getContext();

    void showToast(String message);

    CommonListener getCommonListener();

    void addFormElements(List<View> views);

    ActionBar getSupportActionBar();

    Toolbar getToolbar();

    void setToolbarTitleColor(int white);

    void updateVisibilityOfNextAndSave(boolean next, boolean save);

    void hideKeyBoard();

    void transactThis(JsonFormFragment next);

    void startActivityForResult(Intent intent, int requestCode);

    void updateRelevantImageView(Bitmap bitmap, String imagePath, String currentKey,
        String stepName);

    void updateRelevantEditText(String currentKey, String value);

    void updateRelevantMap(String key, String value);

    void updateRelevantTextInputLayout(String key, String value);

    void writeValue(String stepName, String key, String value);

    void writeValue(String stepName, String prentKey, String childObjectKey, String childKey, String value);

    JSONObject getStep(String stepName);

    String getCurrentJsonState();

    void finishWithResult(Intent returnIntent);

    void setUpBackButton();

    void backClick();

    void unCheckAllExcept(String parentKey, String childKey);

    String getCount();

    JsonFormBundle getBundle(Locale locale);

    JsonExpressionResolver getExpressionResolver();

    ResourceResolver getResourceResolver();

    void historyPush(String stepName);

    void historyPop();
}
