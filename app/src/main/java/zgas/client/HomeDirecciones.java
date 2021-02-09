package zgas.client;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import zgas.client.adapters.DireccionesListAdapter;
import zgas.client.includes.Toolbar;
import zgas.client.models.Sucursales;

public class HomeDirecciones extends AppCompatActivity {

    private CoordinatorLayout snackbar;
    private ProgressBar loadingDomicilios;
    private RecyclerView rvDomicilio;
    private ConnectivityManager cm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_domicilios);
        Toolbar.show(this, true);
        cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        declaration();
        listenner();
        load_domicilios();

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadingDomicilios.setVisibility(View.VISIBLE);
        load_domicilios();
        loadingDomicilios.setVisibility(View.INVISIBLE);

    }

    private void declaration() {
        loadingDomicilios = findViewById(R.id.loadingDomicilios);
        rvDomicilio = findViewById(R.id.rvDomicilio);

        snackbar = findViewById(R.id.snackbar_layout);
    }

    private boolean pressButton;
    private void listenner() {

        FloatingActionButton addACC = findViewById(R.id.addACC);
        addACC.setOnClickListener(view -> {

            if(pressButton)
                return;
            else pressButton = true;

            if(Home.mDirecciones.size() == 10)
            {
                Snackbar.make(view, "No puedes añadir más de 10 domicilios.", Snackbar.LENGTH_LONG)
                        .setActionTextColor(getResources().getColor(R.color.white)).show();
                pressButton = false;
                return;
            }
            else if(!isConnected())
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar snackbar1;
                        snackbar1 = Snackbar.make(view, "No hay conexión a internet, no puedes registrar direcciones.", Snackbar.LENGTH_SHORT);
                        View snackBarView = snackbar1.getView();
                        snackBarView.setBackgroundColor(getResources().getColor(R.color.rojoAlert));
                        snackbar1.show();
                    }
                });
                pressButton = false;
                return;
            }

            Intent f = new Intent(HomeDirecciones.this, HomeDireccionesReg.class );
            startActivity(f);

            pressButton = false;
        });





        rvDomicilio.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean b) {

            }

            final GestureDetector mGestureDetector = new GestureDetector(HomeDirecciones.this, new GestureDetector.SimpleOnGestureListener() {
                @Override public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });

            @Override
            public boolean onInterceptTouchEvent(@NotNull RecyclerView recyclerView, @NotNull MotionEvent motionEvent) {
                try {
                    View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                    if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {

                        int position = recyclerView.getChildAdapterPosition(child);
                        Intent intent = new Intent(HomeDirecciones.this , HomeDireccionesDetails.class);
                        intent.putExtra("posicion", position);
                        startActivity(intent);
                        return true;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                return false;
            }

            @Override
            public void onTouchEvent(@NotNull RecyclerView recyclerView, @NotNull MotionEvent motionEvent) {

            }
        });
    }

    private boolean isConnected(){
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    private void load_domicilios() {
        DireccionesListAdapter direccionesListAdapter = new DireccionesListAdapter(Home.mDirecciones, this);
        rvDomicilio.setHasFixedSize(true);
        rvDomicilio.setLayoutManager(new LinearLayoutManager(this));
        rvDomicilio.setAdapter(direccionesListAdapter);
        loadingDomicilios.setVisibility(View.INVISIBLE);
    }


    //BACK PRESS
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
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