package pravin.praktikum;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {

    public static final String URL = "http://192.168.1.124:81/Android/Belajar1/";
    private RadioButton radiSesButton;
    private ProgressDialog progress;

    @BindView(R.id.radioSesi) RadioGroup radioGroup;
    @BindView(R.id.editTextNPM) EditText editTextNPM;
    @BindView(R.id.editTextNama) EditText editTextNama;
    @BindView(R.id.ediTextKelas) EditText ediTextKelas;


    @OnClick(R.id.btnLihat) void lihat(){
        Intent intent = new Intent(MainActivity.this,data_praktikum.class);
        startActivity(intent);
    }
    @OnClick(R.id.btnDaftar) void daftar(){
        progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setMessage("Loading......");
        progress.show();

        String npm = editTextNPM.getText().toString();
        String nama = editTextNama.getText().toString();
        String kelas  = ediTextKelas.getText().toString();
        int selectedId = radioGroup.getCheckedRadioButtonId();

        radiSesButton = (RadioButton) findViewById(selectedId);
        String sesi = radiSesButton.getText().toString();

        if(npm.length() <= 7){
            progress.dismiss();
            Toast.makeText(getApplicationContext(),"NPM Tidak Boleh Kurang Dari 7",Toast.LENGTH_SHORT).show();
        }else {

        if (npm.isEmpty() || nama.isEmpty() || kelas.isEmpty()){
            progress.dismiss();
            Toast.makeText(getApplicationContext(),"Form Tidak Boleh Kosong",Toast.LENGTH_SHORT).show();
        }else{
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL).addConverterFactory(GsonConverterFactory.create()).build();

        RegisterAPI api = retrofit.create(RegisterAPI.class);
        Call<Value> call = api.daftar(npm,nama,kelas,sesi);
        call.enqueue(new Callback<Value>() {
            @Override
            public void onResponse(Call<Value> call, Response<Value> response) {
                String value = response.body().getMessage();
                String message = response.body().getMessage();
                progress.dismiss();
                if (value.equals("1")){
                    Toast.makeText(MainActivity.this,message,Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this,message,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Value> call, Throwable t) {
                progress.dismiss();
                Toast.makeText(MainActivity.this,"Connection Lost", Toast.LENGTH_SHORT).show();
            }
        });
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }
}
