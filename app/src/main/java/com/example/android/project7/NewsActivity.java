package com.example.android.project7;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<News>>, NavigationView.OnNavigationItemSelectedListener {

    /**
     * Valor constante para o ID do loader das News
     */
    private static final int NEWS_LOADER_ID = 1;
    /**
     * Valor constante e vázio para definir um query vazio
     */
    private static final String EMPTY_QUERY = "";
    /**
     * Valor de 4 que define a altura da divisoria da lista
     */
    private static final int DIVIDER_HEIGHT = 4;
    /**
     * Valor de int arbitrario que corresponde a inicialização do loader para o metodo testConnection
     */
    private static final int LOADER_INIT = 12345;
    /**
     * Valor de int arbitrario que corresponde ao restart do loader para o metodo testConnection
     */
    private static final int LOADER_RESTART = 123456;
    /**
     * String com a url da pagina do the guardian
     */
    private static final String GUARDIAN_WEB_HTTP = "https://www.theguardian.com/international";
    /**
     * Array com os varios sections disponiveis no aplicativo, para ser usado no query, quando acessado o navdrawer
     */
    private final String[] sections = {"world", "uk-news", "business", "politics", "environment", "education", "science", "technology", "global", "commentisfree", "football", "sport", "film",
            "music", "tv-and-radio", "books", "artanddesign", "games", "lifeandstyle", "fashion", "travel", "money", "society"};
    /**
     * ListView da lista de News
     */
    private ListView newsListView;
    /**
     * Adapter da lista de News
     */
    private NewsArrayAdapter mAdapter;
    /**
     * TextView que é mostrada quando ha um erro
     */
    private TextView mErrorTextView;
    /**
     * ProgressBar que é mostrada enquanto a conexão é realizada
     */
    private ProgressBar mLoadingBar;
    /**
     * Criação das variaveis que irão criar a url de query das noticias
     */
    private String mSection = EMPTY_QUERY;
    private String mFromDate = EMPTY_QUERY;
    private String mToDate = EMPTY_QUERY;
    private String mOrderBy = EMPTY_QUERY;
    private String mPageSize = EMPTY_QUERY;
    private String mSearch = "news";
    private int mHighlightColorId = R.color.colorPrimaryDark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // inicialização e Criação do Drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // inicialização e Criação do NavView
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // inicialização do LoadingSpinner
        mLoadingBar = findViewById(R.id.loading_spinner);


        // Modifica a Cor da varias section Groups da Drawer para manter o padrão do site do the Guardian
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
        newsListView = findViewById(R.id.list);

        // Criação da lista de News
        createNewsList(newsListView);

        //Definição do ação de clicar em uma noticia, abrindo o Browser na pagina da noticia no Site do the Guardian
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                News currentArticle = mAdapter.getItem(position);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentArticle.getNewsUrl()));
                startActivity(browserIntent);
            }
        });


        //Inicialização e Definição do ação de clicar no botão "Powered by the Guardian", abrindo o Browser na pagina inicial do Site do the Guardian
        CheckableImageButton poweredByTheGuardian = findViewById(R.id.powered_by_the_guardian_button);
        poweredByTheGuardian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(GUARDIAN_WEB_HTTP));
                startActivity(browserIntent);
            }
        });

        // Inicialização do SearchView e definicção da ação de submeter a procura pelo Método doMySearch
        SearchView newsSearch = findViewById(R.id.menu_search);
        newsSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                doMySearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }


    /**
     * Metodo onResume, como poderá ser retornado pelo back Button da Settings Page, coloquei a inicialização das preferencias,
     * e o init Loader neste metodo Overide, que pela LifeCycle da Activity, reduz o numero de inicializações
     */
    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // definições e atribuições das preferencias
        mOrderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        mPageSize = sharedPrefs.getString(
                getString(R.string.settings_list_size_key),
                getString(R.string.settings_list_size_default));

        mFromDate = sharedPrefs.getString(
                getString(R.string.settings_from_date_key),
                getString(R.string.settings_from_date_default));

        mToDate = sharedPrefs.getString(
                getString(R.string.settings_to_date_key),
                getString(R.string.settings_to_date_default));

        // testa se a data toDate é maior que fromDate, caso seja, define a toDate em vazio e mostra um alerta com um toast
        if (NewsAppUtilities.compareDates(mFromDate, mToDate)) {
            Toast.makeText(this, getResources().getString(R.string.toast_date_message), Toast.LENGTH_SHORT).show();
            mToDate = EMPTY_QUERY;
        }

        // inicializa a conexão com o loader após fazer o teste de conexão ativa
        connectionTest(LOADER_INIT);
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
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, PreferencesActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // define a Id da navigation item selecionada
        int id = item.getItemId();

        //Testa o Id e para cada Section, inicia o metodo navDrawerSectionSelection, que recebe o numero relativa a array de sections e a cor correspoendente, para iniciar uma nova query

        switch (id) {
            case R.id.nav_world:
                navDrawerSectionSelection(0, R.color.newsAccentDark);
                break;
            case R.id.nav_uk_news:
                navDrawerSectionSelection(1, R.color.newsAccentDark);
                break;
            case R.id.nav_business:
                navDrawerSectionSelection(2, R.color.newsAccentDark);
                break;
            case R.id.nav_politcs:
                navDrawerSectionSelection(3, R.color.newsAccentDark);
                break;
            case R.id.nav_environment:
                navDrawerSectionSelection(4, R.color.newsAccentDark);
                break;
            case R.id.nav_education:
                navDrawerSectionSelection(5, R.color.newsAccentDark);
                break;
            case R.id.nav_science:
                navDrawerSectionSelection(6, R.color.newsAccentDark);
                break;
            case R.id.nav_tech:
                navDrawerSectionSelection(7, R.color.newsAccentDark);
                break;
            case R.id.nav_global:
                navDrawerSectionSelection(8, R.color.newsAccentDark);
                break;
            case R.id.nav_the_guardian_view:
                navDrawerSectionSelection(9, R.color.opinionsAccentDark);
                break;
            case R.id.nav_soccer:
                navDrawerSectionSelection(10, R.color.sportAccentDark);
                break;
            case R.id.nav_other_sports:
                navDrawerSectionSelection(11, R.color.sportAccentDark);
                break;
            case R.id.nav_film:
                navDrawerSectionSelection(12, R.color.cultureAccentDark);
                break;
            case R.id.nav_music:
                navDrawerSectionSelection(13, R.color.cultureAccentDark);
                break;
            case R.id.nav_tv_and_radio:
                navDrawerSectionSelection(14, R.color.cultureAccentDark);
                break;
            case R.id.nav_books:
                navDrawerSectionSelection(15, R.color.cultureAccentDark);
                break;
            case R.id.nav_art_and_design:
                navDrawerSectionSelection(16, R.color.cultureAccentDark);
                break;
            case R.id.nav_games:
                navDrawerSectionSelection(17, R.color.cultureAccentDark);
                break;
            case R.id.nav_life_style:
                navDrawerSectionSelection(18, R.color.lifestyleAccentDark);
                break;
            case R.id.nav_fashion:
                navDrawerSectionSelection(19, R.color.lifestyleAccentDark);
                break;
            case R.id.nav_travel:
                navDrawerSectionSelection(20, R.color.lifestyleAccentDark);
                break;
            case R.id.nav_money:
                navDrawerSectionSelection(21, R.color.lifestyleAccentDark);
                break;
            case R.id.nav_society:
                navDrawerSectionSelection(22, R.color.lifestyleAccentDark);
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        // esvazia o texto de erro
        mErrorTextView.clearComposingText();
        mErrorTextView.setVisibility(View.GONE);

        // mantem o loading bar aparecendo enquanto estiver carregando os dados
        mLoadingBar.setVisibility(View.VISIBLE);

        // cria a url do query a partir do metodo createUrlFromQueries que se encontra no NewsAppUtilities, utiliza todas as Strings
        // de query para criar a URL
        String urlQuery = NewsAppUtilities.createUrlFromQueries(mSection, mFromDate, mToDate, mOrderBy, mPageSize, mSearch);

        // retorna a lista com o query selecionado
        return new NewsListLoader(this, urlQuery);
    }

    public void onLoadFinished(Loader<List<News>> loader, List<News> newsList) {
        // Seta o texto de estado vazio para mostrar que não foi encontrado resultados na busca
        mErrorTextView.setText(R.string.empty_return_list);
        mErrorTextView.setVisibility(View.VISIBLE);

        // Limpa o adapter de dados de News anteriores
        mAdapter.clear();

        // esconde o LoadingBar
        mLoadingBar.setVisibility(View.GONE);

        // Se há uma lista válida de {@link News}s, então os adiciona ao data set do adapter.
        // Isto ativará a atualização da ListView.
        if (newsList != null && !newsList.isEmpty()) {
            mAdapter.addAll(newsList);
        }
    }

    public void onLoaderReset(Loader<List<News>> loader) {
        // Reseta o Loader, então podemos limpar nossos dados existentes.
        mAdapter.clear();
    }

    /**
     * Metodo para criar a lista das noticias, tem como entrada a ListView que irá recebe-la
     */
    private void createNewsList(ListView newsListView) {

        // esvazia a lista
        newsListView.clearChoices();

        // define a cor dos separadores baseado no tipo de noticia
        ColorDrawable colorId = new ColorDrawable(getResources().getColor(mHighlightColorId));

        newsListView.setDivider(colorId);
        newsListView.setDividerHeight(DIVIDER_HEIGHT);

        // Create a new {@link ArrayAdapter} of News
        mAdapter = new NewsArrayAdapter(this, new ArrayList<News>(), mHighlightColorId);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsListView.setAdapter(mAdapter);

        mErrorTextView = findViewById(R.id.alert_message);
        newsListView.setEmptyView(mErrorTextView);

    }

    /**
     * Metodo para criar a lista a partir do NavDrawer, ele pega a section pelo array, pega a cor da section, cria uma nova listView,
     * esvazia a mSearch, define o mSection e a cor de detalhes conforme a escolha e cria uma nova lista com essas informações
     */
    private void navDrawerSectionSelection(int selectedSection, int sectionColor) {
        newsListView = findViewById(R.id.list);
        mSearch = EMPTY_QUERY;
        mSection = sections[selectedSection];
        mHighlightColorId = sectionColor;
        createNewsList(newsListView);
        connectionTest(LOADER_RESTART);
    }

    /**
     * Metodo para criar a lista a partir do search, ele coloca na mSearch o texto do searchView, cria uma nova listView,
     * define a cor de detalhes conforme para padrão do App  e cria uma nova lista com essas informações
     */
    private void doMySearch(String query) {
        newsListView = findViewById(R.id.list);
        mSearch = query;
        mHighlightColorId = R.color.colorPrimaryDark;
        createNewsList(newsListView);
        connectionTest(LOADER_RESTART);
    }

    /**
     * Metodo para definir programaticamente a cor dos section groups do nav drawer, usando o SpannableString e aplicando o Stilo confome cada section
     */
    private void navSectionStyles(MenuItem menu, int colorStyle) {
        SpannableString style = new SpannableString(menu.getTitle());
        style.setSpan(new TextAppearanceSpan(this, colorStyle), 0, style.length(), 0);
        menu.setTitle(style);
    }

    /**
     * Metodo para Testar a conexão antes de iniciar o loader, caso não estejá com uma comexão ativa a mensagem de erro avisará
     */
    private void connectionTest(int loaderType) {
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
