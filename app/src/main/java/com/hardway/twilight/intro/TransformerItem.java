package com.hardway.twilight.intro;

import android.support.v4.view.ViewPager;

/**
 * Created by karth on 1/29/2018.
 */

public class TransformerItem {

    final String title;

    final Class<? extends ViewPager.PageTransformer> clazz;

    public TransformerItem(Class<? extends ViewPager.PageTransformer> clazz) {

        this.clazz = clazz;

        title = clazz.getSimpleName();

    }

    public Class<? extends ViewPager.PageTransformer> getClazz() {

        return clazz;

    }

    @Override

    public String toString() {

        return title;

    }

}