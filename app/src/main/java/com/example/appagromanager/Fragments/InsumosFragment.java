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
import com.google.android.material.snackbar.Snackbar;

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

        binding.addInsumoFab.setOnClickListener(v -> {
            if (insumoList.isEmpty()) {
                Toast.makeText(getContext(), "No hay insumos para reponer", Toast.LENGTH_SHORT).show();
                return;
            }
            String[] insumosNombres = new String[insumoList.size()];
            for (int i = 0; i < insumoList.size(); i++) {
                insumosNombres[i] = insumoList.get(i).getNombre();
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Selecciona un insumo a reponer")
                    .setItems(insumosNombres, (dialog, which) -> {
                        Insumo insumoSeleccionado = insumoList.get(which);
                        mostrarDialogoReponerInsumo(insumoSeleccionado);
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
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
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        String fecha = year + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", dayOfMonth);
                        fechaEditText.setText(fecha);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
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

            if (cantidadStr.isEmpty() || fecha.isEmpty() || animalSpinner.getSelectedItem() == null) {
                Snackbar snackbar = Snackbar.make(dialogView,
                        "Por favor completa todos los campos",
                        Snackbar.LENGTH_LONG);
                snackbar.setBackgroundTint(getResources().getColor(R.color.verde));
                snackbar.setTextColor(getResources().getColor(android.R.color.white));
                snackbar.show();
                return;
            }

            double cantidadUsada = Double.parseDouble(cantidadStr);

            if (cantidadUsada > insumo.getCantidad()) {
                Snackbar snackbar = Snackbar.make(dialogView,
                        "No puedes usar más de lo disponible: " + insumo.getCantidad(),
                        Snackbar.LENGTH_LONG);
                snackbar.setBackgroundTint(getResources().getColor(R.color.verde));
                snackbar.setTextColor(getResources().getColor(android.R.color.white));
                snackbar.show();
                return;
            }

            Animal animalSeleccionado = (Animal) animalSpinner.getSelectedItem();
            String animalId = animalSeleccionado.getId();
            String fincaId = animalSeleccionado.getFincaId();

            bottomViewModel.registrarUsoInsumo(insumo.getId(), animalId, fecha, fincaId, cantidadUsada);

            double nuevaCantidad = insumo.getCantidad() - cantidadUsada;
            if (nuevaCantidad < 0) nuevaCantidad = 0;

            bottomViewModel.actualizarCantidadInsumo(insumo.getId(), nuevaCantidad);

            insumo.setCantidad(nuevaCantidad);
            insumosAdapter.notifyDataSetChanged();

            Snackbar snackbar = Snackbar.make(binding.getRoot(),
                    "Uso de insumo registrado correctamente",
                    Snackbar.LENGTH_LONG);
            View snackbarView = snackbar.getView();
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) snackbarView.getLayoutParams();
            params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, 775);
            snackbarView.setLayoutParams(params);

            snackbar.setBackgroundTint(getResources().getColor(R.color.verde));
            snackbar.setTextColor(getResources().getColor(android.R.color.white));
            snackbar.show();

            dialog.dismiss();
        });

        dialog.show();
    }

    private void mostrarDialogoReponerInsumo(Insumo insumo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_reponer_insumo, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        TextView insumoNombreTextView = dialogView.findViewById(R.id.insumoNombreTextView);
        EditText cantidadReponerEditText = dialogView.findViewById(R.id.cantidadReponerEditText);
        Button cancelarBtn = dialogView.findViewById(R.id.cancelarBtn);
        Button guardarBtn = dialogView.findViewById(R.id.guardarBtn);

        insumoNombreTextView.setText("Insumo: " + insumo.getNombre());

        cancelarBtn.setOnClickListener(v -> dialog.dismiss());

        guardarBtn.setOnClickListener(v -> {
            String cantidadStr = cantidadReponerEditText.getText().toString();

            if (cantidadStr.isEmpty()) {
                Snackbar snackbar = Snackbar.make(dialogView,
                        "Por favor ingresa la cantidad a reponer",
                        Snackbar.LENGTH_LONG);
                snackbar.setBackgroundTint(getResources().getColor(R.color.verde));
                snackbar.setTextColor(getResources().getColor(android.R.color.white));
                snackbar.show();
                return;
            }

            double cantidadAReponer = Double.parseDouble(cantidadStr);
            if (cantidadAReponer <= 0) {
                Snackbar snackbar = Snackbar.make(dialogView,
                        "La cantidad debe ser mayor que cero",
                        Snackbar.LENGTH_LONG);
                snackbar.setBackgroundTint(getResources().getColor(R.color.verde));
                snackbar.setTextColor(getResources().getColor(android.R.color.white));
                snackbar.show();
                return;
            }

            double nuevaCantidad = insumo.getCantidad() + cantidadAReponer;

            bottomViewModel.actualizarCantidadInsumo(insumo.getId(), nuevaCantidad);

            insumo.setCantidad(nuevaCantidad);
            insumosAdapter.notifyDataSetChanged();

            Snackbar snackbar = Snackbar.make(binding.getRoot(),
                    "Insumo repuesto correctamente",
                    Snackbar.LENGTH_LONG);
            snackbar.setBackgroundTint(getResources().getColor(R.color.verde));
            snackbar.setTextColor(getResources().getColor(android.R.color.white));
            snackbar.show();

            dialog.dismiss();
        });

        dialog.show();
    }
}