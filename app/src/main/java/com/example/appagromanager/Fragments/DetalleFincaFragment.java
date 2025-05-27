package com.example.appagromanager.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.appagromanager.databinding.FragmentDetalleFincaBinding;
import com.example.appagromanager.viewmodel.BottomViewModel;

public class DetalleFincaFragment extends Fragment {

    private FragmentDetalleFincaBinding binding;
    private BottomViewModel bottomViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDetalleFincaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bottomViewModel = new ViewModelProvider(requireActivity()).get(BottomViewModel.class);

        String fincaId = getArguments() != null ? getArguments().getString("finca_id") : null;
        if (fincaId != null) {
            bottomViewModel.getFincaById(fincaId).observe(getViewLifecycleOwner(), finca -> {
                if (finca != null) {
                    binding.tvNombre.setText("Nombre: " + finca.getNombre());
                    binding.tvFincaDescriocion.setText(finca.getDescripcion());
                    binding.tvUbicacion.setText(finca.getUbicacion());
                    binding.tvCapacidad.setText(String.valueOf(finca.getCapacidad()));
                }
            });
        }

        binding.btnEliminarFinca.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Confirmar eliminación")
                    .setMessage("¿Estás seguro de que quieres eliminar esta finca? Esta acción no se puede deshacer.")
                    .setPositiveButton("Eliminar", (dialog, which) -> {
                        if (fincaId != null) {
                            bottomViewModel.eliminarFinca(fincaId).observe(getViewLifecycleOwner(), success -> {
                                if (success != null && success) {
                                    Toast.makeText(getContext(), "Finca eliminada correctamente", Toast.LENGTH_SHORT).show();
                                    requireActivity().getSupportFragmentManager().popBackStack();
                                } else {
                                    Toast.makeText(getContext(), "Error al eliminar la finca", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }
}
