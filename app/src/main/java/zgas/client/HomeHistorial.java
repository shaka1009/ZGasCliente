package zgas.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;


import com.google.firebase.database.Query;

import zgas.client.adapters.HistoryBookingClientAdapter;
import zgas.client.includes.Toolbar;
import zgas.client.models.HistoryBooking;
import zgas.client.providers.AuthProvider;


public class HomeHistorial extends AppCompatActivity {

    private RecyclerView mReciclerView;
    private HistoryBookingClientAdapter mAdapter;
    private AuthProvider mAuthProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_historial);
        Toolbar.show(this, true);

        mAuthProvider = new AuthProvider();

        mReciclerView = findViewById(R.id.recyclerViewHistoryBooking);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mReciclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Query query = FirebaseDatabase.getInstance().getReference()
                .child("HistoryBooking")
                .orderByChild("idCliente")
                .equalTo(mAuthProvider.getId());


        FirebaseRecyclerOptions<HistoryBooking> options = new FirebaseRecyclerOptions.Builder<HistoryBooking>()
                .setQuery(query, HistoryBooking.class)
                .build();


        mAdapter = new HistoryBookingClientAdapter(options, HomeHistorial.this);

        mReciclerView.setAdapter(mAdapter);

        mAdapter.startListening();

    }

    @Override
    protected void onStop() {
        mAdapter.stopListening();
        super.onStop();
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
        finish();
    }
    //BACK PRESS
}