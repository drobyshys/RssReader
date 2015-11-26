package com.dev.orium.reader.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.dev.orium.reader.R;
import com.dev.orium.reader.fragments.ViewRssFragment;

public class RssViewActivity extends AppCompatActivity {

    private ViewRssFragment mFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_container);


        if (savedInstanceState == null) {
            mFragment = new ViewRssFragment();

            if (getIntent() != null)
                mFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, mFragment)
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
