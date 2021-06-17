package zgas.client.providers;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.installations.FirebaseInstallations;

import zgas.client.models.Token;


public class TokenProvider {

    DatabaseReference mDatabase;



    public TokenProvider() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Tokens").child("Clientes");
    }

    public void create(final String idUser) {
        if (idUser == null) return;
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                Token token = new Token(instanceIdResult.getToken());
                mDatabase.child(idUser).setValue(token);
            }
        });
    }

    public DatabaseReference getToken(String idUser) {
        return mDatabase.child(idUser);
    }


    public DatabaseReference getTokenOp(String idUser) {
        return FirebaseDatabase.getInstance().getReference().child("Tokens").child("Operadores").child(idUser);
    }

}
