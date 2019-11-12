package com.vijay.jsonwizard.expressions;


import android.util.Log;

import androidx.collection.LruCache;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import org.json.JSONObject;

public class ExternalContentLru extends LruCache<String, DocumentContext> {

    public static final String TAG = "ExternalContentLru";
    private ExternalContentResolver contentResolver;

    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *     the maximum number of entries in the cache. For all other caches,
     *     this is the maximum sum of the sizes of the entries in this cache.
     */
    public ExternalContentLru(ExternalContentResolver contentResolver, int maxSize) {
        super(maxSize);
        this.contentResolver = contentResolver;
    }

    @Override
    protected DocumentContext create(String key) {
        if (contentResolver == null) {
            Log.w(TAG, "External content resolver not specified");
            return null;
        }

        JSONObject content = contentResolver.loadExtenalContent(key);
        if (content != null) {
            return JsonPath.parse(content);
        }
        return null;
    }
}
