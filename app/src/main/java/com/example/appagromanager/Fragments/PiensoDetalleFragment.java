package com.example.appagromanager.Fragments;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.appagromanager.BottomViewModel;
import com.example.appagromanager.R;
import com.example.appagromanager.databinding.FragmentPiensoDetalleBinding;
import com.example.appagromanager.models.Animal;
import com.example.appagromanager.models.Pienso;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PiensoDetalleFragment extends Fragment {

    private FragmentPiensoDetalleBinding binding;
    private BottomViewModel bottomViewModel;

    private double consumoPorAnimal = 0.0;
    private int cantidadAnimales = 0;
    private List<Pienso> listaPiensos = new ArrayList<>();
    private String grupoSeleccionadoActual = "";
    private String grupoSeleccionado = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPiensoDetalleBinding.inflate(inflater, container, false);
        bottomViewModel = new ViewModelProvider(requireActivity()).get(BottomViewModel.class);

        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.grupos_array,
                android.R.layout.simple_spinner_item
        );
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerGrupo.setAdapter(adapterSpinner);

        binding.spinnerGrupo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                grupoSeleccionadoActual = (String) parent.getItemAtPosition(position);
                grupoSeleccionado = grupoSeleccionadoActual;

                Log.d("PiensoDetalle", "Grupo seleccionado: " + grupoSeleccionado);

                bottomViewModel.cargarConfiguracionConsumoPorGrupo(grupoSeleccionado);
                bottomViewModel.cargarPiensos();
                bottomViewModel.obtenerAnimalesPorGrupo(grupoSeleccionado);

                bottomViewModel.getCantidadAnimalesPorGrupo(grupoSeleccionado).observe(getViewLifecycleOwner(), cantidad -> {
                    cantidadAnimales = (cantidad != null) ? cantidad : 0;
                    Log.d("PiensoDetalle", "Cantidad animales para grupo " + grupoSeleccionado + ": " + cantidadAnimales);
                    String grupoTexto = (grupoSeleccionado != null) ? grupoSeleccionado.toLowerCase() : "grupo";
                    binding.textViewCantidad.setText("Cantidad de " + grupoTexto + ": " + cantidadAnimales);
                    actualizarConsumoTotal();
                });
                verificarEstadoBotonAlimentar(grupoSeleccionado);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        bottomViewModel.getConfiguracionConsumo().observe(getViewLifecycleOwner(), config -> {
            if (config != null) {
                consumoPorAnimal = config.getConsumoKgPorAnimalPorDia();
                Log.d("PiensoDetalle", "Consumo por animal cargado: " + consumoPorAnimal);
                binding.textViewConsumoPorAnimal.setText("Consumo por animal: " + consumoPorAnimal + " kg");
            } else {
                consumoPorAnimal = 0;
                Log.d("PiensoDetalle", "No hay configuración de consumo disponible");
                binding.textViewConsumoPorAnimal.setText("Consumo por animal: --");
            }
            actualizarConsumoTotal();
        });

        bottomViewModel.getPiensosLiveData().observe(getViewLifecycleOwner(), piensos -> {
            if (piensos != null && !piensos.isEmpty()) {
                listaPiensos = piensos;
                Pienso piensoSeleccionado = piensos.get(0);
                binding.textViewPienso.setText(piensoSeleccionado.getNombre());
                sugerirPiensoMasFrecuente();
            }
        });

        bottomViewModel.getAnimales().observe(getViewLifecycleOwner(), animales -> {
            sugerirPiensoMasFrecuente();
        });

        binding.buttonAlimentar.setOnClickListener(v -> {
            String grupo = grupoSeleccionado;
            if (yaSeAlimentoHoy(grupo)) {
                Toast.makeText(requireContext(), "Este grupo ya fue alimentado hoy.", Toast.LENGTH_SHORT).show();
                return;
            }

            double cantidadTotal = consumoPorAnimal * cantidadAnimales;

            if (cantidadTotal <= 0) {
                Toast.makeText(requireContext(), "No se puede registrar consumo. Verifique los datos.", Toast.LENGTH_SHORT).show();
                return;
            }

            String piensoSeleccionado = (String) binding.textViewPienso.getText().toString();
            if (piensoSeleccionado == null || piensoSeleccionado.isEmpty()) {
                Toast.makeText(requireContext(), "Seleccione un pienso válido", Toast.LENGTH_SHORT).show();
                return;
            }

            Pienso piensoSeleccionadoObj = null;
            for (Pienso p : listaPiensos) {
                if (p.getNombre().equals(piensoSeleccionado)) {
                    piensoSeleccionadoObj = p;
                    break;
                }
            }

            if (piensoSeleccionadoObj == null) {
                Toast.makeText(requireContext(), "Pienso no válido", Toast.LENGTH_SHORT).show();
                return;
            }

            bottomViewModel.registrarConsumo(grupo, cantidadTotal, piensoSeleccionadoObj.getId());

            Pienso finalPiensoSeleccionadoObj = piensoSeleccionadoObj;
            bottomViewModel.getConsumoRegistrado().observe(getViewLifecycleOwner(), success -> {
                if (success != null && success) {
                    double nuevaCantidad = finalPiensoSeleccionadoObj.getCantidadActualKg() - cantidadTotal;
                    bottomViewModel.actualizarCantidadPienso(finalPiensoSeleccionadoObj.getId(), nuevaCantidad);
                    guardarFechaAlimentacion(grupo);
                    Toast.makeText(requireContext(), "Consumo registrado correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Error al registrar consumo", Toast.LENGTH_SHORT).show();
                }
            });
        });

        return binding.getRoot();
    }

    private void actualizarConsumoTotal() {
        if (cantidadAnimales > 0 && consumoPorAnimal > 0) {
            double total = cantidadAnimales * consumoPorAnimal;
            Log.d("PiensoDetalle", "Actualizando consumo total: " + total);
            binding.textViewConsumoTotal.setText("Consumo total del grupo: " + total + " kg");
        } else {
            Log.d("PiensoDetalle", "Consumo total no actualizado, cantidadAnimales=" + cantidadAnimales + ", consumoPorAnimal=" + consumoPorAnimal);
            binding.textViewConsumoTotal.setText("Consumo total del grupo: --");
        }
    }

    private void sugerirPiensoMasFrecuente() {
        List<Animal> animales = bottomViewModel.getAnimales().getValue();
        if (animales == null || animales.isEmpty()) return;

        Map<String, Integer> conteoPiensos = new HashMap<>();
        for (Animal animal : animales) {
            String idPienso = animal.getPiensoId();
            if (idPienso != null && !idPienso.isEmpty()) {
                conteoPiensos.put(idPienso, conteoPiensos.getOrDefault(idPienso, 0) + 1);
            }
        }

        String idPiensoMasFrecuente = null;
        int maxFrecuencia = 0;
        for (Map.Entry<String, Integer> entry : conteoPiensos.entrySet()) {
            if (entry.getValue() > maxFrecuencia) {
                maxFrecuencia = entry.getValue();
                idPiensoMasFrecuente = entry.getKey();
            }
        }

        if (idPiensoMasFrecuente != null) {
            for (Pienso p : listaPiensos) {
                if (p.getId().equals(idPiensoMasFrecuente)) {
                    binding.textViewPienso.setText(p.getNombre());
                    break;
                }
            }
        }
    }

    private String getFechaHoy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return LocalDate.now().toString();
        } else {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
            return sdf.format(java.util.Calendar.getInstance().getTime());
        }
    }


    private boolean yaSeAlimentoHoy(String grupo) {
        String fechaHoy = getFechaHoy();
        String key = "ultima_alimentacion_" + grupo;
        return requireContext()
                .getSharedPreferences("alimentacion_prefs", 0)
                .getString(key, "")
                .equals(fechaHoy);
    }

    private void guardarFechaAlimentacion(String grupo) {
        String fechaHoy = getFechaHoy();
        String key = "ultima_alimentacion_" + grupo;
        requireContext()
                .getSharedPreferences("alimentacion_prefs", 0)
                .edit()
                .putString(key, fechaHoy)
                .apply();

        verificarEstadoBotonAlimentar(grupo);
    }

    private void verificarEstadoBotonAlimentar(String grupo) {
        boolean yaAlimentado = yaSeAlimentoHoy(grupo);
        binding.buttonAlimentar.setEnabled(!yaAlimentado);
        binding.buttonAlimentar.setAlpha(yaAlimentado ? 0.5f : 1.0f);
    }

}