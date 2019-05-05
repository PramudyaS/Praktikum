package pravin.praktikum;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class update_data_praktikum extends AppCompatActivity {

    private final String URL = "http://192.168.1.124:81/Android/Belajar1/";
    private RadioButton radioSesButton;
    private ProgressDialog progress;

    @BindView(R.id.radioPagi) RadioButton radioButtonPagi;
    @BindView(R.id.radioSiang) RadioButton radioButtonSiang;
    @BindView(R.id.radioSesi) RadioGroup radioGroup;
    @BindView(R.id.editTextNPM) EditText editTextNPM;
    @BindView(R.id.editTextNama) EditText editTextNama;
    @BindView(R.id.ediTextKelas) EditText editTextKelas;

    @OnClick(R.id.btnUpdate) void ubah(){
        progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setMessage("Loading....");
        progress.show();


        String npm = editTextNPM.getText().toString();
        String nama = editTextNama.getText().toString();
        String kelas = editTextKelas.getText().toString();

        int selectedId = radioGroup.getCheckedRadioButtonId();

        radioSesButton = (RadioButton) findViewById(selectedId);
        String sesi = radioSesButton.getText().toString();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RegisterAPI api = retrofit.create(RegisterAPI.class);
        Call<Value> call = api.update(npm,nama,kelas,sesi);
        call.enqueue(new Callback<Value>() {
            @Override
            public void onResponse(Call<Value> call, Response<Value> response) {
                try{
                    String value = response.body().getValue();
                    String message = response.body().getMessage();
                    progress.dismiss();
                    if (value.equals("1")){
                        Toast.makeText(update_data_praktikum.this,message,Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        Toast.makeText(update_data_praktikum.this, message, Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.getMessage();
                }
            }

            @Override
            public void onFailure(Call<Value> call, Throwable t) {
                t.printStackTrace();
                progress.dismiss();
                Toast.makeText(update_data_praktikum.this,"Lost Connection",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_data_praktikum);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String npm = intent.getStringExtra("npm");
        String nama = intent.getStringExtra("nama");
        String kelas = intent.getStringExtra("kelas");
        String sesi = intent.getStringExtra("sesi");

        editTextNPM.setText(npm);
        editTextNama.setText(nama);
        editTextKelas.setText(kelas);


        if (sesi.equals("Pagi")){
            radioButtonPagi.setChecked(true);
        }else{
            radioButtonSiang.setChecked(true);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;

            case R.id.action_delete:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Caution");
                alertDialogBuilder.setMessage("Apakah Anda Yakin Menghapus Data Ini ?").setCancelable(false)
                        .setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String npm = editTextNPM.getText().toString();
                                Retrofit retrofit = new Retrofit.Builder().baseUrl(URL)
                                        .addConverterFactory(GsonConverterFactory.create()).build();
                                RegisterAPI api = retrofit.create(RegisterAPI.class);
                                Call<Value> call = api.hapus(npm);
                                call.enqueue(new Callback<Value>() {
                                    @Override
                                    public void onResponse(Call<Value> call, Response<Value> response) {
                                        String value = response.body().getValue();
                                        String message = response.body().getMessage();
                                        if (value.equals("1")){
                                            Toast.makeText(update_data_praktikum.this,message,Toast.LENGTH_SHORT).show();
                                            finish();
                                        }else{
                                            Toast.makeText(update_data_praktikum.this,message,Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Value> call, Throwable t) {
                                        t.printStackTrace();
                                        Toast.makeText(update_data_praktikum.this,"Connection Lost",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete,menu);
        return true;
    }
}
