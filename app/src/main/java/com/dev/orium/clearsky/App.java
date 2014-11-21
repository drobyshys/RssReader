package com.dev.orium.clearsky;

import android.app.Application;

/**
 * Created by y.drobysh on 17.11.2014.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();


        DataUtils.init(this);

    }
}
