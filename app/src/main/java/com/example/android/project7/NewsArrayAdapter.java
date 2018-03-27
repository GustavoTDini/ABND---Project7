package com.example.android.project7;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


/**
 * Classe que cria a lista para mostrar as noticias,
 */

public class NewsArrayAdapter extends ArrayAdapter<News>  {

    private static final int TYPE_LIST_ITEM = 0;
    private static final int TYPE_MAIN_STORY = 1;
    private static final int TYPE_COUNT = 2;

    private int mColorCode;

    public NewsArrayAdapter(Activity context, List<News> newsList, int textColorCode) {
        super( context, 0, newsList );
        mColorCode = textColorCode;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 ? TYPE_MAIN_STORY : TYPE_LIST_ITEM);
    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View newsArticleView = convertView;
        ViewHolder holder = null;
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

        int colorId = ContextCompat.getColor(getContext(), mColorCode);

        holder.title.setText( newsArticle.getNewsTitle());
        holder.title.setTextColor(colorId);
        holder.section.setText( newsArticle.getNewsSection());
        holder.section.setTextColor(colorId);
        holder.date.setText(newsArticle.getNewsDate());
        holder.trailText.setText(newsArticle.getNewsTrailText());
        new AsyncDownloadImage(holder.thumbnail).execute(newsArticle.getmNewsThumbnailUrl());

        return newsArticleView;
    }

    public static class AsyncDownloadImage extends AsyncTask<String, Void, Bitmap> {

        private ImageView bmpImage;

        private AsyncDownloadImage(ImageView bmImage) {
            this.bmpImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            return NewsAppUtilities.decodeThumbnailUrl(urls[0]);

        }

        protected void onPostExecute(Bitmap result) {
            bmpImage.setImageBitmap(result);
        }
    }

    public class ViewHolder {

        final TextView title;
        final TextView section;
        final ImageView thumbnail;
        final TextView date;
        final TextView trailText;

        public ViewHolder(View view) {
            title = view.findViewById(R.id.news_title);
            section = view.findViewById(R.id.news_section);
            thumbnail = view.findViewById(R.id.news_thumbnail);
            date = view.findViewById(R.id.news_date);
            trailText = view.findViewById(R.id.news_trail_text);
        }

    }

}