package com.vijay.jsonwizard.utils;

/**
 * Created by xcarmona on 21/06/18.
 */

public class CarouselItem {

    private final String name;
    private final String value;
    private final String image;

    public CarouselItem(String name, String value, String image) {
        this.name = name;
        this.value = value;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getImage() {
        return image;
    }
}
