package zgas.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import zgas.client.includes.Toolbar;
import zgas.client.models.ClientBooking;
import zgas.client.models.Driver;
import zgas.client.models.DriverFound;
import zgas.client.models.FCMBody;
import zgas.client.models.FCMResponse;
import zgas.client.providers.AuthProvider;
import zgas.client.providers.ClientBookingProvider;
import zgas.client.providers.DriverProvider;
import zgas.client.providers.DriversFoundProvider;
import zgas.client.providers.GeofireProvider;
import zgas.client.providers.GoogleApiProvider;
import zgas.client.providers.NotificationProvider;
import zgas.client.providers.TokenProvider;

public class HomeStatusServicio extends AppCompatActivity {
    private AuthProvider mAuthProvider;
    private GoogleApiProvider mGoogleApiProvider;
    private ClientBookingProvider mClientBookingProvider;
    private ClientBooking mClientBooking;
    private GeofireProvider mGeofireProvider;
    private DriversFoundProvider mDriversFoundProvider;
    private TokenProvider mTokenProvider;
    private NotificationProvider mNotificationProvider;
    private Driver mDriver;
    private DriverProvider mDriverProvider;

    boolean allData;

    private ArrayList<String> mDriversNotAccept = new ArrayList<>();
    private ArrayList<String> mDriversFoundList = new ArrayList<>();
    private List<String> mTokenList = new ArrayList<>();

    private Handler mHandler = new Handler();
    private int mTimeLimit = 0;
    boolean isAcepted = false;

    private final double mRadius = 20;  //RADIO DE BÚSQUEDA

