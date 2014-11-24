package com.dev.orium.reader.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dev.orium.reader.R;
import com.dev.orium.reader.MainController;


public class ViewRssFragment extends Fragment {
    private MainController controller;


//    @InjectView(android.R.id.list)
//    ListView listView;

    public ViewRssFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_rss, container, false);
    }

    public void setController(MainController controller) {
        this.controller = controller;
    }

    public MainController getController() {
        return controller;
    }

//    @Override
//    public void onViewCreated(View v, Bundle savedInstanceState) {
//        super.onViewCreated(v, savedInstanceState);
//        ButterKnife.inject(this, v);
//
//        listView.setAdapter(new ArrayAdapter<FeedItem>(getActivity(), android.R.layout.simple_list_item_1,
//                android.R.id.text1, FeedItem.getFakeData()));
//
//    }

//    @OnItemClick(android.R.id.list)
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Toast.makeText(getActivity(), position + "", Toast.LENGTH_SHORT).show();
//    }


}
