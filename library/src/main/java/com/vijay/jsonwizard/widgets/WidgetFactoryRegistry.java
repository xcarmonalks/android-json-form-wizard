package com.vijay.jsonwizard.widgets;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jurkiri on 23/11/17.
 */

public class WidgetFactoryRegistry {

    private static final Map<String, FormWidgetFactory> map = new HashMap<>();

    static {
        map.put(JsonFormConstants.EDIT_TEXT, new MaterialEditTextFactory());
        map.put(JsonFormConstants.LABEL, new LabelFactory());
        map.put(JsonFormConstants.CHECK_BOX, new CheckBoxFactory());
        map.put(JsonFormConstants.RADIO_BUTTON, new RadioButtonFactory());
        map.put(JsonFormConstants.CHOOSE_IMAGE, new ImagePickerFactory());
        map.put(JsonFormConstants.SPINNER, new SpinnerFactory());
        map.put(JsonFormConstants.DATE_PICKER, new DatePickerFactory());
        map.put(JsonFormConstants.TIME_PICKER, new TimePickerFactory());
        map.put(JsonFormConstants.EDIT_GROUP, new EditGroupFactory());
        map.put(JsonFormConstants.SEPARATOR, new SeparatorFactory());
        map.put(JsonFormConstants.CAROUSEL, new CarouselFactory());
        map.put(JsonFormConstants.EXTENDED_LABEL, new ExtendedLabelFactory());
        map.put(JsonFormConstants.BARCODE_TEXT, new BarcodeTextFactory());
        map.put(JsonFormConstants.LOCATION_PICKER, new LocationPickerFactory());
        map.put(JsonFormConstants.RESOURCE_VIEWER, new ResourceViewerFactory());
    }

    public static FormWidgetFactory getWidgetFactory(String type) {
        return map.get(type);
    }
}
