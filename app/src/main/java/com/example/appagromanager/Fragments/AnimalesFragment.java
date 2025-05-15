package com.example.appagromanager.Fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.appagromanager.BottomViewModel;
import com.example.appagromanager.AnimalAdapter;
import com.example.appagromanager.R;
import com.example.appagromanager.databinding.FragmentAnimalesBinding;
import com.example.appagromanager.models.Animal;
import com.example.appagromanager.models.Finca;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AnimalesFragment extends Fragment {

    private FragmentAnimalesBinding binding;
    private BottomViewModel bottomViewModel;
    private AnimalAdapter adapter;
    private List<Finca> listaFincas = null;

    // Variable para controlar el diálogo y cerrarlo desde el observer
    private AlertDialog dialogNuevoAnimal;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAnimalesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.grupos_array,
                android.R.layout.simple_spinner_item
        );
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.grupoSpinner.setAdapter(adapterSpinner);
        Log.d("AnimalesFragment", "Spinner de grupos configurado");

        bottomViewModel = new ViewModelProvider(this).get(BottomViewModel.class);

        binding.animalesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AnimalAdapter(null, animal -> {
            Log.d("AnimalesFragment", "Animal seleccionado: " + animal.getCrotal());

            NavController navController = NavHostFragment.findNavController(AnimalesFragment.this);
            Bundle bundle = new Bundle();
            bundle.putParcelable("animal", animal);
            navController.navigate(R.id.action_animalesFragment_to_detalleAnimalFragment, bundle);
        });
        binding.animalesRecyclerView.setAdapter(adapter);
        Log.d("AnimalesFragment", "RecyclerView configurado");

        bottomViewModel.getAnimales().observe(getViewLifecycleOwner(), animales -> {
            Log.d("AnimalesFragment", "Animales obtenidos del ViewModel: " + (animales != null ? animales.size() : 0));
            if (animales != null && !animales.isEmpty()) {
                adapter.setAnimales(animales);
                binding.animalesRecyclerView.setVisibility(View.VISIBLE);
                binding.noAnimalesTextView.setVisibility(View.GONE);
                Log.d("AnimalesFragment", "Animales actualizados en el adaptador: " + animales.size());
            } else {
                Log.d("AnimalesFragment", "No se encontraron animales");
                binding.animalesRecyclerView.setVisibility(View.GONE);
                binding.noAnimalesTextView.setVisibility(View.VISIBLE);
            }
        });

        binding.grupoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view1, int position, long id) {
                String grupoSeleccionado = (String) parent.getItemAtPosition(position);
                Log.d("AnimalesFragment", "Grupo seleccionado: " + grupoSeleccionado);
                bottomViewModel.obtenerAnimalesPorGrupo(grupoSeleccionado);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        binding.busquedaEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filtrarPorCrotal(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.nuevoAnimal.setOnClickListener(v -> {
            mostrarDialogoNuevoAnimal();
        });

        // Observer único para el LiveData 'creado' que controla la respuesta a creación de animal
        bottomViewModel.getCreado().observe(getViewLifecycleOwner(), creado -> {
            if (creado != null && creado) {
                Toast.makeText(getContext(), "Animal añadido con éxito", Toast.LENGTH_SHORT).show();
                bottomViewModel.resetCreado();

                String grupoSeleccionado = (String) binding.grupoSpinner.getSelectedItem();
                bottomViewModel.obtenerAnimalesPorGrupo(grupoSeleccionado);

                // Cerrar diálogo si está abierto
                if (dialogNuevoAnimal != null && dialogNuevoAnimal.isShowing()) {
                    dialogNuevoAnimal.dismiss();
                }
            } else if (creado != null) {
                Toast.makeText(getContext(), "Campos rellenados erroneamente", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogoNuevoAnimal() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.nuevo_animal, null);
        builder.setView(dialogView);

        EditText crotalEditText = dialogView.findViewById(R.id.crotalEditText);
        EditText pesoEditText = dialogView.findViewById(R.id.pesoEditText);
        EditText fechaNacimientoEditText = dialogView.findViewById(R.id.fechaNacimientoEditText);
        Spinner grupoSpinner = dialogView.findViewById(R.id.grupoSpinner);
        Spinner fincaSpinner = dialogView.findViewById(R.id.fincaSpinner);

        ArrayAdapter<CharSequence> grupoAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.grupos_array,
                android.R.layout.simple_spinner_item
        );
        grupoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        grupoSpinner.setAdapter(grupoAdapter);

        fechaNacimientoEditText.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        String fecha = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                        fechaNacimientoEditText.setText(fecha);
                    },
                    2020, 0, 1
            );
            datePickerDialog.show();
        });

        // Observar las fincas para llenar el spinner
        bottomViewModel.getFincas().observe(getViewLifecycleOwner(), fincas -> {
            if (fincas != null && !fincas.isEmpty()) {
                listaFincas = fincas;
                ArrayAdapter<Finca> fincaAdapter = new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        fincas
                );
                fincaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                fincaSpinner.setAdapter(fincaAdapter);
            } else {
                Log.e("AnimalesFragment", "Lista de fincas está vacía o null");
            }
        });

        bottomViewModel.obtenerFincas();

        dialogNuevoAnimal = builder
                .setTitle("Nuevo Animal")
                .setPositiveButton("Guardar", null)  // Listener null para sobrescribir después
                .setNegativeButton("Cancelar", (d, which) -> d.dismiss())
                .create();

        dialogNuevoAnimal.show();

        dialogNuevoAnimal.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String crotal = crotalEditText.getText().toString().trim();
            String pesoStr = pesoEditText.getText().toString().trim();
            String fechaNacimiento = fechaNacimientoEditText.getText().toString().trim();
            String grupo = grupoSpinner.getSelectedItem().toString();
            Finca fincaSeleccionada = (Finca) fincaSpinner.getSelectedItem();

            if (crotal.isEmpty() || pesoStr.isEmpty() || fechaNacimiento.isEmpty() || fincaSeleccionada == null) {
                Toast.makeText(getContext(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                return;  // NO cierra el diálogo
            }

            double peso;
            try {
                peso = Double.parseDouble(pesoStr);
                if (peso <= 0) {
                    Toast.makeText(getContext(), "El peso debe ser mayor que cero", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Peso inválido", Toast.LENGTH_SHORT).show();
                return;
            }

            Animal nuevoAnimal = new Animal();
            nuevoAnimal.setCrotal(crotal);
            nuevoAnimal.setPeso(peso);
            nuevoAnimal.setGrupo(grupo);
            nuevoAnimal.setFincaId(fincaSeleccionada.getId());
            nuevoAnimal.setFechaNacimiento(fechaNacimiento);

            String fechaInsercion = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            nuevoAnimal.setFechaInsercion(fechaInsercion);

            bottomViewModel.crearAnimal(nuevoAnimal);
            // No cerramos el diálogo aquí, esperamos respuesta LiveData
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        bottomViewModel.obtenerAnimalesPorGrupo("");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}