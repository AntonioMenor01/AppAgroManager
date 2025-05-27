package com.example.appagromanager.Fragments;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.appagromanager.databinding.FragmentCrearFincaBinding;
import com.example.appagromanager.models.Finca;
import com.example.appagromanager.viewmodel.BottomViewModel;
import com.google.gson.Gson;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class CrearFincaFragment extends Fragment {

    private FragmentCrearFincaBinding binding;
    private BottomViewModel bottomViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCrearFincaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bottomViewModel = new ViewModelProvider(this).get(BottomViewModel.class);

        binding.btnGuardarFinca.setOnClickListener(v -> guardarFinca());
    }

    private void guardarFinca() {
        String nombre = binding.etNombre.getText().toString().trim();
        String ubicacion = binding.etUbicacion.getText().toString().trim();
        String capacidadStr = binding.etCapacidad.getText().toString().trim();
        String descripcion = binding.etDescripcion.getText().toString().trim();

        if (nombre.isEmpty()) {
            binding.etNombre.setError("El nombre es obligatorio");
            return;
        }

        if (ubicacion.isEmpty()) {
            binding.etUbicacion.setError("La ubicación es obligatoria");
            return;
        }

        if (capacidadStr.isEmpty()) {
            binding.etCapacidad.setError("La capacidad es obligatoria");
            return;
        }

        int capacidad;
        try {
            capacidad = Integer.parseInt(capacidadStr);
            if (capacidad <= 0) {
                binding.etCapacidad.setError("La capacidad debe ser mayor que 0");
                return;
            }
        } catch (NumberFormatException e) {
            binding.etCapacidad.setError("Introduce un número válido");
            return;
        }

        if (descripcion.isEmpty()) {
            binding.etDescripcion.setError("La descripción es obligatoria");
            return;
        }

        String fechaActual = String.valueOf(getFechaActualISO8601());
        Finca nuevaFinca = new Finca(nombre, ubicacion, descripcion, capacidad, fechaActual);

        Log.d("DEBUG_JSON_Finca", new Gson().toJson(nuevaFinca));
        Log.d("CrearFincaFragment", "Finca creada: " + nuevaFinca.toString());

        bottomViewModel.insertarFinca(nuevaFinca).observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(getContext(), "Finca guardada", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).popBackStack();
            } else {
                Toast.makeText(getContext(), "Error al guardar finca", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private OffsetDateTime getFechaActualISO8601() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return OffsetDateTime.now(ZoneOffset.UTC);
        } else {
            return null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}