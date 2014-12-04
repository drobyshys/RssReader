package com.dev.orium.reader.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.dev.orium.reader.App;
import com.dev.orium.reader.R;
import com.dev.orium.reader.MainController;

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
        super.onCreate(savedInstanceState);

        savedState = savedInstanceState;

        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);


        this.isTablet = drawerLayout == null;

        controller = new MainController(this, savedInstanceState);
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
    protected void onStop() {
        super.onStop();
        controller.saveData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        controller.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (!controller.onBackPressed())
            super.onBackPressed();

    }
}
