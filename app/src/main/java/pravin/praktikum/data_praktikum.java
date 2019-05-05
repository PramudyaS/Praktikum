package pravin.praktikum;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class data_praktikum extends AppCompatActivity implements SearchView.OnQueryTextListener {

    public static final String URL = "http://192.168.1.124:81/Android/Belajar1/";
    private List<Result> results = new ArrayList<>();
    private RecyclerViewAdapter viewAdapter;

    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.progress_bar) ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_praktikum);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewAdapter = new RecyclerViewAdapter(this,results);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(viewAdapter);

        loadDataMahasiswa();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDataMahasiswa();
    }

    private void loadDataMahasiswa() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL).addConverterFactory(GsonConverterFactory.create()).build();
        RegisterAPI api = retrofit.create(RegisterAPI.class);
        Call<Value> call = api.view();
        call.enqueue(new Callback<Value>() {
            @Override
            public void onResponse(Call<Value> call, Response<Value> response) {
                String value = response.body().getValue();
                progressBar.setVisibility(View.GONE);
                if (value.equals("1")){
                    try{
                    results = response.body().getResult();
                    viewAdapter = new RecyclerViewAdapter(data_praktikum.this,results);
                    recyclerView.setAdapter(viewAdapter);
                    }catch (Exception e){
                        e.getMessage();
                    }
                }
            }

            @Override
            public void onFailure(Call<Value> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
       recyclerView.setVisibility(View.GONE);
       progressBar.setVisibility(View.VISIBLE);
       Retrofit retrofit = new Retrofit.Builder().baseUrl(URL)
               .addConverterFactory(GsonConverterFactory.create()).build();
       RegisterAPI api = retrofit.create(RegisterAPI.class);
       Call<Value> call = api.search(newText);
       call.enqueue(new Callback<Value>() {
           @Override
           public void onResponse(Call<Value> call, Response<Value> response) {
               String value = response.body().getValue();
               progressBar.setVisibility(View.GONE);
               recyclerView.setVisibility(View.VISIBLE);
               if (value.equals("1")){
                   results = response.body().getResult();
                   viewAdapter = new RecyclerViewAdapter(data_praktikum.this,results);
                   recyclerView.setAdapter(viewAdapter);
               }
           }


           @Override
           public void onFailure(Call<Value> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
           }
       });
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search,menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView search = (SearchView) MenuItemCompat.getActionView(item);
        search.setQueryHint("Cari Nama Mahasiswa");
        search.setIconified(false);
        search.setOnQueryTextListener(this);
        return true;
    }
}
