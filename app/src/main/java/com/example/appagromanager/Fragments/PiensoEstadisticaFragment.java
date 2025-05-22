package com.example.appagromanager.Fragments;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appagromanager.BottomViewModel;
import com.example.appagromanager.databinding.FragmentPiensoEstadisticaBinding;
import com.example.appagromanager.models.Pienso;

import java.util.HashMap;
import java.util.Map;

public class PiensoEstadisticaFragment extends Fragment {

    private static final String TAG = "PiensoFragment";
    private FragmentPiensoEstadisticaBinding binding;
    private BottomViewModel bottomViewModel;
    private Map<String, Pienso> piensoMap = new HashMap<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPiensoEstadisticaBinding.inflate(inflater, container, false);
        bottomViewModel = new ViewModelProvider(requireActivity()).get(BottomViewModel.class);

        bottomViewModel.getPiensosLiveData().observe(getViewLifecycleOwner(), piensos -> {
            if (piensos != null) {
                for (Pienso p : piensos) {
                    String key = null;
                    String nombreMinuscula = p.getNombre().toLowerCase();
                    if (nombreMinuscula.contains("vacuno")) {
                        key = "Vacuno";
                    } else if (nombreMinuscula.contains("porcino")) {
                        key = "Porcino";
                    } else if (nombreMinuscula.contains("ovino")) {
                        key = "Ovino";
                    }

                    if (key != null) {
                        piensoMap.put(key, p);
                    } else {
                        Log.w(TAG, "Pienso con nombre inesperado: " + p.getNombre());
                    }
                }
                mostrarCantidades();
            } else {
                Log.w(TAG, "Piensos recibidos son nulos");
            }
        });

        binding.btnVacuno.setOnClickListener(v -> actualizarPienso("Vacuno", binding.etVacuno));
        binding.btnPorcino.setOnClickListener(v -> actualizarPienso("Porcino", binding.etPorcino));
        binding.btnOvino.setOnClickListener(v -> actualizarPienso("Ovino", binding.etOvino));

        return binding.getRoot();
    }

    public void mostrarCantidades() {
        Pienso vacuno = piensoMap.get("Vacuno");
        Pienso porcino = piensoMap.get("Porcino");
        Pienso ovino = piensoMap.get("Ovino");

        mostrarCantidadConAlerta(binding.tvCantidadVacuno, vacuno);
        mostrarCantidadConAlerta(binding.tvCantidadPorcino, porcino);
        mostrarCantidadConAlerta(binding.tvCantidadOvino, ovino);

        Log.d(TAG, "Cantidades mostradas en pantalla: Vacuno=" + (vacuno != null ? vacuno.getCantidadActualKg() : "null")
                + ", Porcino=" + (porcino != null ? porcino.getCantidadActualKg() : "null")
                + ", Ovino=" + (ovino != null ? ovino.getCantidadActualKg() : "null"));
    }

    private void mostrarCantidadConAlerta(TextView textView, Pienso pienso) {
        if (pienso == null) return;

        float actual = pienso.getCantidadActualKg();
        float minimo = (float) pienso.getStockMinimoKg();

        textView.setText(String.format("%.2f KG", actual));

        if (actual < minimo) {

            textView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            if (isVisible()) {
                Toast.makeText(getContext(), "¡Alerta! Bajo stock de " + pienso.getNombre(), Toast.LENGTH_LONG).show();
            }
            Log.w(TAG, "Stock bajo para " + pienso.getNombre() + ": actual=" + actual + " < mínimo=" + minimo);
        } else {
            textView.setTextColor(getResources().getColor(android.R.color.black));
        }
    }


    private void actualizarPienso(String tipo, EditText editText) {
        Pienso pienso = piensoMap.get(tipo);
        if (pienso == null) {
            Toast.makeText(getContext(), "No se encontró el pienso " + tipo, Toast.LENGTH_SHORT).show();
            return;
        }

        String input = editText.getText().toString().trim();
        if (input.isEmpty()) {
            Toast.makeText(getContext(), "Introduce una cantidad", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            float cantidadAAgregar = Float.parseFloat(input);

            if (cantidadAAgregar <= 0) {
                Toast.makeText(getContext(), "La cantidad debe ser mayor a 0", Toast.LENGTH_SHORT).show();
                return;
            }

            float cantidadActualBD = pienso.getCantidadActualKg();
            float nuevaCantidad = cantidadActualBD + cantidadAAgregar;

            bottomViewModel.actualizarCantidadPienso(pienso.getId(), nuevaCantidad);

            bottomViewModel.getCantidadPiensoActualizada().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean success) {
                    if (success != null && success) {
                        pienso.setCantidadActualKg(nuevaCantidad);
                        mostrarCantidades();
                        editText.setText("");
                        Toast.makeText(getContext(), "Cantidad actualizada correctamente", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Error al actualizar el pienso", Toast.LENGTH_SHORT).show();
                    }

                    bottomViewModel.getCantidadPiensoActualizada().removeObserver(this);
                }
            });

        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Cantidad inválida", Toast.LENGTH_SHORT).show();
        }
    }
}