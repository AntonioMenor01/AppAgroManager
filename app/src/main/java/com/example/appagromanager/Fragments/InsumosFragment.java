package com.example.appagromanager.Fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appagromanager.R;
import com.example.appagromanager.adapter.InsumosAdapter;
import com.example.appagromanager.databinding.FragmentInsumosBinding;
import com.example.appagromanager.models.Animal;
import com.example.appagromanager.models.Insumo;
import com.example.appagromanager.viewmodel.BottomViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class InsumosFragment extends Fragment {

    private FragmentInsumosBinding binding;
    private InsumosAdapter insumosAdapter;
    private List<Insumo> insumoList = new ArrayList<>();
    private BottomViewModel bottomViewModel;

    private List<Animal> animalesLista = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentInsumosBinding.inflate(inflater, container, false);
        bottomViewModel = new ViewModelProvider(requireActivity()).get(BottomViewModel.class);

        setupRecyclerView();
        setupSearch();
        setupFab();

        bottomViewModel.getInsumos().observe(getViewLifecycleOwner(), lista -> {
            if (lista != null) {
                Log.d("InsumosFragment", "Insumos recibidos: " + lista.size());
                insumosAdapter.actualizarLista(lista);
            } else {
                Log.d("InsumosFragment", "Lista de insumos es null");
            }
        });

        bottomViewModel.getAnimales().observe(getViewLifecycleOwner(), animales -> {
            if (animales != null) {
                animalesLista = animales;
                Log.d("InsumosFragment", "Animales recibidos en fragment: " + animalesLista.size());
            } else {
                Log.d("InsumosFragment", "Lista de animales es null");
                animalesLista = new ArrayList<>();
            }
        });

        bottomViewModel.cargarInsumos();
        bottomViewModel.cargarAnimales();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        binding.insumosRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        insumosAdapter = new InsumosAdapter(insumoList, this::mostrarDialogoRegistroUso);
        binding.insumosRecyclerView.setAdapter(insumosAdapter);
    }

    private void setupSearch() {
        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                insumosAdapter.filtrar(s.toString());
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void setupFab() {
        binding.addInsumoFab.setOnClickListener(v -> {
            // Añadir nuevo insumo (Lo tengo que hacer :) )
        });
    }

    private void mostrarDialogoRegistroUso(Insumo insumo) {
        Log.d("InsumosFragment", "Mostrar diálogo registro uso para insumo: " + insumo.getNombre());

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_registro_uso_insumo, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        TextView insumoNombreTextView = dialogView.findViewById(R.id.insumoNombreTextView);
        Spinner animalSpinner = dialogView.findViewById(R.id.animalSpinner);
        EditText cantidadEditText = dialogView.findViewById(R.id.cantidadEditText);
        EditText fechaEditText = dialogView.findViewById(R.id.fechaEditText);
        Button cancelarBtn = dialogView.findViewById(R.id.cancelarBtn);
        Button guardarBtn = dialogView.findViewById(R.id.guardarBtn);

        insumoNombreTextView.setText("Insumo: " + insumo.getNombre());

        final Calendar calendar = Calendar.getInstance();
        fechaEditText.setOnClickListener(v -> {
            Log.d("InsumosFragment", "Fecha editText clickeado para mostrar DatePicker");
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        String fecha = year + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", dayOfMonth);
                        Log.d("InsumosFragment", "Fecha seleccionada: " + fecha);
                        fechaEditText.setText(fecha);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        if (animalesLista != null && !animalesLista.isEmpty()) {
            ArrayAdapter<Animal> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    animalesLista
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            animalSpinner.setAdapter(adapter);

            Log.d("InsumosFragment", "Adapter seteado con " + animalesLista.size() + " animales.");

            animalSpinner.setOnTouchListener((v, event) -> {
                Log.d("InsumosFragment", "Spinner tocado, evento: " + event.getAction());
                return false;
            });
        } else {
            Log.d("InsumosFragment", "No hay animales para mostrar en Spinner");
            Toast.makeText(requireContext(), "No hay animales disponibles para seleccionar.", Toast.LENGTH_SHORT).show();
        }

        cancelarBtn.setOnClickListener(v -> {
            Log.d("InsumosFragment", "Dialogo cancelado");
            dialog.dismiss();
        });

        guardarBtn.setOnClickListener(v -> {
            String cantidadStr = cantidadEditText.getText().toString();
            String fecha = fechaEditText.getText().toString();
            Log.d("InsumosFragment", "Guardar clickeado - cantidad: " + cantidadStr + ", fecha: " + fecha);

            if (cantidadStr.isEmpty() || fecha.isEmpty() || animalSpinner.getSelectedItem() == null) {
                Log.d("InsumosFragment", "Campos incompletos o sin selección en Spinner");
                Toast.makeText(getContext(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            double cantidadUsada = Double.parseDouble(cantidadStr);
            Animal animalSeleccionado = (Animal) animalSpinner.getSelectedItem();
            Log.d("InsumosFragment", "Animal seleccionado: " + animalSeleccionado.getId());

            String animalId = animalSeleccionado.getId();
            String fincaId = animalSeleccionado.getFincaId();

            bottomViewModel.registrarUsoInsumo(insumo.getId(), animalId, fecha, fincaId, cantidadUsada);

            Log.d("InsumosFragment", "Uso de insumo registrado");
            dialog.dismiss();
        });

        dialog.show();
        Log.d("InsumosFragment", "Diálogo mostrado");
    }
}
