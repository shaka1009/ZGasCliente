package zgas.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import zgas.client.adapters.DireccionesListAdapter;
import zgas.client.adapters.SucursalesListAdapter;
import zgas.client.includes.Toolbar;
import zgas.client.models.Direcciones;
import zgas.client.models.Sucursales;

public class HomeSucursales extends AppCompatActivity {

    private RecyclerView rvSucursal;
    private List<Sucursales> mSucursales;
    private ProgressBar loadingSucursales;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_sucursales);
        Toolbar.show(this, true);

        rvSucursal = findViewById(R.id.rvSucursal);
        mSucursales = new ArrayList<>();


        mSucursales.add(new Sucursales("Centro Magno", "Zona B Local 15", "36303386", "Lun-Vie 9:00-19:00\nSáb 9:00-15:00"));
        mSucursales.add(new Sucursales("El Manantial", "López Mateos Sur 5560 Zona L Local 7", "36865806", "Lun-Vie 9:00-18:00\nSáb 9:00-15:00"));
        mSucursales.add(new Sucursales("El Sauz", "Av. Colón No.4030 Local 6-A y 7-A", "36456168", "Lun-Vie 9:00-14:00\n14:30-17:00\nSáb 9:00-15:00"));
        mSucursales.add(new Sucursales("La Gran Plaza", "Av. Vallarta 3959 Zona O Local 1", "36210704, \n38134294", "Lun-Vie 10:00-19:00\nSáb 11:00-20:00\nDom 11:00-18:00"));
        mSucursales.add(new Sucursales("Plaza Altea", "Av Rio Nilo 7377 Local A11", "36067319", "Lun-Vie 10:00-14:00\n14:30-18:00\nSáb 9:00-15:00\nDom 11:00-18:00"));
        mSucursales.add(new Sucursales("Plaza Amistad", "Av. Patria N° 600 Zona A Local 19", "31651101", "Lun-Vie 9:00-18:00\nSáb 9:00-15:00"));
        mSucursales.add(new Sucursales("Plaza Arboledas", "Av. Arboledas No.2500 Zona F Local 17", "31621436", "Lun-Vie 9:00-18:00\nSáb 9:00-15:00\nDom 11:00-18:00"));
        mSucursales.add(new Sucursales("Plaza del Sol I", "López Mateos 2375 Zona B Local 6", "31211572", "Lun-Vie 8:30-19:00\nSáb 9:00-20:00\nDom 11:00-18:00"));
        mSucursales.add(new Sucursales("Plaza del Sol II", "López Mateos 2375 Zona R Local 38 y 39", "31212108", "Lun-Vie 8:30-19:00\nSáb 9:00-20:00\nDom 11:00-18:00"));
        mSucursales.add(new Sucursales("Plaza Fontana", "Sta. Margarita 4140 Local 17", " 33645597", "Lun-Vie 9:00-18:00\nSáb 9:00-15:00"));
        mSucursales.add(new Sucursales("Plaza Independencia", "Calzada Independencia Nte. N° 3295, Zona E Local 9", "36740154", "Lun-Vie 10:00-19:00\nSáb 9:00-18:00\nDom 11:00-18:00"));
        mSucursales.add(new Sucursales("Plaza las Torres", "Av. 8 de Julio 1896 Zona L Local 18 y 19", "38106832", "Lun-Vie 9:00-18:00\nSáb 9:00-15:00"));
        mSucursales.add(new Sucursales("Plaza Patria", "Zona L Local 7", "36414104, \n31110048", "Lun-Vie 8:30-19:00\nSáb 9:00-20:00\nDom 11:00-18:00"));
        mSucursales.add(new Sucursales("Plaza Revolución", "Zona B Local 14", "36393894", "Lun-Vie 8:30-19:00\nSáb 9:00-16:00\nDom 11:00-18:00"));
        mSucursales.add(new Sucursales("Plaza San Isidro", "Anillo Periférico 760 Zona G Local 9", "33641051", "Lun-Vie 9:00-18:00\nSáb 9:00-15:00"));
        mSucursales.add(new Sucursales("Plaza Tepeyac", "Av. Tepeyac 1150 Zona G Local 1", "36206754", "Lun-Vie 8:30-17:00\nSáb 9:00-15:00"));
        mSucursales.add(new Sucursales("Villas de San Juan", "Circunvalación Div. Del Norte 451 Local 6", "38247836, \n36389621", "Lun-Vie 8:30-19:00\nSáb 9:00-16:00\nDom 11:00-18:00"));

        load_surucrsales();
    }

    private void load_surucrsales() {
        SucursalesListAdapter sucursalesListAdapter = new SucursalesListAdapter(mSucursales, this);
        rvSucursal.setHasFixedSize(true);
        rvSucursal.setLayoutManager(new LinearLayoutManager(this));
        rvSucursal.setAdapter(sucursalesListAdapter);
        loadingSucursales = findViewById(R.id.loadingSucursales);
        loadingSucursales.setVisibility(View.INVISIBLE);
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