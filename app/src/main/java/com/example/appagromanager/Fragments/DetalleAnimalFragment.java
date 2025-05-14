package com.example.appagromanager.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.appagromanager.BottomViewModel;
import com.example.appagromanager.R;
import com.example.appagromanager.databinding.FragmentDetalleAnimalBinding;
import com.example.appagromanager.models.Animal;
import com.example.appagromanager.models.Finca;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DetalleAnimalFragment extends Fragment {

    private FragmentDetalleAnimalBinding binding;
    private BottomViewModel bottomViewModel;
    private static final String TAG = "DetalleAnimalFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDetalleAnimalBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Animal animal = getArguments().getParcelable("animal");

        if (animal != null) {
            Log.d(TAG, "Animal recibido: " + animal.getCrotal() + ", ID finca: " + animal.getFincaId());
            binding.textCrotal.setText(animal.getCrotal());
            binding.textNacimiento.setText(formatearFecha(animal.getFechaNacimiento()));
            binding.textPeso.setText(animal.getPeso() + " kg");
            binding.textGrupo.setText(animal.getGrupo());
            binding.textFechaInsercion.setText(formatearFecha(animal.getFechaInsercion()));
            binding.imagenAnimal.setImageResource(getImagenPorGrupo(animal.getGrupo()));

        }

        bottomViewModel = new ViewModelProvider(requireActivity()).get(BottomViewModel.class);
        bottomViewModel.obtenerFincas();
        bottomViewModel.getFincas().observe(getViewLifecycleOwner(), fincas -> {
            if (fincas != null && animal != null) {
                for (Finca finca : fincas) {
                    if (finca.getId().equals(animal.getFincaId())) {
                        binding.textFinca.setText(finca.getNombre());
                        Log.d(TAG, "Finca encontrada: " + finca.getNombre());
                        break;
                    }
                }
            } else {
                Log.w(TAG, "Fincas es null o animal es null");
            }
        });

        binding.btnEliminar.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Confirmar eliminación")
                    .setMessage("¿Estás seguro de que quieres eliminar este animal?")
                    .setPositiveButton("Eliminar", (dialog, which) -> {
                        String crotal = binding.textCrotal.getText().toString();
                        bottomViewModel.eliminarAnimal(crotal);
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
        bottomViewModel.getEliminado().observe(getViewLifecycleOwner(), eliminado -> {
            if (eliminado != null) {
                if (eliminado) {
                    Toast.makeText(requireContext(), "Animal eliminado correctamente", Toast.LENGTH_SHORT).show();
                    requireActivity().onBackPressed(); // O usar Navigation
                } else {
                    Toast.makeText(requireContext(), "Error al eliminar el animal", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private int getImagenPorGrupo(String grupo) {
        if (grupo == null) return R.drawable.imagen;

        switch (grupo.toUpperCase()) {
            case "VACUNO":
                return R.drawable.vaca;
            case "OVINO":
                return R.drawable.oveja;
            case "PORCINO":
                return R.drawable.cerdo;
            default:
                return R.drawable.imagen;
        }
    }

    private String formatearFecha(String fechaOriginal) {
        try {
            SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            SimpleDateFormat formatoSalida = new SimpleDateFormat("dd-MM-yyyy", new Locale("es", "ES"));
            return formatoSalida.format(formatoEntrada.parse(fechaOriginal));
        } catch (Exception e) {
            e.printStackTrace();
            return fechaOriginal;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
