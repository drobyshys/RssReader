package com.dev.orium.clearsky.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dev.orium.clearsky.R;

/**
 * Created by y.drobysh on 20.11.2014.
 */
public class AddFeedFragment extends DialogFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_feed, container, false);
    }
}
