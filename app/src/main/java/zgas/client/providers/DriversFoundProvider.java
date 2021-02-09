package zgas.client.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import zgas.client.models.DriverFound;


public class DriversFoundProvider {

    DatabaseReference mDatabase;

    public DriversFoundProvider(String id) {
        // LE ENVIAMOS LA SOLITUD DE VIAJE
        mDatabase = FirebaseDatabase.getInstance().getReference().child("DriversFound").child(id);
    }

    public Task<Void> create(DriverFound driverFound) {
        return mDatabase.child(driverFound.getIdDriver()).setValue(driverFound);
    }

    /*
     * SI UN CONDCUTOR ESTA RECIBIENDO LA NOTIFICACION
     */
    public Query getDriverFoundByIdDriver(String idDriver) {
        return mDatabase.orderByChild("idDriver").equalTo(idDriver);
    }

    public Task<Void> delete(String idDriver) {
        return mDatabase.child(idDriver).removeValue();
    }


    public Task<Void> allDelete() {
        return mDatabase.removeValue();
    }
}
