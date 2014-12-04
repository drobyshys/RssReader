package com.dev.orium.reader.ui;

import android.app.Activity;
import android.content.res.Configuration;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import com.dev.orium.reader.Utils.AppUtils;

/**
* Created by y.drobysh on 02.12.2014.
*/
public class PaneAnimator implements Animation.AnimationListener {
//        private final Activity context;
    private final FrameLayout menu;
    private final FrameLayout feed;
    private final FrameLayout rssView;
    private final int _fullWidth;
    private final int _smallWidth;
    private final int _bigWidth;
    private final boolean _isPortrait;

    private int _runningAnimationsCount;

    private boolean mIsRssShown;
    private boolean isFull;

    public PaneAnimator(Activity context, FrameLayout menu, FrameLayout feed, FrameLayout rssView) {
//            this.context = context;
        this.menu = menu;
        this.feed = feed;
        this.rssView = rssView;

        _isPortrait = context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        _fullWidth = AppUtils.getScreenWidth(context);
        _smallWidth = _fullWidth / 3;
        _bigWidth = _smallWidth * 2;

        menu.getLayoutParams().width = _smallWidth;
        feed.getLayoutParams().width = _bigWidth;
        rssView.getLayoutParams().width = _bigWidth;


    }

    public void onMenuItem() {
        changeWidthTo(feed, _bigWidth);
    }


    public void showRss() {
        if (isMenuVisible())
            moveByX(menu, -_smallWidth);

        if (mIsRssShown)
            return;

        changeWidthTo(feed, _isPortrait ? 0 : _smallWidth);

        mIsRssShown = true;

        if (_isPortrait) {
            changeWidthTo(rssView, _fullWidth);
            moveByX(feed, 0);
            moveByX(rssView, 0);
            isFull = true;
        } else {
            moveByX(feed, 0);
            moveByX(rssView, _smallWidth);
        }
    }

    public boolean isMenuVisible() {
        return menu.getX() >= 0;
    }

    public void onBackPress() {
        mIsRssShown = false;

        if (isMenuVisible())
            return;

        moveByX(menu,0);

        changeWidthTo(feed, _bigWidth);
        moveByX(feed, _smallWidth);
        moveByX(rssView, _fullWidth);
        return;
    }

    public void toggleFullScreen() {
        if (_runningAnimationsCount != 0)
            return;

        if (mIsRssShown) {
            if (isFull) {
                changeWidthTo(feed, _smallWidth);
                changeWidthTo(rssView, _bigWidth);
            } else {
                changeWidthTo(feed, 0);
                changeWidthTo(rssView, _fullWidth);
            }
            isFull = !isFull;
        }
    }

    private void changeWidthTo(FrameLayout frame, int newWidth) {
        if (frame.getWidth() != newWidth) {
            frame.getLayoutParams().width = newWidth;
            frame.requestLayout();
        }
    }

    private void moveByX(View v, int y) {
        MoveToXAnimation anim = new MoveToXAnimation(v, y);
        anim.setDuration(500);
        v.startAnimation(anim);
        anim.setAnimationListener(this);
    }

    @Override
    public void onAnimationStart(Animation animation) {
        _runningAnimationsCount++;
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        _runningAnimationsCount--;
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
