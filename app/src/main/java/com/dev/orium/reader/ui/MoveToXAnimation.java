package com.dev.orium.reader.ui;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by y.drobysh on 02.12.2014.
 */
public class MoveToXAnimation extends Animation {

    private final View view;
    private final float startX, y;

    public MoveToXAnimation(View view, float y) {
        this.view = view;
        startX = view.getX();
        this.y = y - startX;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        float newPos = startX + (y * interpolatedTime);
        view.setX(newPos);
        view.requestLayout();
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }


}
