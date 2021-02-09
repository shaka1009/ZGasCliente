package zgas.client;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import zgas.client.includes.Toolbar;
import zgas.client.providers.AuthProvider;
import zgas.client.providers.ClientBookingProvider;
import zgas.client.providers.DireccionProvider;

public class HomePedirCilindrosVerificarSolicitar extends AppCompatActivity {

    private AuthProvider mAuthProvider;
    DireccionProvider mDireccionProvider;
    ClientBookingProvider mBoookingProvider;
    private ConnectivityManager cm;

    private boolean isCancel;

    int cantidad30kg, cantidad20kg, cantidad10kg;
    private int posicion;


    Button btnCancel;
    ProgressBar progressBar, progressBar2;
    TextView tvTitulo, tvEtiqueta, tvCalle, tvPedidoNombre, tvCantidad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_pedir_cilindros_verificar_solicitar);
        Toolbar.show(this, true);
        cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        mAuthProvider = new AuthProvider();
        mDireccionProvider = new DireccionProvider(mAuthProvider.getId());
        mBoookingProvider = new ClientBookingProvider();


        cantidad30kg = getIntent().getIntExtra("cantidad30kg", 0);
        cantidad20kg = getIntent().getIntExtra("cantidad20kg", 0);
        cantidad10kg = getIntent().getIntExtra("cantidad10kg", 0);
        posicion = getIntent().getIntExtra("posicion", 100);

        if((cantidad30kg == 0 && cantidad20kg == 0 && cantidad10kg == 0) || posicion == 100)
            finish();

/* DEP
        //borrar
        cantidad30kg = 5;
        cantidad20kg = 4;
        cantidad10kg = 2;
        posicion = 0;
        //borrar*/

        declaration();
        listenner();
        Logic();

    }

    private void listenner() {
        btnCancel.setOnClickListener(v -> {
            isCancel = true;
            finish();
        });
    }

    @SuppressLint("SetTextI18n")
    private void declaration() {
        isCancel = false;
        tvTitulo = findViewById(R.id.tvTitulo);
        tvEtiqueta = findViewById(R.id.tvEtiqueta);
        tvCalle = findViewById(R.id.tvCalle);
        tvPedidoNombre = findViewById(R.id.tvPedidoNombre);
        tvCantidad = findViewById(R.id.tvCantidad);

        progressBar = findViewById(R.id.progressBar);
        progressBar2 = findViewById(R.id.progressBar2);

        btnCancel = findViewById(R.id.btnCancel);
    }

    private boolean isConnected(){
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    int counter = 0;
    @SuppressLint("SetTextI18n")
    private void Logic()
    {
        tvEtiqueta.setText(Home.mDirecciones.get(posicion).getEtiqueta());
        tvCalle.setText(Home.mDirecciones.get(posicion).getCalle() + ", " + Home.mDirecciones.get(posicion).getColonia() );
        tvPedidoNombre.setText("Tu pedido, " + Home.mClient.getNombre() + " " + Home.mClient.getApellido());

        String cantidad = "";
        String salto= "";

        if(cantidad30kg!=0)
        {
            cantidad = cantidad30kg + " - Cil 30 Kg ";
        }
        if(cantidad20kg!=0)
        {
            if(cantidad30kg>=1)
                salto = "\n";
            else
                salto = "";
            cantidad = cantidad + salto + cantidad20kg + " - Cil 20 Kg ";
        }
        if(cantidad10kg!=0)
        {
            if(cantidad30kg>=1 || cantidad20kg>=1)
                salto = "\n";
            else
                salto = "";
            cantidad = cantidad + "\n" + cantidad10kg + " - Cil 10 Kg ";
        }


        tvCantidad.setText(cantidad);

        final Timer t = new Timer();
        TimerTask tt = new TimerTask(){
            @Override
            public void run()
            {
                counter++;
                progressBar.setProgress(counter);
                if(isCancel)
                    t.cancel();
                if(counter == 100 )
                {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.INVISIBLE);
                        btnCancel.setVisibility(View.INVISIBLE);
                        tvTitulo.setText("Espera un momento...");
                        progressBar2.setVisibility(View.VISIBLE);
                    });

                    if(isCancel)
                        runOnUiThread(() -> finish());
                    else
                        new Thread(() -> peticion_servicio()).start();

                    t.cancel();
                }
            }
        };
        t.schedule(tt, 0, 50);
    }

    private void peticion_servicio()
    {
        StringBuilder token = new StringBuilder();
        for (int i=0; i<10; i++)
        {

            int codigoAscii;
            while(true)
            {
                codigoAscii  = (int)Math.floor(Math.random()*(122 -
                        65)+65);

                if(codigoAscii >= 91 && codigoAscii <= 96)
                    codigoAscii  = (int)Math.floor(Math.random()*(122 - 65)+65);
                else
                {
                    break;
                }
            }
            token.append((char) codigoAscii);
        }

        Map<String, Object> data = mDireccionProvider.direccionToMap(Home.mDirecciones.get(posicion));

        data.put("idDriver", "");
        data.put("idCliente", mAuthProvider.getId());
        data.put("cantidad30kg", cantidad30kg);
        data.put("cantidad20kg", cantidad20kg);
        data.put("cantidad10kg", cantidad10kg);
        data.put("idHistoryBooking", "");
        data.put("total", Home.mPrice.getTotal(Home.mDirecciones.get(posicion).getEmpresa(), cantidad30kg, cantidad20kg, cantidad10kg));

        data.put("id", token.toString());
        data.put("type", "cilindros");
        data.put("status", "new");

        while(true)
        {
            if(isCancel)
            {
                runOnUiThread(() -> Toast.makeText(HomePedirCilindrosVerificarSolicitar.this, "El servicio ha sido cancelado.", Toast.LENGTH_SHORT).show());
                finish();
                break;
            }
            else if(isConnected())
            {
                mBoookingProvider.create(mAuthProvider.getId(), data).addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                    {
                        Intent intent = new Intent(HomePedirCilindrosVerificarSolicitar.this, HomeStatusServicio.class);
                        intent.putExtra("cantidad30kg", cantidad30kg);
                        intent.putExtra("cantidad20kg", cantidad20kg);
                        intent.putExtra("cantidad10kg", cantidad10kg);

                        intent.putExtra("id", token.toString());
                        intent.putExtra("type", "cilindros");
                        intent.putExtra("status", "new");

                        intent.putExtra("posicion", posicion);

                        startActivity(intent);
                    }
                    else
                    {
                        runOnUiThread(() -> Toast.makeText(HomePedirCilindrosVerificarSolicitar.this, "El servicio ha sido cancelado.", Toast.LENGTH_SHORT).show());
                    }
                    finish();
                });

                break;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }



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
    public void onBackPressed(){
        backPress();
    }

    private void backPress() {
        isCancel = true;
        finish();
    }
    //BACK PRESS
}