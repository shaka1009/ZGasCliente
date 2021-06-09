package zgas.client;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.text.Collator;
import java.util.Locale;

import zgas.client.includes.Popup;
import zgas.client.includes.Toolbar;
import zgas.client.models.Direcciones;
import zgas.client.providers.AuthProvider;
import zgas.client.providers.DireccionProvider;
import zgas.client.tools.Validacion;

public class HomeDireccionesDetails extends AppCompatActivity {

    private CoordinatorLayout snackbar;
    private DireccionProvider mDireccionProvider;
    private Popup mPopup;
    private TextView tvEtiqueta, tvDomicilio, tvNumExt, tvNumInt, tvSParticular;
    private LinearLayout llEtiqueta;
    private LinearLayout llNumExt;
    private LinearLayout llNumInt;
    private LinearLayout llSParticular;
    private EditText etEtiqueta, etNumExt, etNumInt, etSParticular;
    private ImageView ivEmpresa;
    private int posicion;
    boolean pressButton = false;
    Validacion mVal;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_domicilios_details);
        Toolbar.show(this, true);
        mPopup = new Popup(this, getApplicationContext(), findViewById(R.id.popupError));
        AuthProvider mAuthProvider = new AuthProvider();
        mDireccionProvider = new DireccionProvider(mAuthProvider.getId());
        mVal = new Validacion();

        posicion = getIntent().getIntExtra("posicion", 100);

        declaration();
        listenner();

        if(posicion==100)
        {
            finish();
        }
        enable_edit(false);
    }

    private void declaration() {
        etEtiqueta = findViewById(R.id.etEtiqueta);
        etNumExt = findViewById(R.id.etNumExt);
        etNumInt = findViewById(R.id.etNumInt);
        etSParticular = findViewById(R.id.etSParticular);

        tvEtiqueta = findViewById(R.id.tvEtiqueta);
        tvNumExt = findViewById(R.id.tvNumExt);
        tvNumInt = findViewById(R.id.tvNumInt);
        tvSParticular = findViewById(R.id.tvSParticular);
        tvDomicilio = findViewById(R.id.tvDomicilio);

        llEtiqueta  = findViewById(R.id.llEtiqueta);
        llNumExt = findViewById(R.id.llNumExt);
        llNumInt = findViewById(R.id.llNumInt);
        llSParticular = findViewById(R.id.llSParticular);

        ivEmpresa = findViewById(R.id.ivEmpresa);

        etEtiqueta.setText(Home.mDirecciones.get(posicion).getEtiqueta());
        etNumExt.setText(Home.mDirecciones.get(posicion).getNumExterior());
        etNumInt.setText(Home.mDirecciones.get(posicion).getNumInterior());
        etSParticular.setText(Home.mDirecciones.get(posicion).getSParticular());

        tvEtiqueta.setText(Home.mDirecciones.get(posicion).getEtiqueta());
        tvDomicilio.setText(Home.mDirecciones.get(posicion).getDomicilioLargo());
        tvNumExt.setText(Home.mDirecciones.get(posicion).getNumExterior());
        tvNumInt.setText(Home.mDirecciones.get(posicion).getNumInterior());
        tvSParticular.setText(Home.mDirecciones.get(posicion).getSParticular());

        snackbar = findViewById(R.id.snackbar_layout);
        ivEmpresa.setImageResource(Home.mDirecciones.get(posicion).getmFlagImage());
    }

    private void enable_edit(boolean b)
    {
        if(b)
        {
            etEtiqueta.setEnabled(true);
            etNumExt.setEnabled(true);
            etNumInt.setEnabled(true);
            etSParticular.setEnabled(true);

            etEtiqueta.setVisibility(View.VISIBLE);
            etNumExt.setVisibility(View.VISIBLE);
            etNumInt.setVisibility(View.VISIBLE);
            etSParticular.setVisibility(View.VISIBLE);

            tvEtiqueta.setVisibility(View.GONE);
            tvNumExt.setVisibility(View.GONE);
            tvNumInt.setVisibility(View.GONE);
            tvSParticular.setVisibility(View.GONE);

            etEtiqueta.setText(Home.mDirecciones.get(posicion).getEtiqueta());
            tvDomicilio.setText(Home.mDirecciones.get(posicion).getDomicilioLargo());
            etNumExt.setText(Home.mDirecciones.get(posicion).getNumExterior());
            etNumInt.setText(Home.mDirecciones.get(posicion).getNumInterior());
            etSParticular.setText(Home.mDirecciones.get(posicion).getSParticular());
        }
        else
        {
            etEtiqueta.setEnabled(false);
            etNumExt.setEnabled(false);
            etNumInt.setEnabled(false);
            etSParticular.setEnabled(false);

            tvEtiqueta.setVisibility(View.VISIBLE);
            tvNumExt.setVisibility(View.VISIBLE);
            tvNumInt.setVisibility(View.VISIBLE);
            tvSParticular.setVisibility(View.VISIBLE);

            tvEtiqueta.setText(Home.mDirecciones.get(posicion).getEtiqueta());
            tvDomicilio.setText(Home.mDirecciones.get(posicion).getDomicilioLargo());
            tvNumExt.setText(Home.mDirecciones.get(posicion).getNumExterior());
            tvNumInt.setText(Home.mDirecciones.get(posicion).getNumInterior());
            tvSParticular.setText(Home.mDirecciones.get(posicion).getSParticular());

            etEtiqueta.setVisibility(View.GONE);
            etNumExt.setVisibility(View.GONE);
            etNumInt.setVisibility(View.GONE);
            etSParticular.setVisibility(View.GONE);
        }

    }

    private void listenner() {
        mPopup.popupEliminarDireccionEliminar.setOnClickListener(v -> {
            if(pressButton)
                return;
            else pressButton = true;
            eliminar_direccion();
            mPopup.hidePopupDireccion();
        });

        llEtiqueta.setOnClickListener(v -> {
            if(pressButton)
                return;
            else pressButton = true;
            etEtiqueta.requestFocus();
            SleepButton();
        });

        llNumExt.setOnClickListener(v -> {
            if(pressButton)
                return;
            else pressButton = true;
            etNumExt.requestFocus();
            SleepButton();
        });

        llNumInt.setOnClickListener(v -> {
            if(pressButton)
                return;
            else pressButton = true;
            etNumInt.requestFocus();
            SleepButton();
        });

        llSParticular.setOnClickListener(v -> {
            if(pressButton)
                return;
            else pressButton = true;
            etSParticular.requestFocus();
            SleepButton();
        });
    }

    private void eliminar_direccion() {
        mDireccionProvider.deleteDireccion(Home.mDirecciones.get(posicion).getSlot()).removeValue().addOnCompleteListener(task -> new Thread(() -> mDireccionProvider.getDomicilios().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                load_data();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                load_data();
            }
        })).start());
    }

    private void load_data()
    {
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_toolbar_domicilios, this.menu); //MOSTRAR
        //getMenuInflater().inflate(R.menu.menu_toolbar_check_ok, menu); //MOSTRAR
        //menu.clear(); //OCULTAR
        return true;
    }



    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case android.R.id.home:
                backPress();
                break;
            case R.id.btnEdit:
                if(pressButton)
                    return true;
                else pressButton = true;

                enable_edit(true);
                menu.clear(); //OCULTAR
                getMenuInflater().inflate(R.menu.menu_toolbar_check_cancel, menu); //MOSTRAR
                getMenuInflater().inflate(R.menu.menu_toolbar_check_ok, menu); //MOSTRAR
                SleepButton();
                break;
            case R.id.btnDelete:
                if(pressButton)
                    return true;
                else pressButton = true;
                mPopup.setEliminarDireccion(Home.mDirecciones.get(posicion).getEtiqueta());
                SleepButton();
                break;

            case R.id.btnCancel:

                if(pressButton)
                    return true;
                else pressButton = true;

                menu.clear(); //OCULTAR
                getMenuInflater().inflate(R.menu.menu_toolbar_domicilios, menu); //MOSTRAR
                UIUtil.hideKeyboard(HomeDireccionesDetails.this); //ESCONDER TECLADO
                enable_edit(false);

                SleepButton();
                break;


            case R.id.btnUpdate:
                if(pressButton)
                    return true;
                else pressButton = true;

                UIUtil.hideKeyboard(HomeDireccionesDetails.this); //ESCONDER TECLADO

                String etiqueta = etEtiqueta.getText().toString().trim();
                String NumExt = etNumExt.getText().toString().trim();
                String NumInt = etNumInt.getText().toString().trim();
                String SParticular = etSParticular.getText().toString().trim();

                if(isValData(etiqueta, NumExt))
                {
                    menu.clear(); //OCULTAR
                    getMenuInflater().inflate(R.menu.menu_toolbar_domicilios, menu); //MOSTRAR

                    if(!etiqueta.equals(Home.mDirecciones.get(posicion).getEtiqueta()) ||
                            !NumExt.equals(Home.mDirecciones.get(posicion).getNumExterior()) ||
                            !NumInt.equals(Home.mDirecciones.get(posicion).getNumInterior()) ||
                            !SParticular.equals(Home.mDirecciones.get(posicion).getSParticular()))
                    {
                        update(etiqueta, NumExt, NumInt, SParticular);
                    }

                    enable_edit(false);
                }

                SleepButton();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isValData(String etiqueta, String NumExt) {
        if(etiqueta.equals("") || mVal.isOnlySpace(etiqueta))
        {
            runOnUiThread(() -> {

                Snackbar.make(snackbar, "La etiqueta está vacía, por favor escribe una etiqueta.", Snackbar.LENGTH_LONG) //ERROR 001
                        .setActionTextColor(getResources().getColor(R.color.white)).show();
                //mPopup.setPopupError("La etiqueta está vacía, por favor escribe una etiqueta.");
                etEtiqueta.requestFocus();
            });
            return false;
        }

        ///validar etiqueta
        for(int x=0; x<10; x++)
        {
            try {
                Locale locale = new Locale("es", "ES");
                Collator collator = Collator.getInstance(locale);
                collator.setStrength(Collator.PRIMARY);



                if(collator.compare(Home.mDirecciones.get(x).getEtiqueta(), etiqueta) == 0 && !Home.mDirecciones.get(x).getEtiqueta().equals(Home.mDirecciones.get(posicion).getEtiqueta()))
                {
                    //Toast.makeText(this, "Slot: " +Home.mDirecciones.get(x).getSlot() + "X: " + x , Toast.LENGTH_SHORT).show();

                    Snackbar.make(snackbar, "Ya tienes registrado un domicilio con esta etiqueta.", Snackbar.LENGTH_LONG)
                            .setActionTextColor(getResources().getColor(R.color.white)).show();
                    etEtiqueta.requestFocus();
                    return false;
                }
            }
            catch(Exception ignored){}
        }

        if(NumExt.equals("") ||mVal.isOnlySpace(NumExt))
        {
            runOnUiThread(() -> {

                Snackbar.make(snackbar, "El número exterior es necesario, por favor escribe el número exterior.", Snackbar.LENGTH_LONG)
                        .setActionTextColor(getResources().getColor(R.color.white)).show();
                //mPopup.setPopupError("La etiqueta está vacía, por favor escribe una etiqueta.");
                etEtiqueta.requestFocus();
            });
            return false;
        }

        return true;
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

    private void update(String etiqueta, String NumExt, String NumInt, String SParticular) {
        Direcciones domicilioEdit;

        domicilioEdit = Home.mDirecciones.get(posicion);

        domicilioEdit.setEtiqueta(etiqueta);
        domicilioEdit.setNumExterior(NumExt);
        domicilioEdit.setNumInterior(NumInt);
        domicilioEdit.setSParticular(SParticular);


        mDireccionProvider.update(domicilioEdit).addOnCompleteListener(taskCreate -> {
            if (taskCreate.isSuccessful()) {
                //Toast.makeText(this, "Datos actualizados correctamente.", Toast.LENGTH_SHORT).show();

                Home.mDirecciones.get(posicion).setEtiqueta(etiqueta);
                Home.mDirecciones.get(posicion).setNumExterior(NumExt);
                Home.mDirecciones.get(posicion).setNumInterior(NumInt);
                Home.mDirecciones.get(posicion).setSParticular(SParticular);


            }
            else
                Toast.makeText(this, "Error en actualizar datos.", Toast.LENGTH_SHORT).show();
        });


    }

    @Override
    public void onBackPressed() {
        backPress();
    }

    //DEP
    private void backPress() {
        finish();
    }

}