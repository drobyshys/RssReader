package com.dev.orium.reader;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by y.drobysh on 01.12.2014.
 */
@Module(library = true)
public class AndroidModule {

    private final App app;

    public AndroidModule(App app) {
        this.app = app;
    }

    @Provides @Singleton Context provideApplicationContext() {
        return app;
    }
}
