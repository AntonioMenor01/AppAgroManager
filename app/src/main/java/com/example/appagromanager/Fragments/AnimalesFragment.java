package com.example.appagromanager.Fragments;

import android.annotation.SuppressLint;
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
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.appagromanager.viewmodel.BottomViewModel;
import com.example.appagromanager.adapter.AnimalAdapter;
import com.example.appagromanager.R;
import com.example.appagromanager.databinding.FragmentAnimalesBinding;
import com.example.appagromanager.models.Animal;
import com.example.appagromanager.models.Finca;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AnimalesFragment extends Fragment {

    private FragmentAnimalesBinding binding;
    private BottomViewModel bottomViewModel;
    private AnimalAdapter adapter;
    private List<Finca> listaFincas = null;

    private AlertDialog dialogNuevoAnimal;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAnimalesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
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

        binding.grupoSpinner.setText(adapterSpinner.getItem(0), false);

        binding.grupoSpinner.setOnTouchListener((v, event) -> {
            if (!binding.grupoSpinner.isPopupShowing()) {
                String textoActual = binding.grupoSpinner.getText().toString();
                binding.grupoSpinner.setText("", false);
                binding.grupoSpinner.showDropDown();
                binding.grupoSpinner.post(() -> binding.grupoSpinner.setText(textoActual, false));
            }
            return false;
        });

        binding.grupoSpinner.setOnItemClickListener((parent, view1, position, id) -> {
            String grupoSeleccionado = (String) parent.getItemAtPosition(position);
            Log.d("AnimalesFragment", "Grupo seleccionado: " + grupoSeleccionado);
            bottomViewModel.obtenerAnimalesPorGrupo(grupoSeleccionado);
        });


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

        binding.busquedaEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filtrarPorCrotal(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        TextWatcher filtrosWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                aplicarFiltros();
            }
            @Override public void afterTextChanged(Editable s) {}
        };

        binding.pesoMinEditText.addTextChangedListener(filtrosWatcher);
        binding.pesoMaxEditText.addTextChangedListener(filtrosWatcher);
        binding.edadMinEditText.addTextChangedListener(filtrosWatcher);
        binding.edadMaxEditText.addTextChangedListener(filtrosWatcher);

        binding.nuevoAnimal.setOnClickListener(v -> {
            mostrarDialogoNuevoAnimal();
        });

        bottomViewModel.getCreado().observe(getViewLifecycleOwner(), creado -> {
            if (creado != null && creado) {
                Toast.makeText(getContext(), "Animal añadido con éxito", Toast.LENGTH_SHORT).show();
                bottomViewModel.resetCreado();

                String grupoSeleccionado = binding.grupoSpinner.getText().toString();
                bottomViewModel.obtenerAnimalesPorGrupo(grupoSeleccionado);

                if (dialogNuevoAnimal != null && dialogNuevoAnimal.isShowing()) {
                    dialogNuevoAnimal.dismiss();
                }
            }
        });
    }

    private void aplicarFiltros() {
        String pesoMinStr = binding.pesoMinEditText.getText().toString().trim();
        String pesoMaxStr = binding.pesoMaxEditText.getText().toString().trim();
        String edadMinStr = binding.edadMinEditText.getText().toString().trim();
        String edadMaxStr = binding.edadMaxEditText.getText().toString().trim();
        String grupoSeleccionado = binding.grupoSpinner.getText().toString();
        String crotal = binding.busquedaEditText.getText().toString().trim();

        Double pesoMin = pesoMinStr.isEmpty() ? null : Double.parseDouble(pesoMinStr);
        Double pesoMax = pesoMaxStr.isEmpty() ? null : Double.parseDouble(pesoMaxStr);
        Integer edadMin = edadMinStr.isEmpty() ? null : Integer.parseInt(edadMinStr);
        Integer edadMax = edadMaxStr.isEmpty() ? null : Integer.parseInt(edadMaxStr);

        bottomViewModel.filtrarAnimales(crotal, grupoSeleccionado, pesoMin, pesoMax, edadMin, edadMax);
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

        try {
            ArrayAdapter<CharSequence> grupoAdapter = ArrayAdapter.createFromResource(
                    requireContext(),
                    R.array.grupos_array,
                    android.R.layout.simple_spinner_item
            );
            grupoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            grupoSpinner.setAdapter(grupoAdapter);
        } catch (Exception e) {
            Log.e("AnimalesFragment", "Error al cargar grupos: " + e.getMessage());
            Toast.makeText(getContext(), "Error al cargar los grupos", Toast.LENGTH_SHORT).show();
        }

        fechaNacimientoEditText.setOnClickListener(v -> {
            try {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        requireContext(),
                        (view, year, month, dayOfMonth) -> {
                            String fecha = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                            fechaNacimientoEditText.setText(fecha);
                        },
                        2025, 0, 1
                );
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            } catch (Exception e) {
                Log.e("AnimalesFragment", "Error con el DatePicker: " + e.getMessage());
                Toast.makeText(getContext(), "Error al seleccionar la fecha", Toast.LENGTH_SHORT).show();
            }
        });

        try {
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
                    Collections.sort(fincas, Comparator.comparing(Finca::getNombre));
                } else {
                    Log.e("AnimalesFragment", "Lista de fincas vacía o nula");
                    Toast.makeText(getContext(), "No se encontraron fincas disponibles", Toast.LENGTH_SHORT).show();
                }
            });
            bottomViewModel.obtenerFincas();
        } catch (Exception e) {
            Log.e("AnimalesFragment", "Error al observar fincas: " + e.getMessage());
            Toast.makeText(getContext(), "Error al cargar fincas", Toast.LENGTH_SHORT).show();
        }

        dialogNuevoAnimal = builder
                .setTitle("Nuevo Animal")
                .setPositiveButton("Guardar", null)
                .setNegativeButton("Cancelar", (d, which) -> d.dismiss())
                .create();

        dialogNuevoAnimal.show();

        dialogNuevoAnimal.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String crotal = crotalEditText.getText().toString().trim();
            String pesoStr = pesoEditText.getText().toString().trim();
            String fechaNacimiento = fechaNacimientoEditText.getText().toString().trim();
            String grupo = (grupoSpinner.getSelectedItem() != null) ? grupoSpinner.getSelectedItem().toString() : "";
            Finca fincaSeleccionada = (Finca) fincaSpinner.getSelectedItem();

            if (crotal.isEmpty() || pesoStr.isEmpty() || fechaNacimiento.isEmpty() || grupo.isEmpty() || fincaSeleccionada == null) {
                Toast.makeText(getContext(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (grupo.equals("Selecciona un grupo ...")) {
                Toast.makeText(getContext(), "Por favor selecciona un grupo válido", Toast.LENGTH_SHORT).show();
                return;
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

            bottomViewModel.getAnimalesPorFinca(fincaSeleccionada.getId()).observe(getViewLifecycleOwner(), animales -> {
                if (animales == null) {
                    Toast.makeText(getContext(), "Error al obtener animales de la finca", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (animales.size() >= fincaSeleccionada.getCapacidad()) {
                    Toast.makeText(getContext(), "La finca ha alcanzado su límite de animales", Toast.LENGTH_SHORT).show();
                    return;
                }

                observarUnaVez(bottomViewModel.verificarCrotal(crotal), getViewLifecycleOwner(), enUso -> {
                    if (Boolean.TRUE.equals(enUso)) {
                        Toast.makeText(getContext(), "El crotal ya está en uso", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            Animal nuevoAnimal = new Animal();
                            nuevoAnimal.setCrotal(crotal);
                            nuevoAnimal.setPeso(peso);
                            nuevoAnimal.setGrupo(grupo);
                            nuevoAnimal.setFincaId(fincaSeleccionada.getId());
                            nuevoAnimal.setFechaNacimiento(fechaNacimiento);

                            String fechaInsercion = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                            nuevoAnimal.setFechaInsercion(fechaInsercion);

                            String piensoid = obtenerPiensoIdPorGrupo(grupo);
                            if (piensoid == null) {
                                Toast.makeText(getContext(), "No hay pienso asignado para este grupo", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            nuevoAnimal.setPiensoId(piensoid);

                            bottomViewModel.crearAnimal(nuevoAnimal);

                            dialogNuevoAnimal.dismiss();
                        } catch (Exception e) {
                            Log.e("AnimalesFragment", "Error al crear el animal: " + e.getMessage());
                            Toast.makeText(getContext(), "Error al guardar el animal", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            });
        });

        bottomViewModel.refrescarFincas();
    }

    public static <T> void observarUnaVez(LiveData<T> liveData, LifecycleOwner owner, Observer<T> observer) {
        liveData.observe(owner, new Observer<T>() {
            @Override
            public void onChanged(T t) {
                observer.onChanged(t);
                liveData.removeObserver(this);
            }
        });
    }

    private String obtenerPiensoIdPorGrupo(String grupo) {
        switch (grupo.toUpperCase(Locale.ROOT)) {
            case "VACA":
            case "VACUNO":
                return "039e50ec-eabc-4b9f-8977-b89ad74fb4fb";
            case "CERDO":
            case "PORCINO":
                return "37084c15-4c99-4279-a5be-3c5fb8afe675";
            case "OVEJA":
            case "OVINO":
                return "bc097912-1c87-4365-aac0-aa59d19e4149";
            default:
                return null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        String textoActual = binding.grupoSpinner.getText().toString();
        binding.grupoSpinner.setText("", false);

        ArrayAdapter<CharSequence> adapterSpinner = (ArrayAdapter<CharSequence>) binding.grupoSpinner.getAdapter();
        if (adapterSpinner != null) {
            adapterSpinner.getFilter().filter(null);
        }
        binding.grupoSpinner.post(() -> binding.grupoSpinner.setText(textoActual, false));
        bottomViewModel.obtenerAnimalesPorGrupo("");
        bottomViewModel.obtenerFincas();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}