    //Diseño
    private ProgressBar time1, time2, time3;
    private LottieAnimationView animacion1, animacion2, animacion3;
    private ConstraintLayout mCustomBottomSheet;
    private BottomSheetBehavior mBottomSheetBehavior;
    private TextView tvStatus, tvSubStatus;
    private LinearLayout mHeaderLayout;
    private ImageView mHeaderImage, camion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_status_servicio);
        Toolbar.show(this, true);
        mClientBookingProvider = new ClientBookingProvider();
        mAuthProvider = new AuthProvider();
        mClientBooking = new ClientBooking();
        mGeofireProvider = new GeofireProvider("active_drivers");
        mDriversFoundProvider = new DriversFoundProvider(mAuthProvider.getId());
        mTokenProvider = new TokenProvider();
        mNotificationProvider = new NotificationProvider();
        mGoogleApiProvider = new GoogleApiProvider(HomeStatusServicio.this);
        mDriver = new Driver();
        mDriverProvider = new DriverProvider();
        declaration();
        bottomSheet();
        start();
    }

    private void isFinish() {
        //ALGORITMO PARA FINALIZAR

        runOnUiThread(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                tvStatus.setText("El servicio ha finalizado.");
                tvSubStatus.setText("Salga a recibir al operador, por favor.");
                time1.setProgress(100);
                time2.setProgress(100);
                time3.setProgress(100);
                time1.setIndeterminate(false);
                time2.setIndeterminate(false);
                time3.setIndeterminate(false);

                Toast.makeText(HomeStatusServicio.this, "El servicio ha finalizado", Toast.LENGTH_SHORT).show();//
            }
        });

        if (mHandlerFinish != null) {
            mHandlerFinish.removeCallbacks(mRunnableFinish);
        }

        mHandlerFinish.postDelayed(mRunnableFinish, 1000);
    }

    private void bottomSheet()
    {
        mCustomBottomSheet = findViewById(R.id.custom_bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(mCustomBottomSheet);

        mHeaderLayout = findViewById(R.id.header_layout);
        mHeaderImage = findViewById(R.id.header_arrow);

        mHeaderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        mBottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {}
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                mHeaderImage.setRotation(slideOffset * 180);
            }
        });
    }

    private void start() {

        mDriversFoundProvider.allDelete();
        allData = recuperarDatos();


        //recuperar datos
        if(!allData)
        {
            mClientBookingProvider.getClientBooking(mAuthProvider.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists())
                    {
                        mClientBooking.setIdCliente(mAuthProvider.getId());
                        mClientBooking.setIdDriver(snapshot.child("idDriver").getValue().toString());
                        mClientBooking.setId(snapshot.child("id").getValue().toString());
                        mClientBooking.setType(snapshot.child("type").getValue().toString());
                        mClientBooking.setStatus(snapshot.child("status").getValue().toString());
                        mClientBooking.setCantidad30kg(Integer.parseInt(snapshot.child("cantidad30kg").getValue().toString()));
                        mClientBooking.setCantidad20kg(Integer.parseInt(snapshot.child("cantidad20kg").getValue().toString()));
                        mClientBooking.setCantidad10kg(Integer.parseInt(snapshot.child("cantidad10kg").getValue().toString()));
                        mClientBooking.setEtiqueta(snapshot.child("etiqueta").getValue().toString());
                        mClientBooking.setCalle(snapshot.child("calle").getValue().toString());
                        mClientBooking.setColonia(snapshot.child("colonia").getValue().toString());
                        mClientBooking.setCodigo_postal(snapshot.child("codigo_postal").getValue().toString());
                        mClientBooking.setDireccion(snapshot.child("direccion").getValue().toString());

                        mClientBooking.setNumExterior(snapshot.child("numExterior").getValue().toString());
                        mClientBooking.setNumInterior(snapshot.child("numInterior").getValue().toString());
                        mClientBooking.setSParticular(snapshot.child("SParticular").getValue().toString());


                        mClientBooking.setEmpresa(Integer.parseInt(snapshot.child("empresa").getValue().toString()));
                        mClientBooking.setRuta(snapshot.child("ruta").getValue().toString());

                        double latitud = (double) snapshot.child("latitud").getValue();
                        double longitud = (double) snapshot.child("longitud").getValue();
                        mClientBooking.setLatitud(latitud);
                        mClientBooking.setLongitud(longitud);

                        Log.d("DEP", "Datos recuperados");

                        load_status();

                        imprimirDatos1();
                    }
                    else
                    {
                        Toast.makeText(HomeStatusServicio.this, "Error: no hay servicio disponible.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else
            load_status();

    }

    LinearLayout lnCantidad30kg, lnCantidad20kg, lnCantidad10kg;
    TextView tvCantidad30kg, tvCantidad20kg, tvCantidad10kg, tvSubTotal30kg, tvSubTotal20kg, tvSubTotal10kg;
    private void imprimirDatos1() {
        runOnUiThread(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                if(mClientBooking.getCantidad30kg()!=0)
                {
                    lnCantidad30kg.setVisibility(View.VISIBLE);
                    tvSubTotal30kg.setText(Home.mPrice.getPrecio(mClientBooking.getEmpresa(), 30*mClientBooking.getCantidad30kg()));
                    tvCantidad30kg.setText(String.valueOf(mClientBooking.getCantidad30kg()));
                }

                if(mClientBooking.getCantidad20kg()!=0) {
                    lnCantidad20kg.setVisibility(View.VISIBLE);
                    tvSubTotal20kg.setText(Home.mPrice.getPrecio(mClientBooking.getEmpresa(), 20*mClientBooking.getCantidad20kg()));
                    tvCantidad20kg.setText(String.valueOf(mClientBooking.getCantidad20kg()));
                }
                if(mClientBooking.getCantidad10kg()!=0) {
                    lnCantidad10kg.setVisibility(View.VISIBLE);
                    tvSubTotal10kg.setText(Home.mPrice.getPrecio(mClientBooking.getEmpresa(), 10*mClientBooking.getCantidad10kg()));
                    tvCantidad10kg.setText(String.valueOf(mClientBooking.getCantidad10kg()));
                }

                tvTotal.setText(Home.mPrice.getTotal(mClientBooking.getEmpresa(), mClientBooking.getCantidad30kg(), mClientBooking.getCantidad20kg(), mClientBooking.getCantidad10kg()));

                tvCalle.setText(mClientBooking.getCalle());
                tvColonia.setText(mClientBooking.getColonia().trim() + ", " + mClientBooking.getCodigo_postal().trim());
                tvSParticular.setText(mClientBooking.getSParticular().trim());
            }
        });
    }




    //35 SEGUNDOS PARA CANCELAR
    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mTimeLimit < 120) { // SEGUNDOS
                mTimeLimit++;
                mHandler.postDelayed(mRunnable, 1000);

                if(isAcepted)
                {
                    deleteDriversFound();
                    mDriversFoundProvider.allDelete();
                    mHandler.removeCallbacks(mRunnable);
                }
            }
            else {
                if(!isAcepted)
                {
                    cancelRequest();
                }
                deleteDriversFound();
                mDriversFoundProvider.allDelete();
                mHandler.removeCallbacks(mRunnable);
            }
        }
    };



    private int mTimeLimitFinish = 0;

    private Handler mHandlerFinish = new Handler();
    //35 SEGUNDOS PARA Finalizar
    Runnable mRunnableFinish = new Runnable() {
        @Override
        public void run() {
            if (mTimeLimitFinish < 10) { // SEGUNDOS
                mTimeLimitFinish++;
                mHandlerFinish.postDelayed(mRunnableFinish, 1000);
            }
            else {
                mHandlerFinish.removeCallbacks(mRunnableFinish);
                Intent intent = new Intent(HomeStatusServicio.this, HomeCalificacion.class);
                startActivity(intent);
                finish();
            }
        }
    };




    private void cancelRequest() {

        mClientBookingProvider.delete(mAuthProvider.getId()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                sendNotificationCancel();
            }
        });
    }

    private void sendNotificationCancel() {

        if (mTokenList.size() > 0) {
            //String token = dataSnapshot.child("token").getValue().toString();
            Map<String, String> map = new HashMap<>();
            map.put("title", "VIAJE CANCELADO");
            map.put("body",
                    "El cliente cancelo la solicitud"
            );
            FCMBody fcmBody = new FCMBody(mTokenList, "high", "4500s", map);
            mNotificationProvider.sendNotification(fcmBody).enqueue(new Callback<FCMResponse>() {
                @Override
                public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(HomeStatusServicio.this, "La solicitud se cancelo correctamente", Toast.LENGTH_SHORT).show();//
                        }
                    });

                    Intent intent = new Intent(HomeStatusServicio.this, Home.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(Call<FCMResponse> call, Throwable t) {
                    Log.d("Error", "Error " + t.getMessage());
                }
            });
        }
        else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(HomeStatusServicio.this, "La solicitud se cancelo correctamente", Toast.LENGTH_SHORT).show();//
                }
            });

            Intent intent = new Intent(HomeStatusServicio.this, Home.class);
            startActivity(intent);
            finish();
        }

    }

    private void animacion1()
    {
        new Thread(new Runnable(){
            @Override
            public void run()
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        animacion1.setVisibility(View.VISIBLE);
                        animacion1.playAnimation();
                        //animacion1.cancelAnimation();
                        //animacion1.loop(false);//
                    }
                });
            }
        }).start();
    }

    private void stopAnimacion1()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        animacion1.setVisibility(View.INVISIBLE);
                        animacion1.cancelAnimation();
                        animacion1.loop(false);//
                    }
                });
            }
        });
    }

    private void animacion2()
    {
        new Thread(new Runnable(){
            @Override
            public void run()
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        animacion1.setVisibility(View.INVISIBLE);
                        animacion2.setVisibility(View.VISIBLE);
                        animacion2.playAnimation();
                        stopAnimacion1();//
                    }
                });/////HILO
            }
        }).start();

    }

    private void stopAnimacion2()
    {
        new Thread(new Runnable(){
            @Override
            public void run()
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        animacion2.setVisibility(View.INVISIBLE);
                        animacion2.cancelAnimation();
                        animacion2.loop(false);//
                    }
                });/////HILO
            }
        }).start();

    }

    private void animacion3()
    {
        new Thread(new Runnable(){
            @Override
            public void run()
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        animacion2.setVisibility(View.INVISIBLE);
                        //animacion3.setVisibility(View.VISIBLE);
                        //animacion3.playAnimation();
                        camion.setVisibility(View.VISIBLE);
                        stopAnimacion1();//
                    }
                });/////HILO
            }
        }).start();
    }

    private void stopAnimacion3()
    {
        new Thread(new Runnable(){
            @Override
            public void run()
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        animacion3.setVisibility(View.INVISIBLE);
                        animacion3.cancelAnimation();
                        animacion3.loop(false);//
                    }
                });/////HILO
            }
        }).start();
    }

    private void deleteDriversFound() {
        try {
            for (String idDriver: mDriversFoundList) {
                mDriversFoundProvider.delete(idDriver);
            }
        }catch (Exception e) {}
    }

    private boolean recuperarDatos() {
        mClientBooking.setCantidad30kg(getIntent().getIntExtra("cantidad30kg", 100));
        mClientBooking.setCantidad20kg(getIntent().getIntExtra("cantidad20kg", 100));
        mClientBooking.setCantidad10kg(getIntent().getIntExtra("cantidad10kg", 100));
        mClientBooking.setId(getIntent().getStringExtra("id"));
        mClientBooking.setType(getIntent().getStringExtra("type"));
        mClientBooking.setStatus(getIntent().getStringExtra("status"));


        int posicion = getIntent().getIntExtra("posicion", 100);

        if(mClientBooking.isValData(posicion))
        {
            mClientBooking = new ClientBooking(mAuthProvider.getId(), "", mClientBooking.getId(), mClientBooking.getType(), mClientBooking.getStatus(), mClientBooking.getCantidad30kg(), mClientBooking.getCantidad20kg(), mClientBooking.getCantidad10kg(), Home.mDirecciones.get(posicion));
            imprimirDatos1();
            return true;
        }
        return false;

    }

    TextView tvCalle, tvColonia, tvSParticular, tvTotal;
    LinearLayout infoOperador;
    private void declaration() {

        animacion1 = findViewById(R.id.animation1);
        animacion2 = findViewById(R.id.animation2);
        animacion3 = findViewById(R.id.animation3);
        camion = findViewById(R.id.camion);
        infoOperador = findViewById(R.id.infoOperador);

        tvSubTotal30kg = findViewById(R.id.tvSubTotal30kg);
        tvSubTotal20kg = findViewById(R.id.tvSubTotal20kg);
        tvSubTotal10kg = findViewById(R.id.tvSubTotal10kg);

        lnCantidad30kg = findViewById(R.id.lnCantidad30kg);
        lnCantidad20kg = findViewById(R.id.lnCantidad20kg);
        lnCantidad10kg = findViewById(R.id.lnCantidad10kg);
        tvCantidad30kg = findViewById(R.id.tvCantidad30kg);
        tvCantidad20kg = findViewById(R.id.tvCantidad20kg);
        tvCantidad10kg = findViewById(R.id.tvCantidad10kg);

        tvUnidadOperador = findViewById(R.id.tvUnidadOperador);
        tvNombreOperador = findViewById(R.id.tvNombreOperador);
        tvApellidoOperador = findViewById(R.id.tvApellidoOperador);

        tvTotal = findViewById(R.id.tvTotal);

        tvStatus = findViewById(R.id.tvStatus);
        tvSubStatus = findViewById(R.id.tvSubStatus);


        tvCalle = findViewById(R.id.tvCalle);
        tvColonia = findViewById(R.id.tvColonia);
        tvSParticular = findViewById(R.id.tvSParticular);


        time1 = findViewById(R.id.time1);
        time2 = findViewById(R.id.time2);
        time3 = findViewById(R.id.time3);

    }

    private void load_status() {
        mClientBookingProvider.getStatus(mAuthProvider.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    String status = snapshot.getValue().toString();

                    if(status.equals("new"))
                        buscando_operador();
                    else if(status.equals("create"))
                    {
                        mHandler.postDelayed(mRunnable, 1000);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvStatus.setText("ESPERANDO RESPUESTA...");
                            }
                        });

                        checkStatusClientBooking();

                    }
                    else if(status.equals("accept"))
                    {
                        serviceAcepted("");
                    }

                    else if(status.equals("finish"))
                    {
                        isFinish();
                    }
                    else
                        finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




    private boolean mDriverFound = false;
    private String  mIdDriverFound = "";
    private LatLng mDriverFoundLatLng;

    @SuppressLint("SetTextI18n")
    private void buscando_operador() {
        /*new Thread(new Runnable(){
            @Override
            public void run()
            {*/
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvStatus.setText("Buscando operador...");
                    }
                });

                animacion1();



                mGeofireProvider.getActiveDrivers(new LatLng(mClientBooking.getLatitud(), mClientBooking.getLongitud()), mRadius).addGeoQueryEventListener(new GeoQueryEventListener() {

                    @Override
                    public void onKeyEntered(String key, GeoLocation location) {
                        mDriversFoundList.add(key);
                        tokenTemp = key;
                        Log.d("DEP", "Conductor encontrado: " + key);
                    }

                    @Override
                    public void onKeyExited(String key) {}

                    @Override
                    public void onKeyMoved(String key, GeoLocation location) {}

                    @Override
                    public void onGeoQueryReady() {
                        // YA FINALIZA LA BUSQUEDA EN UN RADIO DE 3 KILOMETROS
                        checkIfDriverIsAvailable();
                    }

                    @Override
                    public void onGeoQueryError(DatabaseError error) {}
                });

            //}
        //}).start();
    }

    private int mCounterDriversAvailable = 0;
    private int mCounter = 0;

    private void checkIfDriverIsAvailable () {
        new Thread(new Runnable(){
            @Override
            public void run()
            {
                for (String idDriver: mDriversFoundList) {
                    mDriversFoundProvider.getDriverFoundByIdDriver(idDriver).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            mCounterDriversAvailable = mCounterDriversAvailable + 1;

                            for (DataSnapshot d: snapshot.getChildren()) {
                                if (d.exists()) {
                                    String idDriver = d.child("idDriver").getValue().toString();
                                    // ELIMINO DE LA LISTA DE CONDUCTORES ENCONTRADOS EL CONDUCTOR QUE YA EXISTE EN EL NODO
                                    // DriversFound PARA NO ENVIARLE LA NOTIFICACION
                                    mDriversFoundList.remove(idDriver);
                                    mCounterDriversAvailable = mCounterDriversAvailable - 1;

                                }
                            }

                            // YA SABEMOS QUE LA CONSULTA TERMINO
                            // ASEGURAMOS DE NO ENVIARLE LA NOTIFICACION A LOS CONDUCTORES QUE YA ESTAN ACTUALMENTE RECIBIENDO LA
                            // NOTIFICACION
                            if (mCounterDriversAvailable == mDriversFoundList.size()) {
                                getDriversToken();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        }).start();





    }

    private void getDriversToken() {


        if (mDriversFoundList.size() == 0) {
            buscando_operador();
            return;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvStatus.setText("ESPERANDO RESPUESTA...");
            }
        });



        for (String id: mDriversFoundList) {
            mTokenProvider.getTokenOp(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mCounter = mCounter + 1;

                    if (snapshot.exists()) {
                        String token = snapshot.child("token").getValue().toString();
                        mTokenList.add(token);

                        //tokenTemp = token;
                    }

                    if (mCounter == mDriversFoundList.size()) {
                        sendNotification();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }




    }

    String tokenTemp;

    private void sendNotification() {
        Log.d("DEP", "ENTRA A SEND NOTIFICATION");
        Map<String, String> map = new HashMap<>();


        map.put("idClient", mAuthProvider.getId()); //YA
        map.put("destination", mClientBooking.getDireccion()); //YA


        map.put("cantidad30kg", String.valueOf(mClientBooking.getCantidad30kg()));
        map.put("cantidad20kg", String.valueOf(mClientBooking.getCantidad20kg()));
        map.put("cantidad10kg", String.valueOf(mClientBooking.getCantidad10kg()));

        map.put("idServicio", mClientBooking.getId());

        map.put("latitud", String.valueOf(mClientBooking.getLatitud()));
        map.put("longitud", String.valueOf(mClientBooking.getLongitud()));


        map.put("title", "Solicitud de servicio a " + "" + " de tu ubicación."); //YA

        //map.put("min", durationText); //YA
        //map.put("distance", distanceText); //YA
        map.put("searchById", "false");


        String cantidad ="";
        String salto= "";
        if (mClientBooking.getCantidad30kg() != 0)
        {
            cantidad = "Cil 30kg: " + mClientBooking.getCantidad30kg();
        }
        if (mClientBooking.getCantidad20kg() != 0)
        {
            if(mClientBooking.getCantidad30kg()>=1)
                salto = "\n";
            else
                salto = "";
            cantidad = cantidad + salto + "Cil 20kg: " + mClientBooking.getCantidad20kg();
        }
        if (mClientBooking.getCantidad10kg() != 0)
        {
            if(mClientBooking.getCantidad30kg()>=1 || mClientBooking.getCantidad20kg()>=1)
                salto = "\n";
            else
                salto = "";
            cantidad = cantidad + salto + "Cil 10kg: " + mClientBooking.getCantidad10kg();
        }

        map.put("body",
                "Un cliente esta solicitando un servicio a una distancia de " + "" + ".\n" +
                        "En el domicilio: " + mClientBooking.getCalle() + "\n" +
                        cantidad
        ); //YA

        map.put("cantidad", cantidad);

        //Log.d("DEP", "Id Driver: " + idDriver + " Distancia" + distanceText + "duración: " + durationText);

        FCMBody fcmBody = new FCMBody(mTokenList, "high", "4500s", map);

        mNotificationProvider.sendNotification(fcmBody).enqueue(new Callback<FCMResponse>() {
            @Override
            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                // ESTAMOS RECORRIENDO LA LISTA DE LOS CONDUCTORES ENCONTRADOS PARA ALMACENARLOS EN FIREBASE
                for (String idDriver : mDriversFoundList) {
                    Log.d("DEP", "ENTRA A bucle");
                    DriverFound driverFound = new DriverFound(idDriver, mAuthProvider.getId());
                    mDriversFoundProvider.create(driverFound);
                }

                ///* create
                mClientBookingProvider.updateStatus(mAuthProvider.getId(), "create").addOnCompleteListener(taskCreate -> {
                    if (taskCreate.isSuccessful()) {
                        Log.d("DEP", "ENTRA A Booking Create");
                        mHandler.postDelayed(mRunnable, 1000);
                        checkStatusClientBooking();
                    }
                });

                //
            }

            @Override
            public void onFailure(Call<FCMResponse> call, Throwable t) {
                Log.d("DEP", "Error " + t.getMessage());
            }


        });
    }

    private void checkStatusClientBooking() {
        mListener = mClientBookingProvider.getClientBooking(mAuthProvider.getId()).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String status = dataSnapshot.child("status").getValue().toString();
                    String idDriver = dataSnapshot.child("idDriver").getValue().toString();
                    if (status.equals("accept") && !idDriver.equals("")) {

                        sendNotificationCancelToDrivers(idDriver);
                        isAcepted = true;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(HomeStatusServicio.this, "La solicitud ha sido aceptada", Toast.LENGTH_SHORT).show();////
                            }
                        });



                        serviceAcepted(idDriver);




                        /*
                        Intent intent = new Intent(HomeStatusServicio.this, MapClientBookingActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                         */


                    } else if (status.equals("cancel")) {
                        /*
                        if (mIsLookingFor) {
                            restartRequest();
                        }

                         */
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(HomeStatusServicio.this, "El conductor no acepto el viaje", Toast.LENGTH_SHORT).show();//
                            }
                        });

                        /*
                        Intent intent = new Intent(RequestDriverActivity.this, MapClientActivity.class);
                        startActivity(intent);
                        finish();

                         */
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void serviceAcepted(String idDriver) {

        String[] idOperador = new String[1];
        if(idDriver.equals(""))
        {
            //Recuperar ID DRIVER
            mClientBookingProvider.getClientBooking(mAuthProvider.getId()).child("idDriver").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists())
                    {
                        idOperador[0] = snapshot.getValue().toString();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }

        isAcepted = true;
        mDriversFoundProvider.allDelete();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                stopAnimacion1();
                animacion2();

                tvStatus.setText("Operador encontrado.");
                tvSubStatus.setText("El operador ha aceptado la solicitud de servicio.");
                time1.setIndeterminate(false);
                time2.setIndeterminate(true);
                time1.setProgress(100);//
            }
        });

        new Thread(new Runnable(){
            @Override
            public void run()
            {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvStatus.setText("Solicitando datos del operador.");
                    }
                });

                String idFinal;
                if(idDriver.equals(""))
                {
                    idFinal = idOperador[0];
                }
                else
                    idFinal = idDriver;

                mDriverProvider.getDriver(idFinal).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {

                            mDriver.setNombre(snapshot.child("nombre").getValue().toString());
                            mDriver.setApellido(snapshot.child("apellido").getValue().toString());
                            mDriver.setTelefono(snapshot.child("telefono").getValue().toString());
                            //mDriver.setUnidad(snapshot.child("unidad").getValue().toString());
                            mDriver.setUnidad("Unidad: ");

                            tvSubStatus.setText("Se han recibido correctamente los datos del operador.");


                            imprimirDatos2();



                            new Thread(new Runnable(){
                                @Override
                                public void run()
                                {
                                    try {
                                        Thread.sleep(2000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            stopAnimacion2();
                                            animacion3();

                                            tvStatus.setText("Servicio en camino.");
                                            tvSubStatus.setText("Tu servicio está en camino.");
                                            time2.setIndeterminate(false);
                                            time3.setIndeterminate(true);
                                            time2.setProgress(100);
                                        }
                                    });


                                    checkStatusFinish();


                                }
                            }).start();






                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }


        }).start();

    }

    TextView tvUnidadOperador, tvNombreOperador, tvApellidoOperador;
    private void imprimirDatos2() {
        runOnUiThread(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                infoOperador.setVisibility(View.VISIBLE);
                tvUnidadOperador.setText(mDriver.getUnidad());
                tvNombreOperador.setText(mDriver.getNombre() + " " + mDriver.getApellido());



            }
        });
    }

    private void checkStatusFinish() {

        if (mListener != null) {
            mClientBookingProvider.getStatus(mAuthProvider.getId()).removeEventListener(mListener);
        }

        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
        }

        checkStatusClientBookingFinish();
    }


    private void checkStatusClientBookingFinish() {
        mListener = mClientBookingProvider.getClientBooking(mAuthProvider.getId()).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String status = dataSnapshot.child("status").getValue().toString();

                    if (status.equals("finish")) {
                        isFinish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void sendNotificationCancelToDrivers(String idDriver) {

        if (mTokenList.size() > 0) {
            //String token = dataSnapshot.child("token").getValue().toString();
            Map<String, String> map = new HashMap<>();
            map.put("title", "VIAJE CANCELADO");
            map.put("body",
                    "El cliente cancelo la solicitud"
            );

            // ELIMINAR DE LA LISTA DE TOKEN
            // EL TOKEN DEL CONDUCTOR QUE ACEPTO EL VIAJE
            mTokenList.remove(idDriver);

            FCMBody fcmBody = new FCMBody(mTokenList, "high", "4500s", map);
            mNotificationProvider.sendNotification(fcmBody).enqueue(new Callback<FCMResponse>() {
                @Override
                public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {

                }

                @Override
                public void onFailure(Call<FCMResponse> call, Throwable t) {
                    Log.d("Error", "Error " + t.getMessage());
                }
            });
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
    public void onBackPressed() {
        backPress();
    }

    private void backPress() {
        this.moveTaskToBack(true);
    }
    //BACK PRESS


    private ValueEventListener mListener;
    private boolean mIsFinishSearch = false;

    @Override
    protected void onDestroy() {

        if (mListener != null) {
            mClientBookingProvider.getStatus(mAuthProvider.getId()).removeEventListener(mListener);
        }

        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
        }

        if (mHandlerFinish != null) {
            mHandlerFinish.removeCallbacks(mRunnableFinish);
        }

        mIsFinishSearch = true;

        super.onDestroy();
    }
}
