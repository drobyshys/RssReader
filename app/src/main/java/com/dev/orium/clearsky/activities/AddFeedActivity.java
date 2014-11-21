package com.dev.orium.clearsky.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.orium.clearsky.R;
import com.dev.orium.clearsky.Utils;
import com.dev.orium.clearsky.data.Feed;
import com.dev.orium.clearsky.network.RequestHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;

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





    private ArrayAdapter<Feed> adapter;
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
    }

    private void applyDialogTheme() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = 1.0f;
        params.dimAmount = 0.1f;
        getWindow().setAttributes((WindowManager.LayoutParams) params);

        DisplayMetrics metrics = Utils.getScreenMetrics(this);
        // This sets the window size, while working around the IllegalStateException thrown by ActionBarView
        getWindow().setLayout(Math.min(metrics.widthPixels, 900) ,Math.min(metrics.heightPixels, 1200));
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        searchFeedTask.setActivity(null);
        return searchFeedTask;
    }

    private void onSearchComplete(List<Feed> feeds, boolean hasError) {
        if (adapter == null) {
            adapter = new ArrayAdapter<Feed>(this, android.R.layout.simple_list_item_1, android.R.id.text1, new ArrayList<Feed>());
            listView.setAdapter(adapter);
        }
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
        if (!Utils.checkInternetConnection(this)) {
            Toast.makeText(this, R.string.check_connection, Toast.LENGTH_SHORT).show();
            return;
        }

        String query = etQuery.getText().toString().trim();
        if (query != null) {
            Utils.hideKeyboard(this, etQuery);

            searchFeedTask = new SearchFeedTask(this);
            searchFeedTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, query);
            pbLoading.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        }
    }

    @OnItemClick(android.R.id.list)
    void onFeedClick(AdapterView<?> parent, View v, int position, long id) {
        Intent data = new Intent();
        data.putExtra(SELECTED_FEED, adapter.getItem(position));

        //save to db

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

        @Override
        protected List<Feed> doInBackground(String... params) {
            try {
                return RequestHelper.searchFeedRequest(params[0]);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
                hasError = true;
            }
            return null;
        }


        @Override
        protected void onPostExecute(List<Feed> feeds) {
            if (!isCancelled() && activity != null) {
                activity.onSearchComplete(feeds, hasError);
            }
        }

    }
}
