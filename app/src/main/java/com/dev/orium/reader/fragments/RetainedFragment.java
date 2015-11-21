package com.dev.orium.reader.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by admin on 21.11.2015.
 */
public class RetainedFragment<T> extends Fragment {

    public static final String FRAGMENT_TAG = "RetainedFragment";
    private T data;

    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    public void setData(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

}
