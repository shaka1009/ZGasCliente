package zgas.client.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.HashMap;
import java.util.Map;

import zgas.client.models.Client;

public class ClientProvider {

    DatabaseReference mDatabase;

    public ClientProvider() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Usuarios").child("Clientes");
    }

    public Task<Void> create(Client client) {
        Map<String, Object> map = new HashMap<>();
        map.put("nombre", client.getNombre());
        map.put("apellido", client.getApellido());
        map.put("telefono", client.getTelefono());
        return mDatabase.child(client.getId()).child("Datos").setValue(map);
    }

    public Task<Void> update(Client client) {
        Map<String, Object> map = new HashMap<>();
        map.put("nombre", client.getNombre());
        map.put("apellido", client.getApellido());
        map.put("telefono", client.getTelefono());
        //map.put("image", client.getImage());
        return mDatabase.child(client.getId()).child("Datos").updateChildren(map);
    }

    public DatabaseReference getClient(String idClient) {
        return mDatabase.child(idClient).child("Datos");
    }

}
