package zgas.client.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import zgas.client.R;
import zgas.client.models.Direcciones;

public class DireccionesListAdapter extends RecyclerView.Adapter<DireccionesListAdapter.ViewHolder> {

    private List<Direcciones> mData;
    private LayoutInflater mInflater;
    private Context context;

    public DireccionesListAdapter(List<Direcciones> itemList, Context context)
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
        View view = mInflater.inflate(R.layout.rv_domicilios, null);
        return new ViewHolder(view);
    }

    @Override public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        holder.bindData(mData.get(position));
    }

    public void setItems(List<Direcciones> items)
    {
        mData = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView etiqueta, calle;
        ImageView logo_empresa;

        ViewHolder(View itemView)
        {
            super(itemView);
            etiqueta = itemView.findViewById(R.id.rvEtiqueta);
            calle = itemView.findViewById(R.id.rvDomicilio);
            logo_empresa = itemView.findViewById(R.id.logo_empresa);

        }
        void bindData(final Direcciones item)
        {
            etiqueta.setText(item.getEtiqueta());
            calle.setText(item.getCalle());
            logo_empresa.setImageResource(item.getmFlagImage());
        }
    }

}
