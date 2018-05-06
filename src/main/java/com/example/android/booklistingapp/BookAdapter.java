package com.example.android.booklistingapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class BookAdapter extends ArrayAdapter<Book> {
    private static final String LOG_TAG = BookAdapter.class.getName();
    private static ImageView image_view;


    public BookAdapter(@NonNull Context context, List<Book> books) {
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }
        Book currentBook = getItem(position);
        image_view = (ImageView) convertView.findViewById(R.id.imageView);
        TextView title = (TextView) convertView.findViewById(R.id.book_title);
        TextView author = (TextView) convertView.findViewById(R.id.author);
        String url = currentBook.getImage();
        new ImageTask(image_view).execute(url);
        title.setText(currentBook.getTitle());
        author.setText(currentBook.getAuthor());
        return convertView;
    }

    private static class ImageTask extends AsyncTask<String, Void, Bitmap> {

        ImageView img;
        InputStream inputStream;

        public ImageTask(ImageView imageView) {
            img = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.connect();
                if (urlConnection.getResponseCode() != 200) {
                    return null;
                }
                inputStream = urlConnection.getInputStream();
                if (inputStream != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            img.setImageBitmap(bitmap);
        }
    }
}