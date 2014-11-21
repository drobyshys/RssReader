package com.dev.orium.clearsky.activities;

import android.app.ActionBar;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.dev.orium.clearsky.R;
import com.dev.orium.clearsky.fragments.ViewRssFragment;

/**
 * Created by y.drobysh on 19.11.2014.
 */
public class RssViewActivity extends ActionBarActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_container);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new ViewRssFragment())
                    .commit();

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
