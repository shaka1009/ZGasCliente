package zgas.client.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/*
import com.optic.uberclone.R;
import com.optic.uberclone.activities.HistoryBookingDetailClientActivity;
import com.optic.uberclone.models.HistoryBooking;
import com.optic.uberclone.providers.DriverProvider;
 */


import com.squareup.picasso.Picasso;

import zgas.client.R;
import zgas.client.models.HistoryBooking;
import zgas.client.providers.DriverProvider;


public class HistoryBookingClientAdapter extends FirebaseRecyclerAdapter<HistoryBooking, HistoryBookingClientAdapter.ViewHolder> {

    private DriverProvider mDriverProvider;
    private Context mContext;

    public HistoryBookingClientAdapter(FirebaseRecyclerOptions<HistoryBooking> options, Context context) {
        super(options);
        mDriverProvider = new DriverProvider();
        mContext = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull HistoryBooking historyBooking) {

        final String id = getRef(position).getKey();



        String cantidad ="";
        String salto= "";
        if (historyBooking.getCantidad30kg() != 0)
        {
            cantidad = "Cil 30kg: " + historyBooking.getCantidad30kg();
        }
        if (historyBooking.getCantidad20kg() != 0)
        {
            if(historyBooking.getCantidad30kg()>=1)
                salto = "\n";
            else
                salto = "";
            cantidad = cantidad + salto + "Cil 20kg: " + historyBooking.getCantidad20kg();
        }
        if (historyBooking.getCantidad10kg() != 0)
        {
            if(historyBooking.getCantidad30kg()>=1 || historyBooking.getCantidad20kg()>=1)
                salto = "\n";
            else
                salto = "";

            cantidad = cantidad + salto + "Cil 10kg: " + historyBooking.getCantidad10kg();
        }

        holder.textViewCantidad.setText(cantidad);

        holder.textViewDestination.setText(historyBooking.getCalle());
        holder.textViewCalification.setText(String.valueOf(historyBooking.getCalificationClient()));

        mDriverProvider.getDriver(historyBooking.getIdDriver()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("nombre").getValue().toString();
                    holder.textViewName.setText(name);



                    /*
                    if (dataSnapshot.hasChild("image")) {
                        String image = dataSnapshot.child("image").getValue().toString();
                        Picasso.with(mContext).load(image).into(holder.imageViewHistoryBooking);
                    }
                     */



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        /*

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(mContext, HistoryBookingDetailClientActivity.class);
                //intent.putExtra("idHistoryBooking", id);
                //mContext.startActivity(intent);
            }
        });

        */

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_history_booking, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewName;
        private TextView textViewCantidad;
        private TextView textViewDestination;
        private TextView textViewCalification;
        private ImageView imageViewHistoryBooking;
        private View mView;


        public ViewHolder(View view) {
            super(view);

            mView = view;
            textViewName = view.findViewById(R.id.textViewName);
            textViewCantidad = view.findViewById(R.id.textViewCantidad);
            textViewDestination = view.findViewById(R.id.textViewDestination);
            textViewCalification = view.findViewById(R.id.textViewCalification);
            imageViewHistoryBooking = view.findViewById(R.id.imageViewHistoryBooking);

        }

    }
}
