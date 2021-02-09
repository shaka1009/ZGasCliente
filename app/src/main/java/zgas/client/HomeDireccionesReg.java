package zgas.client;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import zgas.client.includes.Popup;
import zgas.client.includes.Toolbar;
import zgas.client.models.Direcciones;
import zgas.client.providers.AuthProvider;
import zgas.client.providers.DireccionProvider;
import zgas.client.tools.Validacion;

public class HomeDireccionesReg extends AppCompatActivity {

    private Popup mPopup;
    private EditText etEtiqueta, etNumExt, etNumInt, etSParticular;
    private LinearLayout llEtiqueta, llDireccion, llNumExt, llNumInt, llSParticular;
    private TextView tvDomicilio;
    private Button btnAgregarDomicilio;
    private ProgressBar pbAgregarDomicilio;

    private Direcciones domicilioReg;
    private DireccionProvider mDireccionProvider;

    private ConnectivityManager cm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_domicilios_reg);
        Toolbar.show(this, true);
        mPopup = new Popup(this, getApplicationContext(), findViewById(R.id.popupError));
        domicilioReg = new Direcciones();
        AuthProvider mAuthProvider = new AuthProvider();
        mDireccionProvider = new DireccionProvider(mAuthProvider.getId());
        cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        declaration();
        listenner();

        firstTime();
    }

    private void firstTime() {
        Intent intent = new Intent(HomeDireccionesReg.this, HomeDireccionesRegMap.class);
        intent.putExtra("calle", domicilioReg.getCalle());
        startActivityForResult(intent, 1);
        etEtiqueta.requestFocus();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                domicilioReg.setCalle(data.getStringExtra("calle"));
                tvDomicilio.setText(domicilioReg.getCalle());
                domicilioReg.setColonia(data.getStringExtra("colonia"));
                domicilioReg.setCodigo_postal(data.getStringExtra("codigo_postal"));
                domicilioReg.setDomicilioLargo(data.getStringExtra("address"));
                double latitud = data.getDoubleExtra("latitud", 0);
                double longitud= data.getDoubleExtra("longitud", 0);
                domicilioReg.setmLatLng(new LatLng(latitud, longitud));
            }
            //mTextViewResult.setText("Nothing selected");
        }
    }

    private void declaration() {
        etEtiqueta = findViewById(R.id.etEtiqueta);
        etNumExt = findViewById(R.id.etNumExt);
        etNumInt = findViewById(R.id.etNumInt);
        etSParticular = findViewById(R.id.etSParticular);

        tvDomicilio = findViewById(R.id.tvDomicilio);

        llEtiqueta  = findViewById(R.id.llEtiqueta);
        llDireccion = findViewById(R.id.llDomicilio);
        llNumExt = findViewById(R.id.llNumExt);
        llNumInt = findViewById(R.id.llNumInt);
        llSParticular = findViewById(R.id.llSParticular);

        btnAgregarDomicilio = findViewById(R.id.btnAgregarDomicilio);
        pbAgregarDomicilio = findViewById(R.id.pbAgregarDomicilio);
    }


    private boolean pressButton = false;
    private void listenner() {

        btnAgregarDomicilio.setOnClickListener(this::btnAgregar);
                
                
        llEtiqueta.setOnClickListener(v -> {
            if(pressButton)
                return;
            else pressButton = true;

            etEtiqueta.requestFocus();


            SleepButton();
            loading(false);
        });

        llDireccion.setOnClickListener(v -> clickDomicilio());

        tvDomicilio.setOnClickListener(v -> clickDomicilio());

        llNumExt.setOnClickListener(v -> {
            if(pressButton)
                return;
            else pressButton = true;

            etNumExt.requestFocus();

            SleepButton();
            loading(false);
        });

        llNumInt.setOnClickListener(v -> {
            if(pressButton)
                return;
            else pressButton = true;

            etNumInt.requestFocus();


            SleepButton();
            loading(false);
        });

        llSParticular.setOnClickListener(v -> {
            if(pressButton)
                return;
            else pressButton = true;

            etSParticular.requestFocus();


            SleepButton();
            loading(false);
        });



        //Esta mamada no sirve
        etSParticular.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                new Thread(() -> {
                    int ssidLength = charSequence.length();
                    if (ssidLength == 0) {
                        return;
                    }
                    if (charSequence.charAt(ssidLength - 1)=='\n'){
                        UIUtil.hideKeyboard(HomeDireccionesReg.this); //ESCONDER TECLADO

                        //String newText = (String) charSequence;
                        //newText.replace('\n', ' ');

                    }
                }).start();

            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });


    }

    private void btnAgregar(View v) {
        new Thread(() -> {
            if(pressButton)
                return;
            else pressButton = true;

            loading(true);

            add_data();


            if(isValidData(v))
            {
                //if()

                createDomicilios(v);
            }

            SleepButton();
        }).start();

    }

    private void loading(boolean b) {
        if(b)
        {
            runOnUiThread(() -> {
                etEtiqueta.setEnabled(false);
                tvDomicilio.setEnabled(false);
                etNumExt.setEnabled(false);
                etNumInt.setEnabled(false);
                etSParticular.setEnabled(false);
                btnAgregarDomicilio.setVisibility(View.INVISIBLE);
                pbAgregarDomicilio.setVisibility(View.VISIBLE);
            });
        }
        else
        {
            runOnUiThread(() -> {
                etEtiqueta.setEnabled(true);
                tvDomicilio.setEnabled(true);
                etNumExt.setEnabled(true);
                etNumInt.setEnabled(true);
                etSParticular.setEnabled(true);
                btnAgregarDomicilio.setVisibility(View.VISIBLE);
                pbAgregarDomicilio.setVisibility(View.INVISIBLE);
            });
        }

    }

    private void add_data() {
        domicilioReg.setEtiqueta(etEtiqueta.getText().toString().trim());
        domicilioReg.setNumExterior(etNumExt.getText().toString().trim());
        domicilioReg.setNumInterior(etNumInt.getText().toString().trim());
        domicilioReg.setSParticular(etSParticular.getText().toString().replace('\n', ' ').trim());
        domicilioReg.setRuta(domicilioReg.getmLatLng());
    }

    private boolean isValidData(View v) {
        Validacion mVal = new Validacion();
        if(domicilioReg.getEtiqueta().equals("") || mVal.isOnlySpace(domicilioReg.getEtiqueta()))
        {
            runOnUiThread(() -> {
                Snackbar.make(v, "La etiqueta está vacía, por favor escribe una etiqueta.", Snackbar.LENGTH_LONG)
                        .setActionTextColor(getResources().getColor(R.color.white)).show();
                //mPopup.setPopupError("La etiqueta está vacía, por favor escribe una etiqueta.");
                etEtiqueta.requestFocus();
            });
            loading(false);
            return false;
        }


        ///validar etiqueta
        for(int x=0; x<10; x++)
        {
            try {
                Locale locale = new Locale("es", "ES");
                Collator collator = Collator.getInstance(locale);
                collator.setStrength(Collator.PRIMARY);

                if(collator.compare(Home.mDirecciones.get(x).getEtiqueta(), domicilioReg.getEtiqueta()) == 0)
                {
                    Snackbar.make(v, "Ya tienes registrado un domicilio con esta etiqueta.", Snackbar.LENGTH_LONG)
                            .setActionTextColor(getResources().getColor(R.color.white)).show();
                    etEtiqueta.requestFocus();
                    loading(false);
                    return false;
                }
            }
            catch(Exception ignored){}
        }


        if(domicilioReg.getCalle().equals("") || domicilioReg.getCodigo_postal().equals("") || domicilioReg.getColonia().equals("") || domicilioReg.getDomicilioLargo().equals(""))
        {
            runOnUiThread(() -> {
                //Snackbar.make(v, "No has ingresado el domicilio, por favor ingresa el domicilio.", Snackbar.LENGTH_LONG)
                //        .setActionTextColor(getResources().getColor(R.color.white)).show();
                mPopup.setPopupError("No has ingresado el domicilio, por favor ingresa el domicilio.");
            });

             mPopup.btnPoupErrorAceptar.setOnClickListener(v12 -> {
                 mPopup.hidePopupError();
                 Intent intent = new Intent(HomeDireccionesReg.this, HomeDireccionesRegMap.class);
                 intent.putExtra("calle", domicilioReg.getCalle());
                 intent.putExtra("latitud", domicilioReg.getmLatLng().latitude);
                 intent.putExtra("longitud", domicilioReg.getmLatLng().longitude);
                 startActivityForResult(intent, 1);
             });
            loading(false);
            return false;
        }
        else if(domicilioReg.getNumExterior().equals("") || mVal.isOnlySpace(domicilioReg.getNumExterior()))
        {
            runOnUiThread(() -> {
                Snackbar.make(v, "El número exterior es necesario, por favor escribe el número exterior.", Snackbar.LENGTH_LONG)
                        .setActionTextColor(getResources().getColor(R.color.white)).show();
                //mPopup.setPopupError("El número exterior es necesario, por favor escribe el número exterior.");
                etNumExt.requestFocus();
            });
            loading(false);
            return false;
        }
        else if(domicilioReg.getmLatLng().latitude == 0 || domicilioReg.getmLatLng().longitude == 0)
        {
            runOnUiThread(() -> {
                //Snackbar.make(v, "No has ingresado el domicilio, por favor ingresa el domicilio.", Snackbar.LENGTH_LONG)
                //        .setActionTextColor(getResources().getColor(R.color.white)).show();
                mPopup.setPopupError("Error en ubicación, por favor ingresa el domicilio nuevamente.");
            });

            mPopup.btnPoupErrorAceptar.setOnClickListener(v1 -> {
                mPopup.hidePopupError();
                Intent intent = new Intent(HomeDireccionesReg.this, HomeDireccionesRegMap.class);
                intent.putExtra("calle", domicilioReg.getCalle());
                intent.putExtra("latitud", domicilioReg.getmLatLng().latitude);
                intent.putExtra("longitud", domicilioReg.getmLatLng().longitude);
                startActivityForResult(intent, 1);
            });
            loading(false);
            return false;
        }



        //
        return true;
    }



    private void createDomicilios(View v) {

        int slotLibre=100;
        for(int y=0; y<11; y++)
        {
            try {
                if(Home.mDirecciones.get(y).getSlot() != y)
                {
                    slotLibre = y;
                    break;
                }
            }
            catch (Exception e)
            {
                slotLibre = y;
                break;
            }
        }

        if(slotLibre==100)
        {
            Snackbar.make(v, "Has llegado al límite de domicilios.", Snackbar.LENGTH_LONG)
                    .setActionTextColor(getResources().getColor(R.color.white)).show();
        }



        if(!isConnected())
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Snackbar snackbar1;
                    snackbar1 = Snackbar.make(v, "No hay conexión a internet, no puedes registrar la dirección.", Snackbar.LENGTH_SHORT);
                    View snackBarView = snackbar1.getView();
                    snackBarView.setBackgroundColor(getResources().getColor(R.color.rojoAlert));
                    snackbar1.show();
                }
            });
            SleepButton();
            loading(false);
            return;
        }



        Map<String, Object> domicilio_map  = mDireccionProvider.direccionToMap(domicilioReg);

        mDireccionProvider.getDomicilios().child("slot" + slotLibre).setValue(domicilio_map).addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {

                //etiqueta="";
                //domicilio="";
                //domicilioLargo="";

                etEtiqueta.setText("");
                etNumExt.setText("");
                etNumInt.setText("");
                etSParticular.setText("");


                Toast.makeText(HomeDireccionesReg.this, "Domicilio registrado con éxito.", Toast.LENGTH_SHORT).show();
                //btnAgregarDomicilioPress = false;

                //zgas.sleep(1000);
                new Thread(this::load_domicilios).start();
            }
            else
            {
                runOnUiThread(() -> mPopup.setPopupError("Error al agregar domicilio."));
                //btnAgregarDomicilioPress = false;
            }
        });
    }

    private boolean isConnected(){
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    private void load_domicilios()
    {
        Home.mDirecciones = new ArrayList<>();
        new Thread(() -> mDireccionProvider.getDomicilios().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mDireccionProvider.load_domicilios(snapshot);
                finish();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                finish();
            }
        })).start();
    }



    private void clickDomicilio()
    {
        if(pressButton)
            return;
        else pressButton = true;

        Intent intent = new Intent(HomeDireccionesReg.this, HomeDireccionesRegMap.class);
        intent.putExtra("calle", domicilioReg.getCalle());
        intent.putExtra("latitud", domicilioReg.getmLatLng().latitude);
        intent.putExtra("longitud", domicilioReg.getmLatLng().longitude);
        startActivityForResult(intent, 1);

        SleepButton();
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



    //BACK PRESS
    @SuppressLint("NonConstantResourceId")
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