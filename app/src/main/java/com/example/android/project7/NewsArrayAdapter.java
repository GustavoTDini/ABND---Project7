package com.example.android.project7;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.List;


/**
 * Clase que cria a lista para mostrar as noticias,
 */

public class NewsArrayAdapter extends ArrayAdapter<News>  {

    private static final int TYPE_LIST_ITEM = 0;
    private static final int TYPE_MAIN_STORY = 1;

    public NewsArrayAdapter(Activity context, List<News> newsList) {
        super( context, 0, newsList );
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View newsArticleView = convertView;
        ViewHolder holder = new ViewHolder( newsArticleView );
        int viewType = getItemViewType( position );

        if (newsArticleView == null){
            switch (viewType){
                case TYPE_LIST_ITEM:
                    newsArticleView = LayoutInflater.from(getContext()).inflate( R.layout.news_list_item, parent, false);
                    holder = new ViewHolder( newsArticleView );
                    newsArticleView.setTag( holder );

                    break;
                case TYPE_MAIN_STORY:
                    newsArticleView = LayoutInflater.from(getContext()).inflate( R.layout.main_story_layout, parent, false);
                    holder = new ViewHolder( newsArticleView );
                    newsArticleView.setTag( holder );
                    break;
            }

        } else{
            holder = (ViewHolder) newsArticleView.getTag();
        }

        News newsArticle = getItem( position );
        assert newsArticle != null;

        holder.title.setText( newsArticle.getNewsTitle());
        holder.section.setText( newsArticle.getNewsSection());
        new AsyncDownloadImage(holder.thumbnail).execute(newsArticle.getmNewsThumbnailUrl());

        return newsArticleView;
    }

    public class ViewHolder {

        final TextView title;
        final TextView section;
        final ImageView thumbnail;

        public ViewHolder(View view) {
            title = view.findViewById(R.id.news_list_title);
            section = view.findViewById(R.id.news_list_section);
            thumbnail = view.findViewById( R.id.news_list_thumbnail );
        }

    }

    private class AsyncDownloadImage extends AsyncTask<String, Void, Bitmap> {

        ImageView bmpImage;

        public AsyncDownloadImage(ImageView bmImage) {
            this.bmpImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            Bitmap thumbNailImage = NewsAppUtilities.decodeThumbnailUrl( urls[0] );
            return thumbNailImage;

        }
        protected void onPostExecute(Bitmap result) {
            bmpImage.setImageBitmap(result);
        }
    }



//    private static final int TYPE_LIST_ITEM = 0;
//    private static final int TYPE_MAIN_STORY = 1;
//
//    private LayoutInflater mInflater;
//
//    public enum RowType {
//        TOP_STORY, LIST_ITEM
//    }
//
//    private NewsArrayAdapter(Context context, List <News> news) {
//        super( context, 0, news );
//        mInflater = LayoutInflater.from( context );
//    }
//
//    @Override
//    public int getViewTypeCount() {
//        return RowType.values().length;
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        return (position == 0? TYPE_MAIN_STORY : TYPE_LIST_ITEM);
//    }
//
//    public View getView(int position, View convertView, ViewGroup parent) {
//        ViewHolder holder;
//        int rowType = getItemViewType( position );
//        View View;
//        if (convertView == null) {
//            holder = new ViewHolder();
//            switch (rowType) {
//                case TYPE_MAIN_STORY:
//                    convertView = mInflater.inflate( R.layout.main_story_layout, null );
//                    holder.View = getItem( position ).getView( mInflater, convertView );
//                    break;
//                case TYPE_LIST_ITEM:
//                    convertView = mInflater.inflate( R.layout.news_list_item, null );
//                    holder.View = getItem( position ).getView( mInflater, convertView );
//                    break;
//            }
//            convertView.setTag( holder );
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//        }
//        return convertView;
//    }
//
//
//
//    public static class ViewHolder {
//        public View View;
//    }


}