package com.vijay.jsonwizard.utils;

/**
 * Created by xcarmona on 21/06/18.
 */

public class CarouselItem {

    private final String name;
    private final String image;

    public CarouselItem(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }
}
