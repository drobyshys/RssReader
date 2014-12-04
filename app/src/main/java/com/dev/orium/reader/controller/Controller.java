package com.dev.orium.reader.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.dev.orium.reader.model.Feed;

/**
 * Created by y.drobysh on 04.12.2014.
 */
public interface Controller {
    void selectFeed(Feed feed);

    void onRssItemClick(long id);

    void onCreateOptionsMenu(Menu menu);

    void saveData(Bundle outState);

    boolean onOptionsItemSelected(MenuItem item);

    void onActivityResult(int requestCode, int resultCode, Intent data);

    boolean onBackPressed();
}
