package com.dev.orium.clearsky.fragments;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.dev.orium.clearsky.R;
import com.dev.orium.clearsky.ui.MainController;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class MenuFragment extends Fragment implements AdapterView.OnItemClickListener {


    private ListView list;
    private MainController controller;

    public MenuFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        list = (ListView) inflater.inflate(R.layout.fragment_list, container, false);
        return list;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        list.setAdapter(new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_selectable_list_item,
                getResources().getStringArray(R.array.drawer_items))
        );
        list.setOnItemClickListener(this);
    }

    public void setController(MainController controller) {
        this.controller = controller;
    }

    public MainController getController() {
        return controller;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        controller.onMenuItemClick();
    }
}
