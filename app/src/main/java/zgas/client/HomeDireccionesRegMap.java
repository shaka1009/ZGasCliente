package zgas.client;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.List;

import zgas.client.includes.Toolbar;

public class HomeDireccionesRegMap extends AppCompatActivity implements OnMapReadyCallback {

    private final static int AUTOCOMPLETE_REQUEST_CODE = 10;
    private final static int LOCATION_REQUEST_CODE = 1;
    private final static int REQUEST_CHECK_SETTINGS = 0x1;

    private GoogleMap mMap;
    private GoogleMap.OnCameraIdleListener mCameraListener;
    private Geocoder geocoder;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;

    private FusedLocationProviderClient fusedLocationClient;

    private ConnectivityManager cm;
    private TextView tvDomicilio;
    private FloatingActionButton locationNow;
    private Button btnContinuar;
    private LatLng mLatLng;
    String address;
    private String calle="", colonia="", codigo_postal="";
    double latitudIntent, longitudIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_domicilios_reg_map);
        Toolbar.show(this, true);
        cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        geocoder = new Geocoder(HomeDireccionesRegMap.this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        declaration();
        places();
        onCameraMove();
        listenner();

    }

    private void firstTime() {
        List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS_COMPONENTS, Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .setTypeFilter(TypeFilter.ADDRESS)
                .setCountry("Mex")
                .setInitialQuery(calle)
                .build(HomeDireccionesRegMap.this);
        // Start the autocomplete intent.
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);

    }

    private void declaration() {
        mGoogleApiClient = getAPIClientInstance();
        mGoogleApiClient.connect();
        locationNow = findViewById(R.id.locationNow);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        btnContinuar = findViewById(R.id.btnContinuar);

    }

    private void listenner() {
        locationNow.setOnClickListener(v -> Location_Now());

        btnContinuar.setOnClickListener(this::Add_Domicilio);

        tvDomicilio.setOnClickListener(v -> {
            firstTime();
        });
    }

    private void Location_Now() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (gpsActived()) {
                    //mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                    ubicacion_actual();
                } else {
                    //showAlertDialogNOGPS();
                    requestGPSSettings();
                }
            } else {
                checkLocationPermissions();
            }
        } else {
            if (gpsActived()) {
                //mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                ubicacion_actual();
            } else {
                //showAlertDialogNOGPS();
                requestGPSSettings();
            }
        }
    }

    private boolean buttonPress=false;
    private void Add_Domicilio(View v)
    {
        new Thread(new Runnable(){
            @Override
            public void run()
            {
                if(buttonPress)
                    return;
                else buttonPress = true;

                if(calle.equals("") || colonia.equals("") || codigo_postal.equals("") || mLatLng == null)
                {
                    runOnUiThread(() -> Toast.makeText(HomeDireccionesRegMap.this, "Es inválido el domicilio.", Toast.LENGTH_SHORT).show());
                }

                else if(isValidData())
                {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("calle", calle);
                    resultIntent.putExtra("colonia", colonia);
                    resultIntent.putExtra("codigo_postal", codigo_postal);
                    resultIntent.putExtra("address", address);
                    resultIntent.putExtra("latitud", mLatLng.latitude);
                    resultIntent.putExtra("longitud", mLatLng.longitude);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
                else
                {
                    if(!isConnected())
                    {
                        runOnUiThread(() -> {
                            Snackbar snackbar1;
                            snackbar1 = Snackbar.make(v, "No hay conexión a internet, no puedes registrar la dirección.", Snackbar.LENGTH_SHORT);
                            View snackBarView = snackbar1.getView();
                            snackBarView.setBackgroundColor(getResources().getColor(R.color.rojoAlert));
                            snackbar1.show();
                        });
                    }
                    else
                    {
                        runOnUiThread(() -> Toast.makeText(HomeDireccionesRegMap.this, "Zona no disponible.", Toast.LENGTH_SHORT).show());
                    }
                }


                SleepButton();
                buttonPress = false;
            }

            private boolean isValidData() {
                if (address.contains("Jalisco") || address.contains("Jal.")) {
                    Log.d("DEP", "Es de Jalisco.");
                    if (address.contains("Tlajomulco de Zúñiga")) {
                        //Log.d("DEP", "Municipio: Tlajomulco de Zúñiga.");
                        return true;
                    } else if (address.contains("Zapopan")) {
                        //Log.d("DEP", "Municipio: Zapopan.");
                        return true;
                    } else if (address.contains("Guadalajara")) {
                        //Log.d("DEP", "Municipio: Guadalajara.");
                        return true;/*
                    } else if (address.contains("Tequila")) {
                        //Log.d("DEP", "Municipio: Tequila.");
                        return true;*/
                    } else //Log.d("DEP", "Municipio: Tlaquepaque.");
                        if (address.contains("Tonalá")) {
                        //Log.d("DEP", "Municipio: Tonalá.");
                        return true;
                    }
                    else //Log.d("DEP", "Municipio: El Salto.");
                        if (address.contains("El Salto") || address.contains("Salto"))
                        {
                            //Log.d("DEP", "Municipio: El Salto.");
                            return true;
                        }
                    else return address.contains("San Pedro Tlaquepaque") || address.contains("Tlaquepaque");
                }
                else
                    return false;
            }

            private void SleepButton()
            {
                new Thread(() -> {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    buttonPress = false;
                }).start();
            }

        }).start();
    }

    private boolean isConnected(){
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }



    private GoogleApiClient getAPIClientInstance() {
        return new GoogleApiClient.Builder(this).addApi(LocationServices.API).build();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (gpsActived()) {
                        //mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        ubicacion_actual();
                    } else {
                        //showAlertDialogNOGPS();
                        requestGPSSettings();
                    }
                } else {
                    checkLocationPermissions();
                }
            } else {
                checkLocationPermissions();
            }
        }
    }

    private void requestGPSSettings() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        final PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(locationSettingsResult -> {
            Status status = locationSettingsResult.getStatus();

            if (status.getStatusCode() == LocationSettingsStatusCodes.SUCCESS) {
                Toast.makeText(HomeDireccionesRegMap.this, "El GPS ya está activado", Toast.LENGTH_SHORT).show();
            } else if (status.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                try {
                    status.startResolutionForResult(HomeDireccionesRegMap.this, REQUEST_CHECK_SETTINGS);
                    if (ActivityCompat.checkSelfPermission(HomeDireccionesRegMap.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HomeDireccionesRegMap.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    //mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                    ubicacion_actual();
                } catch (IntentSender.SendIntentException e) {
                    Toast.makeText(HomeDireccionesRegMap.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            } else if (status.getStatusCode() == LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE) {
                Toast.makeText(HomeDireccionesRegMap.this, "La configuración del GPS tiene algún error o está disponible.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean gpsActived() {
        boolean isActive = false;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            isActive = true;
        }
        return isActive;
    }

    private void checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Proporciona los permisos para continuar")
                        .setMessage("Esta aplicación requiere de los permisos de ubicación para poder utilizarse.")
                        .setPositiveButton("OK", (dialogInterface, i) -> ActivityCompat.requestPermissions(HomeDireccionesRegMap.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE))
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(HomeDireccionesRegMap.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
    }

    private void ubicacion_actual() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                // Logic to handle location object
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                        new CameraPosition.Builder()
                                .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                .zoom(15f)
                                .build()
                ));

                if (ActivityCompat.checkSelfPermission(HomeDireccionesRegMap.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HomeDireccionesRegMap.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(true);
            }
        });
    }

    private void onCameraMove() {
        mCameraListener = () -> {
            mLatLng = mMap.getCameraPosition().target;
            new Thread(() -> {
                try {
                    List<Address> addressList = geocoder.getFromLocation(mLatLng.latitude, mLatLng.longitude, 1);
                    String city = addressList.get(0).getLocality();
                    address = addressList.get(0).getAddressLine(0);

                    address = address + " " + city;
                    String[] parts = address.split(",");

                    calle = parts[0]; // 123
                    colonia = parts[1]; // 654321
                    codigo_postal = parts[2]; // 654321

                    runOnUiThread(() -> tvDomicilio.setText(calle));

                    Log.d("DEP", "Address: " + address);
                } catch (Exception e) {
                    Log.d("DEP", "Mensaje error: " + e.getMessage());
                    address = "";
                    colonia = "";
                    codigo_postal = "";
                }
            }).start();
        };
    }

    private void places() {
        if(!getIntent().getStringExtra("calle").equals(""))
            calle = getIntent().getStringExtra("calle");
        else
            calle="";

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));
        }
        tvDomicilio = findViewById(R.id.tvDomicilio);

        firstTime();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                Place place = Autocomplete.getPlaceFromIntent(data);
                tvDomicilio.setText(place.getName());
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                        new CameraPosition.Builder()
                                .target(place.getLatLng())
                                .zoom(15f)
                                .build()
                ));
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                assert data != null;
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(HomeDireccionesRegMap.this, "Error: " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
            }  // The user canceled the operation.

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setOnCameraIdleListener(mCameraListener);

        //DEP
        latitudIntent = getIntent().getDoubleExtra("latitud", 20.6340165);
        longitudIntent = getIntent().getDoubleExtra("longitud", -103.3536772);

        if(latitudIntent == 0)
            latitudIntent = 20.6340165;
        if(longitudIntent == 0)
            longitudIntent = -103.3536772;

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                        .target(new LatLng(latitudIntent, longitudIntent))
                        .zoom(15f)
                        .build()
        ));


        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(5);

        //Location_Now();
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            backPress();
        }
        return super.onOptionsItemSelected(item);
    }
    //BACK PRESS

    @Override
    public void onBackPressed() {
        backPress();
    }

    //DEP
    private void backPress() {
        finish();
    }
    //BACK PRESS
}