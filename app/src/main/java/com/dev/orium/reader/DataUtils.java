package com.dev.orium.reader;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by y.drobysh on 17.11.2014.
 */
public class DataUtils {

    private static Context context;

    public static void init(Context ctx) {
        context = ctx;
    }

    public static List<String> getUserCities() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("test");
        list.add("nononono");

        return list;
    }


}
