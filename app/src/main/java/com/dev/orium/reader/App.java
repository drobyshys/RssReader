package com.dev.orium.reader;

import android.app.Application;

import com.dev.orium.reader.Utils.AppUtils;
import com.dev.orium.reader.Utils.DateUtils;
import com.dev.orium.reader.Utils.SharedUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.L;

import java.util.Arrays;
import java.util.List;

/**
 * Created by y.drobysh on 17.11.2014.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        AppUtils.init(this);
        SharedUtils.init(this);
        DateUtils.init(this);


        initImageLoader();
    }

    private void initImageLoader() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_stub) // resource or drawable
                .showImageForEmptyUri(R.drawable.ic_stub) // resource or drawable
                .showImageOnFail(R.drawable.ic_stub) // resource or drawable
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(10)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .diskCacheSize(10 * 1024 * 1024)
                .defaultDisplayImageOptions(options)
                .build();

        L.writeLogs(true);

        ImageLoader.getInstance().init(config);
    }
}


//todo
/*
readed/unreader
favorites
save to disk?
settings
themes
web version
search select several



 */

