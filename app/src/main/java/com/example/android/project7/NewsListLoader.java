package com.example.android.project7;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Loader para carregar a lista de News em um segundo plano
 */

public class NewsListLoader extends AsyncTaskLoader<List<News>> {

    /**
     * URL da busca
     */
    private String mUrl;

    /**
     * Construtor do NewsListLoader
     */
    public NewsListLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    /**
     * Metodo que irá iniciar o load em segundo plano ao iniciar a activity
     */
    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * Metodo que realizará a busca em segundo plano do metodo
     */
    @Override
    public List<News> loadInBackground() {
        //Retorna Null se a URL for null
        if (mUrl == null) {
            return null;
        }

        // Realiza requisição de rede, decodifica a resposta, e extrai uma lista de livros.
        return NewsAppUtilities.fetchNewsData(mUrl);
    }
}
