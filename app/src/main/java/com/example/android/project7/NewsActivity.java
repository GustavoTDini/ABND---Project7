package com.example.android.project7;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
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
    /** Adapter da lista de earthquakes */
    private NewsArrayAdapter mAdapter;
    /** TextView que é mostrada quando ha um erro */
    private TextView mErrorTextView;
    /** ProgressBar que é mostrada enquanto conecção é realizada*/
    private ProgressBar mLoadingBar;
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

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Find a reference to the {@link ListView} in the layout
        ListView newsListView = findViewById(R.id.list);

        createNewsList(newsListView);

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                News currentArticle = mAdapter.getItem(position);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentArticle.getNewsUrl()));
                startActivity(browserIntent);
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

        if (id == R.id.nav_world) {
            // Handle the camera action
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {

        String urlQuery = NewsAppUtilities.createUrlFromQueries(mSection,mFromDate,mToDate,mOrderBy,mPageSize,mSearch);

        return new NewsListLoader(this, urlQuery);
    }

    public void onLoadFinished(Loader<List<News>> loader, List<News> earthquakes) {
        // Seta o texto de estado vazio para mostrar que não foi encontrado resultados na busca
        mErrorTextView.setText(R.string.empty_return_list);

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

        mLoadingBar = findViewById(R.id.loading_spinner);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsListView.setAdapter(mAdapter);

        mErrorTextView = findViewById(R.id.alert_message);
        newsListView.setEmptyView(mErrorTextView);

        // Obtém uma referência ao LoaderManager, a fim de interagir com loaders.
        LoaderManager loaderManager = getLoaderManager();

        // Inicializa o loader. Passa um ID constante int definido acima e passa nulo para
        // o bundle. Passa esta activity para o parâmetro LoaderCallbacks (que é válido
        // porque esta activity implementa a interface LoaderCallbacks).
        loaderManager.initLoader(NEWS_LOADER_ID, null, this);

    }


}
