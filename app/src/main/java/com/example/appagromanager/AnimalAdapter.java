package com.example.appagromanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appagromanager.models.Animal;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AnimalAdapter extends RecyclerView.Adapter<AnimalAdapter.AnimalViewHolder> {

    private List<Animal> animalesOriginales;
    private List<Animal> animalesFiltrados;
    private final OnAnimalClickListener listener;

    public interface OnAnimalClickListener {
        void onAnimalClick(Animal animal);
    }

    public AnimalAdapter(List<Animal> animales, OnAnimalClickListener listener) {
        this.animalesOriginales = animales != null ? animales : new ArrayList<>();
        this.animalesFiltrados = new ArrayList<>(this.animalesOriginales);
        this.listener = listener;
    }

    public void setAnimales(List<Animal> nuevosAnimales) {
        this.animalesOriginales = nuevosAnimales != null ? nuevosAnimales : new ArrayList<>();
        this.animalesFiltrados = new ArrayList<>(this.animalesOriginales);
        notifyDataSetChanged();
    }

    public void filtrarPorCrotal(String texto) {
        if (texto == null || texto.isEmpty()) {
            animalesFiltrados = new ArrayList<>(animalesOriginales);
        } else {
            animalesFiltrados = animalesOriginales.stream()
                    .filter(animal -> animal.getCrotal().toLowerCase().contains(texto.toLowerCase()))
                    .collect(Collectors.toList());
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AnimalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_animal, parent, false);
        return new AnimalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimalViewHolder holder, int position) {
        Animal animal = animalesFiltrados.get(position);
        holder.bind(animal, listener);
    }

    @Override
    public int getItemCount() {
        return animalesFiltrados.size();
    }

    public static class AnimalViewHolder extends RecyclerView.ViewHolder {
        TextView textCrotal, textNacimiento, textPeso;

        public AnimalViewHolder(@NonNull View itemView) {
            super(itemView);
            textCrotal = itemView.findViewById(R.id.textCrotal);
            textNacimiento = itemView.findViewById(R.id.textNacimiento);
            textPeso = itemView.findViewById(R.id.textPeso);
        }

        public void bind(Animal animal, OnAnimalClickListener listener) {
            String emoji = getEmojiForGrupo(animal.getGrupo());

            textCrotal.setText(emoji + " Crotal: " + animal.getCrotal());
            textNacimiento.setText("📅 Nacimiento: " + animal.getFechaNacimiento());
            textPeso.setText("⚖️ Peso: " + animal.getPeso() + " kg");

            itemView.setOnClickListener(v -> listener.onAnimalClick(animal));
        }

        private String getEmojiForGrupo(String grupo) {
            switch (grupo.toUpperCase()) {
                case "VACUNO":
                    return "🐄";
                case "OVINO":
                    return "🐑";
                case "PORCINO":
                    return "🐖";
                default:
                    return "🐾";
            }
        }
    }
}
