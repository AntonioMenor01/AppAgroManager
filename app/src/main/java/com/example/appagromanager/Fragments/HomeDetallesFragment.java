package com.example.appagromanager.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.appagromanager.viewmodel.BottomViewModel;
import com.example.appagromanager.databinding.FragmentHomeDetallesBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeDetallesFragment extends Fragment {

    private FragmentHomeDetallesBinding binding;
    private BottomViewModel bottomViewModel;

    private static final String ARG_ANIMAL_NAME = "animal_name";
    public static HomeDetallesFragment newInstance(String animalName) {
        HomeDetallesFragment fragment = new HomeDetallesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ANIMAL_NAME, animalName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeDetallesBinding.inflate(inflater, container, false);

        bottomViewModel = new ViewModelProvider(requireActivity()).get(BottomViewModel.class);

        TextView tvAnimalName = binding.tvAnimalName;
        String animalName = getArguments() != null ? getArguments().getString(ARG_ANIMAL_NAME) : "Vacas";

        bottomViewModel.getCantidadAnimalesPorGrupo(animalName).observe(getViewLifecycleOwner(), cantidad -> {
            if (cantidad != null) {
                binding.textViewCantidad.setText("Cantidad de " + animalName.toLowerCase() + ": " + cantidad);
            } else {
                binding.textViewCantidad.setText("Error al obtener cantidad.");
            }
        });

        bottomViewModel.getAnimalMasViejoPorGrupo(animalName).observe(getViewLifecycleOwner(), animal -> {
            if (animal != null) {
                String fechaString = animal.getFechaNacimiento();
                try {
                    SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date fechaDate = formatoEntrada.parse(fechaString);

                    SimpleDateFormat formatoSalida = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    String fechaFormateada = formatoSalida.format(fechaDate);

                    binding.textViewViejo.setText("Más viejo: " + fechaFormateada + " (" + animal.getCrotal() + ")");
                } catch (Exception e) {
                    binding.textViewViejo.setText("Más viejo: fecha inválida (" + animal.getCrotal() + ")");
                    e.printStackTrace();
                }
            } else {
                binding.textViewViejo.setText("No se encontró animal más viejo.");
            }
        });

        bottomViewModel.getAnimalMasPesadoPorGrupo(animalName).observe(getViewLifecycleOwner(), animal -> {
            if (animal != null) {
                binding.textViewPesado.setText("Más pesado: " + animal.getPeso() + "kg (" + animal.getCrotal() + ")");
            } else {
                binding.textViewPesado.setText("No se encontró animal más pesado.");
            }
        });

        tvAnimalName.setText(animalName);
        return binding.getRoot();
    }
}