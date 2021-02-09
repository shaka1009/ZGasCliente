package zgas.client;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import zgas.client.includes.Popup;
import zgas.client.includes.Toolbar;

public class HomePedirCilindrosVerificar extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    public static Activity activity;

    private ConnectivityManager cm;
    int cantidad30kg, cantidad20kg, cantidad10kg;
    private int posicion;

    TextView tvDomicilio;
    TextView tvTotalPrice;
    Button btnVerificar;

    private Popup mPopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_pedir_cilindros_verificar);
        Toolbar.show(this, true);
        mPopup = new Popup(this, getApplicationContext(), findViewById(R.id.popupError));
        cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        activity = this;


        cantidad30kg = getIntent().getIntExtra("cantidad30kg", 0);
        cantidad20kg = getIntent().getIntExtra("cantidad20kg", 0);
        cantidad10kg = getIntent().getIntExtra("cantidad10kg", 0);
        posicion = getIntent().getIntExtra("posicion", 100);

        if((cantidad30kg == 0 && cantidad20kg == 0 && cantidad10kg == 0) || posicion == 100)
            finish();

        /* DEP

        cantidad30kg = 5;
        cantidad20kg = 4;
        cantidad10kg = 2;
        posicion = 0;*/

        declaration();
        loading();
        listenner();
    }

    private boolean isConnected(){
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    boolean buttonPress = false;
    private void listenner() {
        btnVerificar.setOnClickListener(v -> {
            if(buttonPress)
                return;
            buttonPress=true;

            mPopup.setPopupConfirmar();

            new Thread(() -> {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                buttonPress=false;
            }).start();
        });



        mPopup.popupVerificarServicioCancelar.setOnClickListener(v -> {
            if(buttonPress)
                return;
            buttonPress=true;


            mPopup.hidePopupConfirmar();

            new Thread(() -> {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                buttonPress=false;
            }).start();
        });

        mPopup.popupVerificarServicioConfirmar.setOnClickListener(v -> {
            if(buttonPress)
                return;
            buttonPress=true;

            if(!isConnected())
            {
                runOnUiThread(() -> {
                    Snackbar snackbar1;
                    snackbar1 = Snackbar.make(v, "No hay conexión a internet, no puedes solicitar el servicio.", Snackbar.LENGTH_SHORT);
                    View snackBarView = snackbar1.getView();
                    snackBarView.setBackgroundColor(getResources().getColor(R.color.rojoAlert));
                    snackbar1.show();
                });
            }
            else
            {
                Intent j = new Intent(HomePedirCilindrosVerificar.this, HomePedirCilindrosVerificarSolicitar.class);
                j.putExtra("cantidad30kg", cantidad30kg);
                j.putExtra("cantidad20kg", cantidad20kg);
                j.putExtra("cantidad10kg", cantidad10kg);
                j.putExtra("posicion", posicion);
                startActivity(j);
                mPopup.hidePopupConfirmar();

                try {
                    HomePedir.activity.finish();
                }catch (Exception ignored){}

                try {
                    HomePedirCilindros.activity.finish();
                }catch (Exception ignored){}

                try {
                    HomePedirCilindrosVerificar.activity.finish();
                }catch (Exception ignored){}
            }
            new Thread(() -> {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                buttonPress=false;
            }).start();
        });
    }

    @SuppressLint("SetTextI18n")
    private void loading() {
        tvDomicilio.setText(

                "Etiqueta: " + Home.mDirecciones.get(posicion).getEtiqueta() + "\n\n" +
                        "Dirección: " + Home.mDirecciones.get(posicion).getDomicilioLargo() + "\n\n" +
                        "Seña Particular: " + Home.mDirecciones.get(posicion).getSParticular());

        tvTotalPrice.setText(Home.mPrice.getTotal(Home.mDirecciones.get(posicion).getEmpresa(), cantidad30kg, cantidad20kg, cantidad10kg));

        TableLayout tableLayout = findViewById(R.id.table_layout);
        table(tableLayout);

        TableLayout tableLayoutPopup = (TableLayout)  mPopup.getPopupVerificarServicio().findViewById(R.id.table_layout);
        table(tableLayoutPopup);
    }

    @SuppressLint("SetTextI18n")
    private void table(TableLayout tableLayout)
    {
        TableRow tableRow;
        TextView producto, cantidad, precio, subtotal;

        if(cantidad30kg!=0)
        {
            producto = new TextView(this);
            cantidad = new TextView(this);
            precio = new TextView(this);
            subtotal = new TextView(this);

            tableRow = new TableRow(this);

            producto.setText("Cil. 30 Kg");
            cantidad.setText(Integer.toString(cantidad30kg));
            precio.setText(Home.mPrice.getPrecio(Home.mDirecciones.get(posicion).getEmpresa(), 30));
            subtotal.setText(Home.mPrice.getPrecio(Home.mDirecciones.get(posicion).getEmpresa(), 30*cantidad30kg));

            tableRow.addView(producto);
            tableRow.addView(cantidad);
            tableRow.addView(precio);
            tableRow.addView(subtotal);
            tableLayout.addView(tableRow);
        }

        if(cantidad20kg!=0)
        {
            producto = new TextView(this);
            cantidad = new TextView(this);
            precio = new TextView(this);
            subtotal = new TextView(this);

            tableRow = new TableRow(this);

            producto.setText("Cil. 20 Kg");
            cantidad.setText(Integer.toString(cantidad20kg));
            precio.setText(Home.mPrice.getPrecio(Home.mDirecciones.get(posicion).getEmpresa(), 20));
            subtotal.setText(Home.mPrice.getPrecio(Home.mDirecciones.get(posicion).getEmpresa(), 20*cantidad20kg));

            tableRow.addView(producto);
            tableRow.addView(cantidad);
            tableRow.addView(precio);
            tableRow.addView(subtotal);
            tableLayout.addView(tableRow);
        }

        if(cantidad10kg!=0)
        {
            producto = new TextView(this);
            cantidad = new TextView(this);
            precio = new TextView(this);
            subtotal = new TextView(this);

            tableRow = new TableRow(this);

            producto.setText("Cil. 10 Kg");
            cantidad.setText(Integer.toString(cantidad10kg));
            precio.setText(Home.mPrice.getPrecio(Home.mDirecciones.get(posicion).getEmpresa(), 10));
            subtotal.setText(Home.mPrice.getPrecio(Home.mDirecciones.get(posicion).getEmpresa(), 10*cantidad10kg));

            tableRow.addView(producto);
            tableRow.addView(cantidad);
            tableRow.addView(precio);
            tableRow.addView(subtotal);
            tableLayout.addView(tableRow);
        }

        if(cantidad30kg!=0 ||cantidad20kg!=0 ||cantidad10kg!=0)
        {
            producto = new TextView(this);
            cantidad = new TextView(this);
            precio = new TextView(this);
            subtotal = new TextView(this);
            tableRow = new TableRow(this);

            producto.setText("\nTOTAL: ");
            cantidad.setText("");
            precio.setText("");
            subtotal.setText("\n" + Home.mPrice.getTotal(Home.mDirecciones.get(posicion).getEmpresa(), cantidad30kg, cantidad20kg, cantidad10kg));

            tableRow.addView(producto);
            tableRow.addView(cantidad);
            tableRow.addView(precio);
            tableRow.addView(subtotal);
            tableLayout.addView(tableRow);
        }
    }


    private void declaration() {
        tvDomicilio = findViewById(R.id.tvDomicilio);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);

        btnVerificar = findViewById(R.id.btnVerificar);
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