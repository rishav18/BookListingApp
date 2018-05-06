package com.example.android.booklistingapp;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final int BOOK_LOADER_ID = 1;
    private static final String API_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    private static int no = 0;
    private ListView listView;
    private ConnectivityManager connectivity_manager;
    private NetworkInfo network_info;
    private BookAdapter bAdapter;
    private ProgressBar progress_Bar;
    private TextView connectionTextView;
    private String url;
    private EditText edit_text;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            if (getLoaderManager().getLoader(BOOK_LOADER_ID) != null) {
                getLoaderManager().initLoader(BOOK_LOADER_ID, null, this);
            }
        }
        progress_Bar = findViewById(R.id.loading_indicator);
        progress_Bar.setVisibility(View.GONE);
        connectionTextView = findViewById(R.id.empty_view);
        connectionTextView.setVisibility(View.GONE);
        listView = findViewById(R.id.list);
        bAdapter = new BookAdapter(this, new ArrayList<Book>());
        listView.setAdapter(bAdapter);
        final LoaderManager loaderManager = getLoaderManager();
        Button button = findViewById(R.id.search);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectivity_manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                network_info = connectivity_manager.getActiveNetworkInfo();
                if (network_info != null && network_info.isConnected()) {
                    progress_Bar.setVisibility(View.VISIBLE);
                    connectionTextView.setVisibility(View.GONE);
                    listView.setVisibility(View.GONE);
                    edit_text = findViewById(R.id.editText);
                    url = edit_text.getText().toString();
                    url = API_URL + url;
                    url = url.trim().replace(" ", "");
                    Log.e(LOG_TAG, url);
                    if (no == 0) {
                        loaderManager.initLoader(BOOK_LOADER_ID, null, MainActivity.this);
                        no++;
                    } else {
                        loaderManager.restartLoader(BOOK_LOADER_ID, null, MainActivity.this);
                        no++;
                    }
                } else {
                    listView.setVisibility(View.GONE);
                    connectionTextView.setVisibility(View.VISIBLE);
                    connectionTextView.setText(R.string.no_connection);
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Book currentItem = bAdapter.getItem(i);
                Uri bookUri = Uri.parse(currentItem.getUrl());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);
                startActivity(websiteIntent);
            }
        });
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        return new BookLoader(this, url);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
        bAdapter.clear();
        progress_Bar.setVisibility(View.GONE);
        if (books != null && !books.isEmpty()) {
            listView.setVisibility(View.VISIBLE);
            bAdapter.addAll(books);
            listView.setSelection(0);
        } else {
            connectionTextView.setVisibility(View.VISIBLE);
            connectionTextView.setText(R.string.no_books);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        bAdapter.clear();
    }

    private static class BookLoader extends AsyncTaskLoader<List<Book>> {
        String Url;

        public BookLoader(Context context, String url) {
            super(context);
            Url = url;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public List<Book> loadInBackground() {
            if (Url == null) {
                return null;
            }

            List<Book> result = Utils.fetchBookData(Url);
            return result;
        }
    }
}
