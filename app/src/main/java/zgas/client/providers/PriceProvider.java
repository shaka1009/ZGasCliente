package zgas.client.providers;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import zgas.client.Home;
import zgas.client.models.Client;
import zgas.client.models.Price;

public class PriceProvider {

    DatabaseReference mDatabase;

    public PriceProvider() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Precio");
    }

    public DatabaseReference getPrice() {
        return mDatabase;
    }

}
