package zgas.client.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import zgas.client.R;
import zgas.client.models.Sucursales;


public class SucursalesListAdapter extends RecyclerView.Adapter<SucursalesListAdapter.ViewHolder> {

    private List<Sucursales> mData;
    private LayoutInflater mInflater;
    private Context context;

    public SucursalesListAdapter(List<Sucursales> itemList, Context context)
    {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = itemList;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = mInflater.inflate(R.layout.rv_sucursales, null);
        return new ViewHolder(view);
    }

    @Override public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        holder.bindData(mData.get(position));
    }

    public void setItems(List<Sucursales> items)
    {
        mData = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView surcursal, direccion, telefono, horario;

        ViewHolder(View itemView)
        {
            super(itemView);
            surcursal = itemView.findViewById(R.id.tvSucursal);
            direccion = itemView.findViewById(R.id.tvDireccion);
            telefono = itemView.findViewById(R.id.tvTelefono);
            horario = itemView.findViewById(R.id.tvHorario);

        }
        @SuppressLint("SetTextI18n")
        void bindData(final Sucursales item)
        {
            surcursal.setText(item.getSucursal());
            direccion.setText("Dirección: " + item.getDireccion());
            telefono.setText("Teléfono: " + item.getTelefono());
            horario.setText("Horario: " + item.getHorario());
        }
    }

}
