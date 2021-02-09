package zgas.client;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import zgas.client.includes.Toolbar;

public class HomePedir extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    public static Activity activity;

    Button btnPedirCilindro;
    Button btnPedirEstacionario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_pedir);
        Toolbar.show(this, true);
        activity = this;

        declaration();
        listenner();
    }

    private void declaration()
    {
        btnPedirCilindro = findViewById(R.id.btnPedirCilindro);
        btnPedirEstacionario = findViewById(R.id.btnPedirEstacionario);
    }


    private boolean pressButton = false;
    private void listenner()
    {
        btnPedirCilindro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pressButton)
                    return;
                else pressButton = true;
                Intent i = new Intent(HomePedir.this , HomePedirCilindros.class);
                startActivity(i);
                SleepButton();
            }
        });

        /*
        btnPedirEstacionario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pressButton)
                    return;
                else pressButton = true;
                startActivity(new Intent(HomePedir.this , HomePedirEstacionario.class));
                SleepButton();
            }
        });*/
    }

    private void SleepButton()
    {
        new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            pressButton = false;
        }).start();
    }



    //BackPress

    @SuppressLint("RtlHardcoded")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            backPress();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        backPress();
    }

    private void backPress() {
        finish();
    }
    //BACK PRESS
}