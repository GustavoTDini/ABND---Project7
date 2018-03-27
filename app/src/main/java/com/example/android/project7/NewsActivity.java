package com.example.android.project7;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CheckableImageButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<News>>, NavigationView.OnNavigationItemSelectedListener{

    /**
     * Valor constante para o ID do loader das News
     */
    private static final int NEWS_LOADER_ID = 1;
    private static final String EMPTY_QUERY = "";
    private static final int DIVIDER_HEIGHT = 4;
    private static final int LOADER_INIT = 12345;
    private static final int LOADER_RESTART = 123456;
    private static final String GUARDIAN_WEB_HTTP = "https://www.theguardian.com/international";
    /** Adapter da lista de earthquakes */
    private NewsArrayAdapter mAdapter;
    /** TextView que é mostrada quando ha um erro */
    private TextView mErrorTextView;
    /**
     * ProgressBar que é mostrada enquanto a conexão é realizada
     */
    private ProgressBar mLoadingBar;
    private String mSection = EMPTY_QUERY;
    private String mFromDate = EMPTY_QUERY;
    private String mToDate = EMPTY_QUERY;
    private String mOrderBy = EMPTY_QUERY;
    private String mPageSize = EMPTY_QUERY;
    private String mSearch = "news";
    private int mHighlightColorId = R.color.colorPrimaryDark;

    private String[] sections = {"world", "uk-news", "business", "politics", "environment", "education", "science", "technology", "global", "commentisfree", "football", "sport", "film",
            "music", "tv-and-radio", "books", "artanddesign", "games", "lifeandstyle", "fashion", "travel", "money", "society"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mLoadingBar = findViewById(R.id.loading_spinner);

        MenuItem newsMenuItem = navigationView.getMenu().findItem(R.id.news_nav_section);
        navSectionStyles(newsMenuItem, R.style.NavNewsGroup);

        MenuItem opinionMenuItem = navigationView.getMenu().findItem(R.id.opinion_nav_section);
        navSectionStyles(opinionMenuItem, R.style.NavOpinionGroup);

        MenuItem sportMenuItem = navigationView.getMenu().findItem(R.id.sport_nav_section);
        navSectionStyles(sportMenuItem, R.style.NavSportGroup);

        MenuItem cultureMenuItem = navigationView.getMenu().findItem(R.id.culture_nav_section);
        navSectionStyles(cultureMenuItem, R.style.NavCultureGroup);

        MenuItem lifestyleMenuItem = navigationView.getMenu().findItem(R.id.lifestyle_nav_section);
        navSectionStyles(lifestyleMenuItem, R.style.NavLifeStyleGroup);

        // Find a reference to the {@link ListView} in the layout
        ListView newsListView = findViewById(R.id.list);

        createNewsList(newsListView);

        connectionTest(LOADER_INIT);

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                News currentArticle = mAdapter.getItem(position);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentArticle.getNewsUrl()));
                startActivity(browserIntent);
            }
        });


        CheckableImageButton poweredByTheGuardian = findViewById(R.id.powered_by_the_guardian_button);
        poweredByTheGuardian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(GUARDIAN_WEB_HTTP));
                startActivity(browserIntent);
            }
        });

        SearchView newsSearch = findViewById(R.id.menu_search); // inititate a search view
        newsSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                doMySearch(query, R.color.colorPrimaryDark);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.main, menu );

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_order) {
            return true;
        }

        return super.onOptionsItemSelected( item );
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // Find a reference to the {@link ListView} in the layout
        ListView newsListView = findViewById(R.id.list);


        switch (id) {
            case R.id.nav_world:
                navDrawerSectionSelection(0, R.color.newsAccent);
                createNewsList(newsListView);
                connectionTest(LOADER_RESTART);
                break;
            case R.id.nav_uk_news:
                navDrawerSectionSelection(1, R.color.newsAccent);
                createNewsList(newsListView);
                connectionTest(LOADER_RESTART);
                break;
            case R.id.nav_business:
                navDrawerSectionSelection(2, R.color.newsAccent);
                createNewsList(newsListView);
                connectionTest(LOADER_RESTART);
                break;
            case R.id.nav_politcs:
                navDrawerSectionSelection(3, R.color.newsAccent);
                createNewsList(newsListView);
                connectionTest(LOADER_RESTART);
                break;
            case R.id.nav_environment:
                navDrawerSectionSelection(4, R.color.newsAccent);
                createNewsList(newsListView);
                connectionTest(LOADER_RESTART);
                break;
            case R.id.nav_education:
                navDrawerSectionSelection(5, R.color.newsAccent);
                createNewsList(newsListView);
                connectionTest(LOADER_RESTART);
                break;
            case R.id.nav_science:
                navDrawerSectionSelection(6, R.color.newsAccent);
                createNewsList(newsListView);
                connectionTest(LOADER_RESTART);
                break;
            case R.id.nav_tech:
                navDrawerSectionSelection(7, R.color.newsAccent);
                createNewsList(newsListView);
                connectionTest(LOADER_RESTART);
                break;
            case R.id.nav_global:
                navDrawerSectionSelection(8, R.color.newsAccent);
                createNewsList(newsListView);
                connectionTest(LOADER_RESTART);
                break;
            case R.id.nav_the_guardian_view:
                navDrawerSectionSelection(9, R.color.opinionsAccent);
                createNewsList(newsListView);
                connectionTest(LOADER_RESTART);
                break;
            case R.id.nav_soccer:
                navDrawerSectionSelection(10, R.color.sportAccent);
                createNewsList(newsListView);
                connectionTest(LOADER_RESTART);
                break;
            case R.id.nav_other_sports:
                navDrawerSectionSelection(11, R.color.sportAccent);
                createNewsList(newsListView);
                connectionTest(LOADER_RESTART);
                break;
            case R.id.nav_film:
                navDrawerSectionSelection(12, R.color.cultureAccent);
                createNewsList(newsListView);
                connectionTest(LOADER_RESTART);
                break;
            case R.id.nav_music:
                navDrawerSectionSelection(13, R.color.cultureAccent);
                createNewsList(newsListView);
                connectionTest(LOADER_RESTART);
                break;
            case R.id.nav_tv_and_radio:
                navDrawerSectionSelection(14, R.color.cultureAccent);
                createNewsList(newsListView);
                connectionTest(LOADER_RESTART);
                break;
            case R.id.nav_books:
                navDrawerSectionSelection(15, R.color.cultureAccent);
                createNewsList(newsListView);
                connectionTest(LOADER_RESTART);
                break;
            case R.id.nav_art_and_design:
                navDrawerSectionSelection(16, R.color.cultureAccent);
                createNewsList(newsListView);
                connectionTest(LOADER_RESTART);
                break;
            case R.id.nav_games:
                navDrawerSectionSelection(17, R.color.cultureAccent);
                createNewsList(newsListView);
                connectionTest(LOADER_RESTART);
                break;
            case R.id.nav_life_style:
                navDrawerSectionSelection(18, R.color.lifestyleAccent);
                createNewsList(newsListView);
                connectionTest(LOADER_RESTART);
                break;
            case R.id.nav_fashion:
                navDrawerSectionSelection(19, R.color.lifestyleAccent);
                createNewsList(newsListView);
                connectionTest(LOADER_RESTART);
                break;
            case R.id.nav_travel:
                navDrawerSectionSelection(20, R.color.lifestyleAccent);
                createNewsList(newsListView);
                connectionTest(LOADER_RESTART);
                break;
            case R.id.nav_money:
                navDrawerSectionSelection(21, R.color.lifestyleAccent);
                createNewsList(newsListView);
                connectionTest(LOADER_RESTART);
                break;
            case R.id.nav_society:
                navDrawerSectionSelection(22, R.color.lifestyleAccent);
                createNewsList(newsListView);
                connectionTest(LOADER_RESTART);
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        mErrorTextView.clearComposingText();
        mErrorTextView.setVisibility(View.GONE);

        mLoadingBar.setVisibility(View.VISIBLE);

        String urlQuery = NewsAppUtilities.createUrlFromQueries(mSection,mFromDate,mToDate,mOrderBy,mPageSize,mSearch);

        return new NewsListLoader(this, urlQuery);
    }

    public void onLoadFinished(Loader<List<News>> loader, List<News> earthquakes) {
        // Seta o texto de estado vazio para mostrar que não foi encontrado resultados na busca
        mErrorTextView.setText(R.string.empty_return_list);
        mErrorTextView.setVisibility(View.VISIBLE);

        // Limpa o adapter de dados de earthquake anteriores
        mAdapter.clear();

        mLoadingBar.setVisibility( View.GONE );

        // Se há uma lista válida de {@link Earthquake}s, então os adiciona ao data set do adapter.
        // Isto ativará a atualização da ListView.
        if (earthquakes != null && !earthquakes.isEmpty()) {
            mAdapter.addAll(earthquakes);
        }
    }

    public void onLoaderReset(Loader<List<News>> loader) {
        // Reseta o Loader, então podemos limpar nossos dados existentes.
        mAdapter.clear();
    }

    public void createNewsList(ListView newsListView) {

        newsListView.clearChoices();

        ColorDrawable colorId = new ColorDrawable(getResources().getColor(mHighlightColorId));

        newsListView.setDivider(colorId);
        newsListView.setDividerHeight(DIVIDER_HEIGHT);

        // Create a new {@link ArrayAdapter} of earthquakes
        mAdapter = new NewsArrayAdapter(this, new ArrayList<News>(), mHighlightColorId);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsListView.setAdapter(mAdapter);

        mErrorTextView = findViewById(R.id.alert_message);
        newsListView.setEmptyView(mErrorTextView);

    }

    public void navDrawerSectionSelection(int selectedSection, int sectionColor) {
        mSearch = EMPTY_QUERY;
        mSection = sections[selectedSection];
        mHighlightColorId = sectionColor;

    }

    public void doMySearch(String query, int sectionColor) {
        ListView newsListView = findViewById(R.id.list);
        LoaderManager loaderManager = getLoaderManager();
        mSearch = query;
        mHighlightColorId = sectionColor;
        createNewsList(newsListView);
        loaderManager.restartLoader(NEWS_LOADER_ID, null, this);

    }

    public void navSectionStyles(MenuItem menu, int colorStyle) {
        SpannableString style = new SpannableString(menu.getTitle());
        style.setSpan(new TextAppearanceSpan(this, colorStyle), 0, style.length(), 0);
        menu.setTitle(style);
    }

    public void connectionTest(int loaderType) {
        // Obtém uma referência ao ConectivityManager para testar a conexão
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            // Obtém uma referência ao LoaderManager, a fim de interagir com loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Inicializa o loader. Passa um ID constante int definido acima e passa nulo para
            // o bundle. Passa esta activity para o parâmetro LoaderCallbacks (que é válido
            // porque esta activity implementa a interface LoaderCallbacks).
            if (loaderType == LOADER_INIT) {
                loaderManager.initLoader(NEWS_LOADER_ID, null, this);
            } else if (loaderType == LOADER_RESTART) {
                loaderManager.restartLoader(NEWS_LOADER_ID, null, this);
            }

        } else {
            // Caso não haja conexão, a mensagem de erro aparecerá
            mLoadingBar.setVisibility(View.GONE);
            mErrorTextView.setText(R.string.no_internet_connection);
        }
    }

}
