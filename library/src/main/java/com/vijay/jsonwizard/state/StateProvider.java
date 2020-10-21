package com.vijay.jsonwizard.state;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.FileNotFoundException;

import static com.vijay.jsonwizard.state.StateContract.COL_JSON;
import static com.vijay.jsonwizard.state.StateContract.buildUri;

public class StateProvider extends ContentProvider {

    private static String sState = null;

    public static Uri saveState(String state) {
        sState = state;
        return buildUri();
    }

    private static boolean uriMatchesChecklistState(@NonNull Uri uri) {
        return uri.getPath() != null && uri.getPath().equals("/" + StateContract.FORM_STATE);
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if (!uriMatchesChecklistState(uri)) {
            return null;
        }

        final MatrixCursor cursor = new MatrixCursor(new String[]{COL_JSON}, 1);
        cursor.addRow(new String[]{sState});
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return StateContract.ITEM_TYPE;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public AssetFileDescriptor openAssetFile(@NonNull Uri uri, @NonNull String mode) throws FileNotFoundException {
        return super.openAssetFile(uri, mode);
    }
}
