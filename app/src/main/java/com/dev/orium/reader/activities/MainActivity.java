package com.dev.orium.reader.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.dev.orium.reader.R;
import com.dev.orium.reader.controller.Controller;
import com.dev.orium.reader.controller.MainController;
import com.dev.orium.reader.controller.TabletController;
import com.dev.orium.reader.fragments.RetainedFragment;

import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

    private boolean isTablet;
    private boolean expanded;
    private Controller controller;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        this.isTablet = findViewById(R.id.drawer_layout) == null;

        if (!isTablet) controller = new MainController(this, state);
        else           controller = new TabletController(this, state);
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
        controller.saveData(null);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        controller.saveData(outState);
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
