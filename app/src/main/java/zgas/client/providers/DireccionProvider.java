package zgas.client.providers;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import zgas.client.Home;
import zgas.client.models.Direcciones;

public class
DireccionProvider {

    DatabaseReference mDatabase;

    public DireccionProvider(String id) {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Usuarios").child("Clientes").child(id).child("Direcciones");
    }

    public Map<String, Object> direccionToMap(Direcciones domicilio)
    {
        Map<String, Object> direccionReg = new HashMap<>();
        direccionReg.put("etiqueta", domicilio.getEtiqueta());
        direccionReg.put("calle", domicilio.getCalle());
        direccionReg.put("colonia", domicilio.getColonia());
        direccionReg.put("codigo_postal", domicilio.getCodigo_postal());
        direccionReg.put("direccion", domicilio.getDomicilioLargo());
        direccionReg.put("numExterior", domicilio.getNumExterior());
        direccionReg.put("numInterior", domicilio.getNumInterior());
        direccionReg.put("SParticular", domicilio.getSParticular());
        direccionReg.put("empresa", domicilio.getEmpresa());
        direccionReg.put("ruta", domicilio.getRuta());
        direccionReg.put("latitud", domicilio.getmLatLng().latitude);
        direccionReg.put("longitud", domicilio.getmLatLng().longitude);
        return direccionReg;
    }

    public void load_domicilios(@NonNull DataSnapshot snapshot)
    {
        Home.mDirecciones = new ArrayList<>();

        if (snapshot.exists())
        {
            try {
                Direcciones.setSlotDefault((short) snapshot.child("default").getValue());
            }
            catch (Exception e)
            {
                Direcciones.setSlotDefault((short) 0);
                Map<String, Object> map = new HashMap<>();
                map.put("default", 0);

                mDatabase.updateChildren(map).addOnCompleteListener(Task::isSuccessful);
            }
            int x;
            for(x=0; x<10; x++)
            {

                try {
                    String etiqueta = Objects.requireNonNull(snapshot.child("slot" + x).child("etiqueta").getValue()).toString();
                    String calle = Objects.requireNonNull(snapshot.child("slot" + x).child("calle").getValue()).toString();
                    String colonia = Objects.requireNonNull(snapshot.child("slot" + x).child("colonia").getValue()).toString();
                    String codigo_postal = Objects.requireNonNull(snapshot.child("slot" + x).child("codigo_postal").getValue()).toString();
                    String direccion = Objects.requireNonNull(snapshot.child("slot" + x).child("direccion").getValue()).toString();
                    String numExterior = Objects.requireNonNull(snapshot.child("slot" + x).child("numExterior").getValue()).toString();
                    String numInterior = Objects.requireNonNull(snapshot.child("slot" + x).child("numInterior").getValue()).toString();
                    String SParticular = Objects.requireNonNull(snapshot.child("slot" + x).child("SParticular").getValue()).toString();
                    int empresa = Integer.parseInt(Objects.requireNonNull(snapshot.child("slot" + x).child("empresa").getValue()).toString());
                    String ruta = Objects.requireNonNull(snapshot.child("slot" + x).child("ruta").getValue()).toString();
                    double latitud = (double) Objects.requireNonNull(snapshot.child("slot" + x).child("latitud").getValue());
                    double longitud = (double) Objects.requireNonNull(snapshot.child("slot" + x).child("longitud").getValue());
                    LatLng mLatLng = new LatLng(latitud, longitud);
                    Home.mDirecciones.add(new Direcciones(etiqueta, calle, colonia, codigo_postal, direccion, numExterior, numInterior, SParticular, empresa, ruta, mLatLng, x));
                }
                catch(Exception ignored){
                }
            }
        }
        Direcciones.setIsLoad(true);
    }


    public Task<Void> update(Direcciones direcciones) {
        Map<String, Object> map = new HashMap<>();
        map.put("etiqueta", direcciones.getEtiqueta());
        map.put("numExterior", direcciones.getNumExterior());
        map.put("numInterior", direcciones.getNumInterior());
        map.put("SParticular", direcciones.getSParticular());
        return mDatabase.child("slot" + direcciones.getSlot()).updateChildren(map);
    }


    public DatabaseReference deleteDireccion(int slot) {
        return mDatabase.child("slot"+slot);
    }

    public DatabaseReference getDomicilios() {
        return mDatabase;
    }

}

