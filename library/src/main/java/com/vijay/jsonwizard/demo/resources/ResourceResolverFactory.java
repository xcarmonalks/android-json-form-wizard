package com.vijay.jsonwizard.demo.resources;

import android.content.Context;
import android.util.Log;

import com.vijay.jsonwizard.expressions.ExternalContentResolver;

public class ResourceResolverFactory {

    private static final String TAG = "ExtContentResolverFact";

    public static ResourceResolver getInstance(Context context, String clazz) {
        try {
            Class c = Class.forName(clazz);
            Object instance = c.newInstance();
            if (!(instance instanceof ResourceResolver)) {
                Log.e(TAG, "Instance should be an instance of ResourceResolver");
                throw new InstantiationException();
            }
            return (ResourceResolver) instance;
        } catch (Exception e) {
            Log.e(TAG, "Error creating ResourceResolver instance",e);
        }
        return null;
    }
}
