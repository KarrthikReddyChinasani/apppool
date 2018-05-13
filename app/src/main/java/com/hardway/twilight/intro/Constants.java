package com.hardway.twilight.intro;

import com.hardway.twilight.animations.ZoomOutSlideTransformer;

import java.util.ArrayList;

/**
 * Created by karth on 1/29/2018.
 */

public class Constants {

    public static final ArrayList<TransformerItem> TRANSFORM_CLASSES;

    static {
        TRANSFORM_CLASSES = new ArrayList<>();
        TRANSFORM_CLASSES.add(new TransformerItem(ZoomOutSlideTransformer.class));
    }

}
