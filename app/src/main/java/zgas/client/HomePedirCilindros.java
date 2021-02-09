package zgas.client;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import zgas.client.adapters.DireccionesAdapterSpinner;
import zgas.client.includes.Toolbar;
import zgas.client.models.Direcciones;

public class HomePedirCilindros extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    public static Activity activity;

    Spinner spinner;

    private TextView tvTotalPrice, tvPrecio30KG,tvPrecio20KG,tvPrecio10KG;

    private Button buttonMenos30kg, buttonMas30kg;
    private TextView tvCantidad30Kg;

    private Button buttonMenos20kg, buttonMas20kg;
    private TextView tvCantidad20Kg;

    private Button buttonMenos10kg, buttonMas10kg;
    private TextView tvCantidad10Kg;

    private Button btnPedir;
    private int posicion;


    int cantidad30kg, cantidad20kg, cantidad10kg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_pedir_cilindros);
        Toolbar.show(this, true);
        activity = this;

        declaration();
        initList();
        listenner();

        cantidad30kg=0; cantidad20kg=0; cantidad10kg=0;
    }

    @Override
    protected void onResume() {
        super.onResume();

        tvPrecio30KG.setText(Home.mPrice.getPrecio(Home.mDirecciones.get(posicion).getEmpresa(), 30));
        tvPrecio20KG.setText(Home.mPrice.getPrecio(Home.mDirecciones.get(posicion).getEmpresa(), 20));
        tvPrecio10KG.setText(Home.mPrice.getPrecio(Home.mDirecciones.get(posicion).getEmpresa(), 10));
        tvTotalPrice.setText(Home.mPrice.getTotal(Home.mDirecciones.get(posicion).getEmpresa(), cantidad30kg, cantidad20kg, cantidad10kg));
    }

    private void declaration() {
        spinner = findViewById(R.id.spinner);

        tvPrecio30KG = findViewById(R.id.tvPrecio30kg);
        tvPrecio20KG = findViewById(R.id.tvPrecio20kg);
        tvPrecio10KG = findViewById(R.id.tvPrecio10kg);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);

        buttonMenos30kg = findViewById(R.id.btnMenos30kg);
        buttonMas30kg = findViewById(R.id.btnMas30kg);
        tvCantidad30Kg = findViewById(R.id.tvCantidad30Kg);

        buttonMenos20kg = findViewById(R.id.btnMenos20kg);
        buttonMas20kg = findViewById(R.id.btnMas20kg);
        tvCantidad20Kg = findViewById(R.id.tvCantidad20Kg);

        buttonMenos10kg = findViewById(R.id.btnMenos10kg);
        buttonMas10kg = findViewById(R.id.btnMas10kg);
        tvCantidad10Kg = findViewById(R.id.tvCantidad10Kg);

        btnPedir = findViewById(R.id.btnPedir);
    }

    @SuppressLint("SetTextI18n")
    private void listenner() {
        buttonMenos30kg.setOnClickListener(v -> new Thread(() -> {
            if(cantidad30kg>0)
            {
                cantidad30kg--;
                runOnUiThread(() -> {
                    tvCantidad30Kg.setText(Integer.toString(cantidad30kg));
                    tvTotalPrice.setText(Home.mPrice.getTotal(Home.mDirecciones.get(posicion).getEmpresa(), cantidad30kg, cantidad20kg, cantidad10kg));
                });
            }
        }).start());

        buttonMas30kg.setOnClickListener(v -> new Thread(() -> {
            if(cantidad30kg<10)
            {
                cantidad30kg++;
                runOnUiThread(() -> {
                    tvCantidad30Kg.setText(Integer.toString(cantidad30kg));
                    tvTotalPrice.setText(Home.mPrice.getTotal(Home.mDirecciones.get(posicion).getEmpresa(), cantidad30kg, cantidad20kg, cantidad10kg));
                });
            }
        }).start());

        buttonMenos20kg.setOnClickListener(v -> new Thread(() -> {
            if(cantidad20kg>0)
            {
                cantidad20kg--;
                runOnUiThread(() -> {
                    tvCantidad20Kg.setText(Integer.toString(cantidad20kg));
                    tvTotalPrice.setText(Home.mPrice.getTotal(Home.mDirecciones.get(posicion).getEmpresa(), cantidad30kg, cantidad20kg, cantidad10kg));
                });
            }
        }).start());

        buttonMas20kg.setOnClickListener(v -> new Thread(() -> {
            if(cantidad20kg<10)
            {
                cantidad20kg++;
                runOnUiThread(() -> {
                    tvCantidad20Kg.setText(Integer.toString(cantidad20kg));
                    tvTotalPrice.setText(Home.mPrice.getTotal(Home.mDirecciones.get(posicion).getEmpresa(), cantidad30kg, cantidad20kg, cantidad10kg));
                });
            }
        }).start());

        buttonMenos10kg.setOnClickListener(v -> new Thread(() -> {
            if(cantidad10kg>0)
            {
                cantidad10kg--;
                runOnUiThread(() -> {
                    tvCantidad10Kg.setText(Integer.toString(cantidad10kg));
                    tvTotalPrice.setText(Home.mPrice.getTotal(Home.mDirecciones.get(posicion).getEmpresa(), cantidad30kg, cantidad20kg, cantidad10kg));
                });
            }
        }).start());

        buttonMas10kg.setOnClickListener(v -> new Thread(() -> {
            if(cantidad10kg<10)
            {
                cantidad10kg++;
                runOnUiThread(() -> {
                    tvCantidad10Kg.setText(Integer.toString(cantidad10kg));
                    tvTotalPrice.setText(Home.mPrice.getTotal(Home.mDirecciones.get(posicion).getEmpresa(), cantidad30kg, cantidad20kg, cantidad10kg));
                });
            }
        }).start());

        btnPedir.setOnClickListener(v -> {
            if(buttonPress)
                return;
            buttonPress=true;

            new Thread(() -> {
                if(cantidad30kg == 0 && cantidad20kg == 0 && cantidad10kg == 0)
                {
                    runOnUiThread(() -> Toast.makeText(HomePedirCilindros.this, "Debes elegir cantidad.", Toast.LENGTH_SHORT).show());
                }
                else
                {
                    Intent b = new Intent(HomePedirCilindros.this, HomePedirCilindrosVerificar.class );
                    b.putExtra("cantidad30kg", cantidad30kg);
                    b.putExtra("cantidad20kg", cantidad20kg);
                    b.putExtra("cantidad10kg", cantidad10kg);
                    b.putExtra("posicion", posicion);
                    startActivity(b);
                }

                new Thread(() -> {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    buttonPress=false;
                }).start();
            }).start();
        });
    }

    boolean buttonPress = false;

    private void initList() {
        Spinner spinnerDomicilio = findViewById(R.id.spinner);
        DireccionesAdapterSpinner mAdapter = new DireccionesAdapterSpinner(this, (ArrayList<Direcciones>) Home.mDirecciones);
        spinnerDomicilio.setAdapter(mAdapter);

        spinnerDomicilio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                posicion = position;
                tvPrecio30KG.setText(Home.mPrice.getPrecio(Home.mDirecciones.get(posicion).getEmpresa(), 30));
                tvPrecio20KG.setText(Home.mPrice.getPrecio(Home.mDirecciones.get(posicion).getEmpresa(), 20));
                tvPrecio10KG.setText(Home.mPrice.getPrecio(Home.mDirecciones.get(posicion).getEmpresa(), 10));
                tvTotalPrice.setText(Home.mPrice.getTotal(Home.mDirecciones.get(posicion).getEmpresa(), cantidad30kg, cantidad20kg, cantidad10kg));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
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