package com.example.android.project7;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe que irá manter os métodos auxiliares que irão facilitar o desenvolvimento e deixar o código mais limpo
 */

public final class NewsAppUtilities {

    private static final int URL_CONNECTION_GET_RESPONSE_CODE = 200;

    private static final int SET_READ_TIMEOUT_IN_MILISSECONDS = 10000;

    private static final int SET_CONNECT_TIMEOUT_IN_MILISSECONDS = 15000;

    private static final String GUARDIAN_API_REQUEST_URL = "http://content.guardianapis.com/search?";

    private static final String GUARDIAN_API_REQUEST_KEY = "test";

    private static final String GUARDIAN_API_REQUEST_FIELDS = "thumbnail,trailText";

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = NewsAppUtilities.class.getSimpleName();

    /** Construtor vazio para a classe*/
    public NewsAppUtilities() {
    }

    /**
     * Retorna um Objeto URL a partir de Uma String.
     */
    @Nullable
    private static URL createUrl(String stringUrl) {
        URL url;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
        return url;
    }

    /**
     * Faz Uma SolicitaçAo de Http e retorna uma String com a informação do JSon
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(SET_READ_TIMEOUT_IN_MILISSECONDS);
            urlConnection.setConnectTimeout(SET_CONNECT_TIMEOUT_IN_MILISSECONDS);
            urlConnection.connect();

            if (urlConnection.getResponseCode() == URL_CONNECTION_GET_RESPONSE_CODE) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "");
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Converte o  {@link InputStream} em uma String que contem a resposta JSON do servidor
     */
    @NonNull
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Retorna a lista dos {@link News} a partir do Json
     */
    @Nullable
    private static List<News> extractFeatureFromJson(String newsJson) {

        if (TextUtils.isEmpty(newsJson)) {
            return null;
        }

        // Cria uma nova lista vazia aonde iremos colocar os artigos obtidos pelo JSON
        List<News> newsArticles = new ArrayList<>();

        // Tenta Retirar as respostas do JSON, caso não consiga, por algum problema, o aplicativo irá jogar uma excessão e mostrar a
        // mensagem de Log, ao  inves de travar
        try {

            JSONObject root = new JSONObject(newsJson);

            JSONObject response = root.getJSONObject( "response" );

            JSONArray newsArticleArray = response.getJSONArray("results");

            int listSize = response.getInt( "pageSize" );

            for (int newsIndex = 0; newsIndex < listSize; newsIndex++) {

                JSONObject thisNewsArticle = newsArticleArray.getJSONObject(newsIndex);

                String newsTitle = thisNewsArticle.optString("webTitle");
                String newsSection = thisNewsArticle.optString("sectionName");
                String newsUrl = thisNewsArticle.optString("webUrl");
                String newsDatefromJson = thisNewsArticle.optString("webPublicationDate");
                String newsDate = newsDatefromJson.substring(0, 10);

                JSONObject fields = thisNewsArticle.getJSONObject( "fields" );
                String newsThumbnailUrl = fields.optString("thumbnail");
                String newsTrailTextHtml = fields.optString("trailText");

                String newsTrailText = Html.fromHtml(newsTrailTextHtml).toString();

                newsArticles.add(new News(newsTitle, newsSection, newsUrl, newsThumbnailUrl, newsDate, newsTrailText));
            }

        } catch (JSONException e) {
            // Se um erro for encontrado, ele irá jogar uma excessão e mostrar a mensagem abaixo
            // assim o app não irá travar
            Log.e(LOG_TAG, "Problem parsing the news JSON results", e);
        }

        // Returna a lista com os Artigos
        return newsArticles;
    }

    /**
     * Retorna a lista dos {@link News} que foram retirados do resposta JSON criado pela URL
     */
    static List<News> fetchNewsData(String requestURL) {

        URL urlRequest = createUrl(requestURL);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(urlRequest);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        return extractFeatureFromJson(jsonResponse);
    }

    /**
     * Método para decodificar a URL com a imagem de thumbail e retornar um bitmap utilizavel pelo android
     */
    @Nullable
    static Bitmap decodeThumbnailUrl(String thumbnailUrl) {

        if (thumbnailUrl == null) {
            return null;
        }

        try {
            URL codedUrl = createUrl(thumbnailUrl);
            HttpURLConnection imageUrlConnection = (HttpURLConnection) codedUrl.openConnection();
            imageUrlConnection.setRequestMethod("GET");
            imageUrlConnection.setReadTimeout(SET_READ_TIMEOUT_IN_MILISSECONDS);
            imageUrlConnection.setConnectTimeout(SET_CONNECT_TIMEOUT_IN_MILISSECONDS);
            imageUrlConnection.connect();

            if (imageUrlConnection.getResponseCode() == URL_CONNECTION_GET_RESPONSE_CODE) {
                InputStream stream = imageUrlConnection.getInputStream();
                return BitmapFactory.decodeStream(stream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + imageUrlConnection.getResponseCode());
                return null;
            }

        } catch (Exception e) {
            Log.e(LOG_TAG, "Problem decoding the thumbnail URL", e);
            return null;
        }
    }

    /**
     * Retorna a URL com os queries selecionados pelas opções no app
     */
    static String createUrlFromQueries(String section, String fromDate, String toDate, String orderBy, String pageSize, String search) {

        Uri baseUri = Uri.parse( GUARDIAN_API_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // testa se existe cada query, caso contrario não usa esse parâmetro
        if (!(section.isEmpty())){
            uriBuilder.appendQueryParameter("section", section);
        }

        if (!(fromDate.isEmpty())){
            uriBuilder.appendQueryParameter("from-date", fromDate);
        }

        if ((!toDate.isEmpty())){
            uriBuilder.appendQueryParameter("to-date", toDate);
        }

        if ((!orderBy.isEmpty())){
            uriBuilder.appendQueryParameter("order-by", orderBy);
        }

        // Query do thumbnail Field - sempre será presente
        uriBuilder.appendQueryParameter("show-fields", GUARDIAN_API_REQUEST_FIELDS);

        if ((!pageSize.isEmpty())){
            uriBuilder.appendQueryParameter("page-size", pageSize);
        }

        if ((!search.isEmpty())){
            uriBuilder.appendQueryParameter("q", search);
        }

        // Query do request Key - necessário para o funcionamento da API
        uriBuilder.appendQueryParameter("api-key", GUARDIAN_API_REQUEST_KEY );

        // retorna a URL com todas as queries
        Log.d("debug", "createUrlFromQueries: " + uriBuilder.toString());

        return uriBuilder.toString();
    }


}
