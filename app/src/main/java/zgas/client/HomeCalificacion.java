package zgas.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import zgas.client.models.ClientBooking;
import zgas.client.models.HistoryBooking;
import zgas.client.providers.AuthProvider;
import zgas.client.providers.ClientBookingProvider;
import zgas.client.providers.HistoryBookingProvider;

public class HomeCalificacion extends AppCompatActivity {


    private TextView mTextViewPrice;
    private RatingBar mRatinBar;
    private Button mButtonCalification;

    private AuthProvider mAuthProvider;
    private ClientBookingProvider mClientBookingProvider;
    private HistoryBooking mHistoryBooking;
    private HistoryBookingProvider mHistoryBookingProvider;

    private float mCalification = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calificacion);

        mAuthProvider = new AuthProvider();
        mClientBookingProvider = new ClientBookingProvider();
        mHistoryBookingProvider = new HistoryBookingProvider();

        mRatinBar = findViewById(R.id.ratingbarCalification);
        mTextViewPrice = findViewById(R.id.textViewPrice);
        mButtonCalification = findViewById(R.id.btnCalification);


        mRatinBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float calification, boolean b) {
                mCalification = calification;
            }
        });

        mButtonCalification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calificate();
            }
        });

        getClientBooking();

    }


    boolean pressButton = false;

    private boolean PushButton()
    {
        return pressButton;
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


    private void getClientBooking() {
        mClientBookingProvider.getClientBooking(mAuthProvider.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ClientBooking clientBooking = dataSnapshot.getValue(ClientBooking.class);
                    mTextViewPrice.setText(clientBooking.getTotal());
                    mHistoryBooking = new HistoryBooking(clientBooking);
                    mHistoryBooking.setIdHistoryBooking(clientBooking.getIdHistoryBooking());

                    if(mHistoryBooking.getStatus().equals("terminate"))
                    {
                        finish();
                        mClientBookingProvider.delete(mAuthProvider.getId());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void calificate() {
        PushButton();
        if (mCalification  > 0) {
            mHistoryBooking.setCalificationDriver(mCalification);
            mHistoryBooking.setTimestamp(new Date().getTime());
            mHistoryBookingProvider.getHistoryBooking(mHistoryBooking.getIdHistoryBooking()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        mHistoryBookingProvider.updateCalificactionDriver(mHistoryBooking.getIdHistoryBooking(), mCalification).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(HomeCalificacion.this, "La calificacion se guardo correctamente", Toast.LENGTH_LONG).show();
                                mClientBookingProvider.updateStatus(mAuthProvider.getId(), "terminate").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        SleepButton();
                                        mClientBookingProvider.delete(mAuthProvider.getId());
                                        finish();
                                    }
                                });

                            }
                        });
                    }
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    SleepButton();
                }
            });


        }
        else {
            Toast.makeText(this, "Debes ingresar la calificacion", Toast.LENGTH_SHORT).show();
            SleepButton();
        }
    }
}