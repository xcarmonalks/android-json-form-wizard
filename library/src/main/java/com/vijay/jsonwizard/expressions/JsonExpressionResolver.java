package com.vijay.jsonwizard.expressions;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JsonOrgJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JsonOrgMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import java.util.EnumSet;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonExpressionResolver {

    private JSONObject dataObject;
    private DocumentContext dataDocumentContext;

    public JsonExpressionResolver(JSONObject form) throws JSONException {
        if (form.has("data")) {
            dataObject = form.getJSONObject("data");
            dataDocumentContext = JsonPath.parse(dataObject);
        }
    }

    public boolean isValidExpression(String expression) {
        if (expression == null) {
            return false;
        }
        return expression.startsWith("$.");
    }

    public String resolveAsString(String expression) throws JSONException {
        JSONArray array = dataDocumentContext.read(expression);
        if (array.length() == 0) {
            return null;
        }
        return array.getString(0);
    }


    public JSONObject resolveAsObject(String expression) throws JSONException {
        JSONArray array = dataDocumentContext.read(expression);
        if (array.length() == 0) {
            return null;
        }
        return array.getJSONObject(0);
    }

    public JSONArray resolveAsArray(String expression, JSONObject instance) throws JSONException {
        if (instance != null) {
            dataDocumentContext.put("$", "current-values", instance);
        }
        JSONArray array = dataDocumentContext.read(expression);

        dataDocumentContext.delete("current-values");

        if (array.length() == 0) {
            return null;
        }
        Object item = array.get(0);
        if (item instanceof JSONArray) {
            return (JSONArray) item;
        }
        return array;
    }

    static {
        Configuration.setDefaults(new Configuration.Defaults() {

            private final JsonProvider jsonProvider = new JsonOrgJsonProvider();
            private final MappingProvider mappingProvider = new JsonOrgMappingProvider();

            @Override
            public JsonProvider jsonProvider() {
                return jsonProvider;
            }

            @Override
            public MappingProvider mappingProvider() {
                return mappingProvider;
            }

            @Override
            public Set<Option> options() {
                return EnumSet.of(Option.DEFAULT_PATH_LEAF_TO_NULL,
                        Option.ALWAYS_RETURN_LIST);
            }
        });
    }

}
