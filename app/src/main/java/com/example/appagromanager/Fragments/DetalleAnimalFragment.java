package com.example.appagromanager.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.appagromanager.BottomViewModel;
import com.example.appagromanager.R;
import com.example.appagromanager.models.Animal;
import com.example.appagromanager.models.Finca;

public class DetalleAnimalFragment extends Fragment {

    private TextView textCrotal, textNacimiento, textPeso, textGrupo, textFechaInsercion, textFinca;
    private BottomViewModel bottomViewModel;
    private static final String TAG = "DetalleAnimalFragment";

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detalle_animal, container, false);

        textCrotal = rootView.findViewById(R.id.textCrotal);
        textNacimiento = rootView.findViewById(R.id.textNacimiento);
        textPeso = rootView.findViewById(R.id.textPeso);
        textGrupo = rootView.findViewById(R.id.textGrupo);
        textFechaInsercion = rootView.findViewById(R.id.textFechaInsercion);
        textFinca = rootView.findViewById(R.id.textFinca);

        Animal animal = getArguments().getParcelable("animal");

        if (animal != null) {
            Log.d(TAG, "Animal recibido: " + animal.getCrotal() + ", ID finca: " + animal.getFincaId());
            textCrotal.setText("Crotal: " + animal.getCrotal());
            textNacimiento.setText("Nacimiento: " + animal.getFechaNacimiento());
            textPeso.setText("Peso: " + animal.getPeso() + " kg");
            textGrupo.setText("Grupo: " + animal.getGrupo());
            textFechaInsercion.setText("Fecha inserción: " + animal.getFechaInsercion());
        }

        bottomViewModel = new ViewModelProvider(requireActivity()).get(BottomViewModel.class);
        bottomViewModel.obtenerFincas();
        bottomViewModel.getFincas().observe(getViewLifecycleOwner(), fincas -> {
            if (fincas != null) {
                for (Finca finca : fincas) {
                    if (finca.getId().equals(animal.getFincaId())) {
                        textFinca.setText("Finca: " + finca.getNombre());
                        break;
                    }
                }
            } else {
                Log.w(TAG, "Fincas es null");
            }
        });

        return rootView;
    }
}