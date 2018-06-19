package com.vijay.jsonwizard.expressions;

import android.content.Context;
import android.util.Log;

public class ExternalContentResolverFactory {

    private static final String TAG = "ExternalContentResolverFactory";

    public static ExternalContentResolver getInstance(Context context, String clazz) {
        try {
            Class c = Class.forName(clazz);
            Object instance = c.newInstance();
            if (!(instance instanceof ExternalContentResolver)) {
                Log.e(TAG, "Instance should be an instance of ExternalContentResolver");
                throw new InstantiationException();
            }
            ((ExternalContentResolver)instance).setContext(context);
            return (ExternalContentResolver) instance;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
