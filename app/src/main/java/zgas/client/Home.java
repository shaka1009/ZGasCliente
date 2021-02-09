package zgas.client;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

import zgas.client.includes.Popup;
import zgas.client.includes.Toolbar;
import zgas.client.models.Client;
import zgas.client.models.Direcciones;
import zgas.client.models.Price;
import zgas.client.providers.AuthProvider;
import zgas.client.providers.ClientBookingProvider;
import zgas.client.providers.ClientProvider;
import zgas.client.providers.DireccionProvider;
import zgas.client.providers.PriceProvider;
import zgas.client.providers.TokenProvider;

public class Home extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    public static Activity activity;

    private AuthProvider mAuthProvider;
    ClientProvider mClientProvider;
    TokenProvider mTokenProvider;
    private DireccionProvider mDireccionProvider;
    private ClientBookingProvider mClientBookingProvider;
    private PriceProvider mPriceProvider;

    public static Client mClient;
    public static Price mPrice;
    public static List<Direcciones> mDirecciones;



    private DrawerLayout drawer;
    private CoordinatorLayout snackbar;
    private Popup mPopup;

    @SuppressLint("StaticFieldLeak")
    public static TextView tvTelefono, tvNombre, tvApellido;

    private Button btnPedir;
    private Button btnPerfil;
    private Button btnDomicilios;
    private Button btnRegalos;
    private Button btnPedidos;
    private ConnectivityManager cm;


    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar.showHome(this, true);
        cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        mPopup = new Popup(this, getApplicationContext(), findViewById(R.id.popupError));
        mPriceProvider = new PriceProvider();
        mAuthProvider = new AuthProvider();
        mClientProvider = new ClientProvider();
        mTokenProvider = new TokenProvider();
        mClientBookingProvider = new ClientBookingProvider();
        mDireccionProvider = new DireccionProvider(mAuthProvider.getId());
        activity = this;

        //close_activitys();
        drawerMain();
        declaration();
        listenner();
        generateToken();

        load_first_day();
        close_activitys();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //CARGA DE DATOS RECARGADOS
        Toast.makeText(this, "Perfil: " + Client.isIsLoad() + "\n" +
                "Precio: " + Price.isLoad() + "\n" +
                "Domicilios: " + Direcciones.isLoad() + "\n"
                , Toast.LENGTH_SHORT).show();

        new Thread(() -> {
            if(!isConnected())
            {
                runOnUiThread(() -> {
                    Snackbar snackbar1;
                    snackbar1 = Snackbar.make(snackbar, "No hay conexión a internet.", Snackbar.LENGTH_SHORT);
                    View snackBarView = snackbar1.getView();
                    snackBarView.setBackgroundColor(getResources().getColor(R.color.rojoAlert));
                    snackbar1.show();
                });
            }

        }).start();
    }

    @Override
    protected void onDestroy() {
        //finish();
        super.onDestroy();
    }



    ////////////CARGA DE DATOS
    private void load_first_day() {
        //Load Perfil
        new Thread(new Runnable(){
            @Override
            public void run()
            {
                load_datos();
            }
        }).start();

        load_in_service();
        load_domicilios();
    }

    private void load_domicilios() {
        new Thread(() -> mDireccionProvider.getDomicilios().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mDireccionProvider.load_domicilios(snapshot);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        })).start();
    }

    private void load_datos()
    {
        try {
            FileInputStream read = openFileInput("Acc_App");
            int size = read.available();
            byte[] buffer = new byte[size];
            read.read(buffer);
            read.close();
            String text = new String(buffer);
            StringTokenizer token = new StringTokenizer(text, "\n");
            String fecha = token.nextToken();

            java.util.Date Data = new Date();
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            if(fecha.equals(dateFormat.format(Data)))
            {

                Home.mClient.setTelefono(token.nextToken());
                Home.mClient.setNombre(token.nextToken());
                Home.mClient.setApellido(token.nextToken());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvTelefono.setText(mClient.getTelefono());
                        tvNombre.setText(mClient.getNombre());
                        tvApellido.setText(mClient.getApellido());
                    }
                });
                Client.setIsLoad(true);
            }
            else
                guardar_datos();
        }
        catch(Exception e){
            guardar_datos();
        }

        load_precio();
    }

    private void guardar_datos()
    {
        new Thread(() -> mClientProvider.getClient(mAuthProvider.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    String id = mAuthProvider.getId();
                    String nombre = Objects.requireNonNull(snapshot.child("nombre").getValue()).toString();
                    String apellido = Objects.requireNonNull(snapshot.child("apellido").getValue()).toString();
                    String telefono = mAuthProvider.getPhone();
                    Home.mClient = new Client(id, nombre, apellido, telefono);
                    Client.setIsLoad(true);

                    Log.d("DEP", "Datos en variable.");

                    try{
                        //DELETE FILE
                        try{
                            try{
                                deleteFile("Acc_App");
                            }catch(Exception ignored){}

                            File f = new File("Acc_App");
                            f.delete();
                        }catch(Exception ignored){}
                        //

                        java.util.Date Data = new Date();
                        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                        FileOutputStream conf = openFileOutput("Acc_App", Context.MODE_PRIVATE);
                        String cadena =
                                dateFormat.format(Data) + "\n" +
                                        Home.mClient.getTelefono() + "\n" +
                                        Home.mClient.getNombre() + "\n" +
                                        Home.mClient.getApellido() + "\n";

                        conf.write(cadena.getBytes());
                        conf.close();
                    }
                    catch(Exception ignored){}

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvTelefono.setText(mClient.getTelefono());
                            tvNombre.setText(mClient.getNombre());
                            tvApellido.setText(mClient.getApellido());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        })).start();
    }

    private void load_precio()
    {
        try {
            FileInputStream read = openFileInput("Precio");
            int size = read.available();
            byte[] buffer = new byte[size];
            read.read(buffer);
            read.close();
            String text = new String(buffer);
            StringTokenizer token = new StringTokenizer(text, "\n");
            String fecha = token.nextToken();

            java.util.Date Data = new Date();
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            if(fecha.equals(dateFormat.format(Data)))
            {
                Home.mPrice.setThermogas(Double.parseDouble(token.nextToken()));
                Home.mPrice.setMultigas(Double.parseDouble(token.nextToken()));
                Home.mPrice.setGaslicuado(Double.parseDouble(token.nextToken()));
                Price.setIsLoad(true);
            }
            else
                guardar_datosPrecio();
        }
        catch(Exception e){
            guardar_datosPrecio();
        }
    }

    private void guardar_datosPrecio()
    {
        new Thread(() -> mPriceProvider.getPrice().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    double thermogas = (double) snapshot.child("thermogas").getValue();
                    double multigas = (double) snapshot.child("multigas").getValue();
                    double gaslicuado = (double) snapshot.child("gaslicuado").getValue();
                    Home.mPrice = new Price(thermogas, multigas, gaslicuado);
                    Price.setIsLoad(true);

                    try{
                        //DELETE FILE
                        try{
                            try{
                                deleteFile("Precio");
                            }catch(Exception ignored){}

                            File f = new File("Precio");
                            f.delete();
                        }catch(Exception ignored){}
                        //

                        java.util.Date Data = new Date();
                        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                        FileOutputStream conf = openFileOutput("Precio", Context.MODE_PRIVATE);
                        String cadena =
                                dateFormat.format(Data) + "\n" +
                                        Home.mPrice.getThermogas() + "\n" +
                                        Home.mPrice.getMultigas() + "\n" +
                                        Home.mPrice.getGaslicuado() + "\n";

                        conf.write(cadena.getBytes());
                        conf.close();
                    }
                    catch(Exception ignored){}
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        })).start();
    }

    private void load_in_service() {
        new Thread(new Runnable(){
            @Override
            public void run()
            {
                mClientBookingProvider.getStatus(mAuthProvider.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String status = snapshot.getValue().toString();

                            /*
                            //FIXEAR
                            if(status.equals("finish"))
                            {
                                Intent intent = new Intent(Home.this, HomeCalificacion.class);
                                startActivity(intent);
                            }

                            else */
                                if(status.equals("create") || status.equals("new") || status.equals("accept") || status.equals("cancel"))
                            {
                                Intent intent = new Intent(Home.this, HomeStatusServicio.class);
                                startActivity(intent);
                            }
                            else if(status.equals("terminate"))
                            {
                                mClientBookingProvider.delete(mAuthProvider.getId());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            }
        }).start();

    }

    //FIN CARGA DE DATOS

    void generateToken() {
        new Thread(new Runnable(){
            @Override
            public void run()
            {
                while(true)
                {
                    if(isConnected())
                    {
                        mTokenProvider.create(mAuthProvider.getId());
                        break;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }


    private boolean isConnected(){
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    private void close_activitys() {
        new Thread(() -> {
            while(true)
            {
                if(Client.isLoad() && Direcciones.isLoad() && Price.isLoad())
                {
                    try {
                        MainActivity.activity.finish();
                    }catch (Exception ignored){}

                    try {
                        LoginSMS.activity.finish();
                    }catch (Exception ignored){}

                    break;
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("DEP", "Cargando datos");
                    }
                });
            }

        }).start();
    }

    private boolean pressButton;
    private void listenner() {
        pressButton = false;

        mPopup.popupCerrarSesionSalir.setOnClickListener(v -> new Thread(() -> {
            mPopup.hidePopupCerrarSesion();
            if(pressButton)
                return;
            else pressButton = true;
            try{
                try{
                    deleteFile("Acc_App");
                }catch(Exception ignored){}

                File f = new File("Acc_App");
                f.delete();
            }catch(Exception ignored){}


            mAuthProvider.logout();
            Intent intent = new Intent(Home.this, Login.class);
            startActivity(intent);
            SleepButton();
            finish();
        }).start());

        mPopup.btnPoupAddAccAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pressButton)
                    return;
                else pressButton = true;
                Intent register = new Intent(Home.this , HomeDirecciones.class);
                startActivity(register);
                SleepButton();
                mPopup.hidePopupAddAcc();
            }
        });

        btnPedir.setOnClickListener(v -> {
            if(pressButton)
                return;
            else pressButton = true;

            if(!Price.isLoad() || !Direcciones.isLoad() || !Client.isLoad())
            {
                Snackbar.make(v, "No se han cargado los datos necesarios.", Snackbar.LENGTH_LONG)
                        .setActionTextColor(getResources().getColor(R.color.white)).show();
                pressButton = false;
                return;
            }

            if(mDirecciones.isEmpty())
            {
                mPopup.setPopupAddAcc("Debes agregar al menos una cuenta para hacer un pedido.");
                pressButton = false;
                return;

            }
            Intent intent = new Intent(Home.this , HomePedir.class);
            startActivity(intent);
            SleepButton();
        });

        mPopup.btnPoupAddAccAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this , HomeDirecciones.class);
                startActivity(intent);
                mPopup.hidePopupAddAcc();
                SleepButton();
            }
        });

        btnPerfil.setOnClickListener(v -> {
            if(pressButton)
                return;
            else pressButton = true;

            if(!Client.isLoad())
            {
                Snackbar.make(v, "No se han cargado los datos necesarios.", Snackbar.LENGTH_LONG)
                        .setActionTextColor(getResources().getColor(R.color.white)).show();
                pressButton = false;
                return;
            }

            Intent register = new Intent(Home.this , HomePerfil.class);
            startActivity(register);
            SleepButton();
        });

        btnDomicilios.setOnClickListener(v -> {
            if(pressButton)
                return;
            else pressButton = true;

            if(!Direcciones.isLoad())
            {
                Snackbar.make(v, "No se han cargado los datos necesarios.", Snackbar.LENGTH_LONG)
                        .setActionTextColor(getResources().getColor(R.color.white)).show();
                pressButton = false;
                return;
            }

            Intent HomeDomicilios = new Intent(Home.this , HomeDirecciones.class);
            startActivity(HomeDomicilios);
            SleepButton();
        });

        btnRegalos.setOnClickListener(v -> {
            if(pressButton)
                return;
            else pressButton = true;
            //Intent register = new Intent(Home.this , Home_regalos.class);
            //startActivity(register);
            SleepButton();
        });

        btnPedidos.setOnClickListener(v -> {
            if(pressButton)
                return;
            else pressButton = true;
            Intent register = new Intent(Home.this , HomeHistorial.class);
            startActivity(register);
            SleepButton();
        });
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

    private void declaration() {
        btnPedir = findViewById(R.id.btnPedir);
        btnPerfil = findViewById(R.id.btnPerfil);
        btnDomicilios = findViewById(R.id.btnDomicilios);
        btnRegalos = findViewById(R.id.btnRegalos);
        btnPedidos = findViewById(R.id.btnPedidos);

        snackbar = findViewById(R.id.snackbar_layout);
    }


    @SuppressLint("NonConstantResourceId")
    private void drawerMain()
    {
        //drawer.openDrawer(Gravity.LEFT);
        //drawer.closeDrawer(Gravity.LEFT);


        //drawer
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);

        tvTelefono = hView.findViewById(R.id.slide_telefono);
        tvNombre = hView.findViewById(R.id.slide_nombre);
        tvApellido = hView.findViewById(R.id.slide_apellido);
        //drawer

        /// MASK 10 DIGITOS
        SimpleMaskFormatter smf1 = new SimpleMaskFormatter("+NN NN NNNN NNNN");
        MaskTextWatcher mtw1 = new MaskTextWatcher(tvTelefono, smf1);
        tvTelefono.addTextChangedListener(mtw1);





        navigationView.bringToFront();

        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId())
            {
                case R.id.menu_perfil:
                    if(pressButton)
                        break;
                    else pressButton = true;

                    if(!Client.isLoad())
                    {
                        View v=findViewById(R.id.menu_perfil);
                        Snackbar.make(v, "No se han cargado los datos necesarios.", Snackbar.LENGTH_LONG)
                                .setActionTextColor(getResources().getColor(R.color.white)).show();
                        pressButton = false;
                        break;
                    }
                    Intent b = new Intent(Home.this, HomePerfil.class );
                    startActivity(b);
                    SleepButton();
                break;

                case R.id.cerrar_sesion:
                    if(pressButton)
                        break;
                    else pressButton = true;

                    try
                    {
                        mPopup.setPopupCerrarSesion(mClient.getNombre(), mClient.getApellido());
                    }
                    catch (Exception ignored){}

                    SleepButton();
                break;


                case R.id.menu_sucursales:
                    if(pressButton)
                        break;
                    else pressButton = true;

                    Intent sucursales = new Intent(this, HomeSucursales.class);
                    startActivity(sucursales);

                    SleepButton();
                    break;



            }
            drawer.closeDrawers();
            return false;
        });
    }



    @SuppressLint("RtlHardcoded")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            drawer.openDrawer(Gravity.LEFT);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true); //Minimizar
    }
}