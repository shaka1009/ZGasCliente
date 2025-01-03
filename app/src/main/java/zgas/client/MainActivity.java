package zgas.client;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import zgas.client.models.Client;
import zgas.client.models.Price;
import zgas.client.providers.AuthProvider;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    public static Activity activity;

    private AuthProvider mAuthProvider;


    int dep = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.AppTheme_Splash);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;
        mAuthProvider = new AuthProvider();

        Home.mClient = new Client();
        Home.mPrice = new Price();
        Home.mDirecciones = new ArrayList<>();

        if(dep==1)
        {
            Intent intent = new Intent(MainActivity.this, HomeAcerca.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mAuthProvider.existSession())
        {
            if(dep==1)
                return;
            //load_data();
            Intent intent = new Intent(MainActivity.this, Home.class);
            startActivity(intent);
        }
        ///* EN PRUEBA, NO QUITAR
        else
        {
            if(dep==1)
                return;
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
        }
        //

    }




    @Override
    public void onBackPressed() {}
}