package com.example.appagromanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appagromanager.R;
import com.example.appagromanager.models.Finca;

import java.util.List;

public class FincaAdapter extends RecyclerView.Adapter<FincaAdapter.FincaViewHolder> {

    public interface OnFincaClickListener {
        void onFincaClick(Finca finca);
    }

    private List<Finca> fincaList;
    private OnFincaClickListener listener;

    public FincaAdapter(List<Finca> fincaList, OnFincaClickListener listener) {
        this.fincaList = fincaList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FincaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_finca, parent, false);
        return new FincaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FincaViewHolder holder, int position) {
        Finca finca = fincaList.get(position);
        holder.bind(finca);
    }

    @Override
    public int getItemCount() {
        return fincaList.size();
    }

    class FincaViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombre, tvUbicacion, tvCapacidad, tvAnimales;

        public FincaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreFinca);
            tvUbicacion = itemView.findViewById(R.id.tvUbicacionFinca);
            tvCapacidad = itemView.findViewById(R.id.tvCapacidadFinca);
            tvAnimales = itemView.findViewById(R.id.tvAnimalesActuales);
        }

        public void bind(final Finca finca) {
            tvNombre.setText(finca.getNombre());
            tvUbicacion.setText(finca.getUbicacion());
//            tvCapacidad.setText("Capacidad: " + finca.getCapacidad());
//            tvAnimales.setText("Animales: " + finca.getAnimalesActuales()); 

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onFincaClick(finca);
                }
            });
        }
    }
}