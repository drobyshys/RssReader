package com.dev.orium.reader.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.orium.reader.R;
import com.dev.orium.reader.Utils.AppUtils;
import com.dev.orium.reader.adapters.FeedSearchAdapter;
import com.dev.orium.reader.model.Feed;
import com.dev.orium.reader.data.RssProvider;
import com.dev.orium.reader.network.RequestHelper;
import com.dev.orium.reader.MainController;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class AddFeedActivity extends ActionBarActivity {

    public static final String TAG = "AddFeedActivity";
    public static final String SELECTED_FEED = "feed";

    @InjectView(R.id.btnSearch)
    Button btnSearch;
    @InjectView(R.id.etQuery)
    EditText etQuery;
    @InjectView(android.R.id.list)
    ListView listView;
    @InjectView(R.id.pbLoading)
    ProgressBar pbLoading;
    @InjectView(android.R.id.empty)
    TextView tvEmpty;





    private FeedSearchAdapter adapter;
    private SearchFeedTask searchFeedTask;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        applyDialogTheme();

        setContentView(R.layout.activity_add_feed);

        setTitle(R.string.title_activity_add_feed);

        ButterKnife.inject(this);

        searchFeedTask = (SearchFeedTask) getLastCustomNonConfigurationInstance();

        if (searchFeedTask != null && searchFeedTask.getStatus() == AsyncTask.Status.RUNNING) {
            searchFeedTask.setActivity(this);
            pbLoading.setVisibility(View.VISIBLE);
        }

        adapter = new FeedSearchAdapter(this, new ArrayList<Feed>());
        listView.setAdapter(adapter);
    }

    private void applyDialogTheme() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = 1.0f;
        params.dimAmount = 0.1f;
        getWindow().setAttributes((WindowManager.LayoutParams) params);

        DisplayMetrics metrics = AppUtils.getScreenMetrics(this);
        // This sets the window size, while working around the IllegalStateException thrown by ActionBarView
        getWindow().setLayout(Math.min(metrics.widthPixels, 900) ,Math.min(metrics.heightPixels, 1200));
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        searchFeedTask.setActivity(null);
        return searchFeedTask;
    }

    private void onSearchComplete(List<Feed> feeds, boolean hasError) {
        pbLoading.setVisibility(View.GONE);
        adapter.clear();

        if (hasError) {
//            tvEmpty.setText(getString(R.string.error_search));
            return;
        }

        adapter.addAll(feeds);
        adapter.notifyDataSetChanged();
        if (feeds.size() == 0) {
            tvEmpty.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.btnSearch)
    void search() {
        if (!AppUtils.checkInternetConnection(this)) {
            Toast.makeText(this, R.string.check_connection, Toast.LENGTH_SHORT).show();
            return;
        }

        String query = etQuery.getText().toString().trim();
        if (query != null) {
            AppUtils.hideKeyboard(this, etQuery);

            searchFeedTask = new SearchFeedTask(this);
            searchFeedTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, query);
            pbLoading.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        }
    }

    @OnItemClick(android.R.id.list)
    void onFeedClick(AdapterView<?> parent, View v, int position, long id) {
        Feed feed = adapter.getItem(position);
        Uri uri = cupboard().withContext(this).put(RssProvider.FEEDS_URI, feed);
        int carId = Integer.parseInt(uri.getPathSegments().get(1));
        Intent data = new Intent();
        data.putExtra(MainController.FEED_ID, carId);
        setResult(RESULT_OK, data);
        finish();
    }

    private static class SearchFeedTask extends AsyncTask<String, Void, List<Feed>> {
        private boolean hasError;
        private AddFeedActivity activity;

        public void setActivity(AddFeedActivity activity) {
            this.activity = activity;
        }

        public SearchFeedTask(AddFeedActivity activity) {
            this.activity = activity;
        }
        @Override protected List<Feed> doInBackground(String... params) {
            try {
                return RequestHelper.searchFeedRequest(params[0]);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
                hasError = true;
            }
            return null;
        }
        @Override protected void onPostExecute(List<Feed> feeds) {
            if (!isCancelled() && activity != null) {
                activity.onSearchComplete(feeds, hasError);
            }
        }

    }
}
