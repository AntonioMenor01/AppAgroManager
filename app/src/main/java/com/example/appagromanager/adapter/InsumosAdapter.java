package com.example.appagromanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.appagromanager.R;
import com.example.appagromanager.models.Insumo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class InsumosAdapter extends RecyclerView.Adapter<InsumosAdapter.InsumoViewHolder> {
    private List<Insumo> originalList;
    private List<Insumo> filteredList;
    private Consumer<Insumo> onRegistrarUsoClicked;

    public InsumosAdapter(List<Insumo> list, Consumer<Insumo> listener) {
        this.originalList = list;
        this.filteredList = new ArrayList<>(list);
        this.onRegistrarUsoClicked = listener;
    }
    public void actualizarLista(List<Insumo> nuevaLista) {
        this.originalList.clear();
        this.originalList.addAll(nuevaLista);
        this.filteredList = new ArrayList<>(originalList);
        notifyDataSetChanged();
    }


    @Override
    public InsumoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_insumo, parent, false);
        return new InsumoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(InsumoViewHolder holder, int position) {
        Insumo insumo = filteredList.get(position);
        holder.bind(insumo);
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void filtrar(String texto) {
        filteredList = originalList.stream()
                .filter(i -> i.getNombre().toLowerCase().contains(texto.toLowerCase()) ||
                        i.getTipo().toLowerCase().contains(texto.toLowerCase()))
                .collect(Collectors.toList());
        notifyDataSetChanged();
    }

    class InsumoViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, tipo, cantidad, unidad;
        Button registrarUsoBtn;

        InsumoViewHolder(View view) {
            super(view);
            nombre = view.findViewById(R.id.nombreTextView);
            tipo = view.findViewById(R.id.tipoTextView);
            cantidad = view.findViewById(R.id.cantidadTextView);
            unidad = view.findViewById(R.id.unidadTextView);
            registrarUsoBtn = view.findViewById(R.id.registrarUsoBtn);
        }

        void bind(Insumo insumo) {
            nombre.setText(insumo.getNombre());
            tipo.setText(insumo.getTipo());
            cantidad.setText("Cantidad: " + insumo.getCantidad());
            unidad.setText(insumo.getUnidad());

            registrarUsoBtn.setOnClickListener(v -> onRegistrarUsoClicked.accept(insumo));
        }
    }
}
