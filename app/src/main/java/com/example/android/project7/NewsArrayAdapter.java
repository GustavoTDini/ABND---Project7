package com.example.android.project7;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import com.example.android.project7.databinding.NewsListItemBinding;

/**
 * Created by SSJdini on 24/03/18.
 */

public class NewsArrayAdapter extends ArrayAdapter {

    private NewsArrayAdapter(Activity context, ArrayList<News> news){
        super(context, 0, news);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        News currentNews = (News) getItem( position );
        assert currentNews != null;

        NewsListItemBinding binding = DataBindingUtil.inflate( LayoutInflater.from( getContext() ), R.layout.news_list_item, parent, false);

        binding.setNews(currentNews);

        binding.executePendingBindings();

        return binding.getRoot();
    }
}
