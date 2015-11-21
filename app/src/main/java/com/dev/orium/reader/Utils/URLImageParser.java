package com.dev.orium.reader.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class URLImageParser implements Html.ImageGetter {

    private View mView;

    /***
     * Construct the URLImageParser which will execute AsyncTask and refresh the container
     */
    public URLImageParser(View view) {
        mView = view;
    }

    @Override
    public Drawable getDrawable(final String source) {
        URLDrawable drawable = new URLDrawable(mView);
        ImageLoader.getInstance().loadImage(source, drawable);

        return drawable;
    }

    static class URLDrawable extends BitmapDrawable implements ImageLoadingListener {
        // the drawable that you need to set, you could set the initial drawing
        // with the loading image if you need to
        protected Drawable drawable;
        private View mView;

        public URLDrawable(final View c) {

            mView = c;
        }

        @Override
        public void draw(Canvas canvas) {
            // override the draw to facilitate refresh function later
            if(drawable != null) {
                drawable.draw(canvas);
            }
        }

        @Override
        public void onLoadingStarted(final String imageUri, final View view) {

        }

        @Override
        public void onLoadingFailed(final String imageUri, final View view, final FailReason failReason) {

        }

        @Override
        public void onLoadingComplete(final String imageUri, final View view, final Bitmap loadedImage) {
            drawable = new BitmapDrawable(mView.getContext().getResources(), loadedImage);
            drawable.setBounds(0, 0, loadedImage.getWidth(), loadedImage.getHeight());
            setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            mView.invalidate();
        }

        @Override
        public void onLoadingCancelled(final String imageUri, final View view) {

        }
    }

}
