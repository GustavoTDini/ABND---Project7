package com.example.android.project7;

/**
 * Classe que irá receber os dados do json das materias da API do the guardian e guarda as informações que irão ser exibidas no layout
 * do app, apresenta 6 variaveis do tipo string, titulo, section, url e thumbnailurl, data e trailText
 */

class News {

    /**
     * String que contem o titulo da materia
     */
    private final String mNewsTitle;

    /**
     * String que contem a section relativo a materia
     */
    private final String mNewsSection;

    /**
     * String que contem a URL do site do the guardian da  materia
     */
    private final String mNewsUrl;

    /**
     * String que contem a imagem de thumbnail
     */
    private final String mNewsThumbnailUrl;

    /**
     * String que contem a data do artigo,
     */
    private final String mNewsDate;

    /**
     * String que contem a trailText
     */
    private final String mNewsTrailText;

    public News(String newsTitle, String newsSection, String newsUrl, String newsThumbnailUrl, String newsDate, String newsTrailText) {
        this.mNewsTitle = newsTitle;
        this.mNewsSection = newsSection;
        this.mNewsUrl = newsUrl;
        this.mNewsThumbnailUrl = newsThumbnailUrl;
        this.mNewsDate = newsDate;
        this.mNewsTrailText = newsTrailText;
    }

    public String getNewsTitle() {
        return mNewsTitle;
    }

    public String getNewsSection() {
        return mNewsSection;
    }

    public String getNewsUrl() {
        return mNewsUrl;
    }

    public String getmNewsThumbnailUrl() {
        return mNewsThumbnailUrl;
    }

    public String getNewsDate() {
        return mNewsDate;
    }

    public String getNewsTrailText() {
        return mNewsTrailText;
    }

}
