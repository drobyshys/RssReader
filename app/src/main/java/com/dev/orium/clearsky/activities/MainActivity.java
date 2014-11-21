package com.dev.orium.clearsky.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.dev.orium.clearsky.R;
import com.dev.orium.clearsky.fragments.ViewRssFragment;
import com.dev.orium.clearsky.ui.MainController;
import com.dev.orium.clearsky.ui.ResizeWidthAnimation;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;


public class MainActivity extends ActionBarActivity {


    @Optional
    @InjectView(R.id.detailContainer)
    FrameLayout detailContainer;
    @Optional
    @InjectView(R.id.container)
    FrameLayout container;

    @Optional @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;



    private boolean isTablet;
    private boolean expanded;
    private MainController controller;

    private Bundle savedState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        savedState = savedInstanceState;
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);


        this.isTablet = drawerLayout == null;

        controller = new MainController(this);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        controller.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (controller.onOptionsItemSelected(item))
            return true;


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!controller.onBackPressed())
            super.onBackPressed();

    }

}
