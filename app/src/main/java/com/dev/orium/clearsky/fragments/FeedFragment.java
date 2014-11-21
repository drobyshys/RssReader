package com.dev.orium.clearsky.fragments;



import android.app.Activity;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.dev.orium.clearsky.R;
import com.dev.orium.clearsky.model.FeedItem;
import com.dev.orium.clearsky.ui.MainController;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class FeedFragment extends Fragment {

    @InjectView(android.R.id.list)
    ListView listView;
    private MainController controller;

    public FeedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        ButterKnife.inject(this, v);

        listView.setAdapter(new ArrayAdapter<FeedItem>(getActivity(), android.R.layout.simple_list_item_1,
                android.R.id.text1, FeedItem.getFakeData()));

    }

    @OnItemClick(android.R.id.list)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getActivity(), position + "", Toast.LENGTH_SHORT).show();

        controller.onFeedItemClick();


    }


    public void setController(MainController controller) {
        this.controller = controller;
    }

    public MainController getController() {
        return controller;
    }
}
