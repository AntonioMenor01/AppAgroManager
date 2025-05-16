package com.example.appagromanager.Fragments;

import static androidx.navigation.Navigation.findNavController;
import static com.example.appagromanager.Fragments.AnimalesFragment.observarUnaVez;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.example.appagromanager.BottomViewModel;
import com.example.appagromanager.R;
import com.example.appagromanager.databinding.FragmentDetalleAnimalBinding;
import com.example.appagromanager.models.Animal;
import com.example.appagromanager.models.Finca;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import androidx.navigation.fragment.NavHostFragment;

public class DetalleAnimalFragment extends Fragment {

    private FragmentDetalleAnimalBinding binding;
    private BottomViewModel bottomViewModel;
    private Animal animal;
    private static final String TAG = "DetalleAnimalFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDetalleAnimalBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        animal = getArguments().getParcelable("animal");

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

        bottomViewModel.getEliminado().observe(getViewLifecycleOwner(), eliminado -> {
            if (eliminado != null && eliminado) {
                Toast.makeText(requireContext(), "Animal eliminado correctamente", Toast.LENGTH_SHORT).show();
                bottomViewModel.resetEliminado();
                NavHostFragment.findNavController(DetalleAnimalFragment.this).popBackStack();
            } else if (eliminado != null) {
                Toast.makeText(requireContext(), "Error al eliminar el animal", Toast.LENGTH_SHORT).show();
                bottomViewModel.resetEliminado();
            }
        });

        binding.btnEliminar.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Confirmar eliminación")
                    .setMessage("¿Estás seguro de que quieres eliminar este animal?")
                    .setPositiveButton("Eliminar", (dialog, which) -> {
                        String animalId = animal.getId();
                        bottomViewModel.eliminarAnimal(animalId);
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        binding.btnEditar.setOnClickListener(v -> {
            mostrarDialogoEdicion();
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


    private void mostrarDialogoEdicion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("¿Qué deseas editar?");
        String[] opciones = {"Crotal", "Peso", "Finca"};

        builder.setItems(opciones, (dialog, which) -> {
            switch (which) {
                case 0: mostrarDialogoTexto("Editar Crotal", binding.textCrotal); break;
                case 1: mostrarDialogoTexto("Editar Peso", binding.textPeso); break;
                case 2: mostrarDialogoFinca(); break;
            }
        });

        builder.show();
    }

    private void mostrarDialogoTexto(String titulo, TextView textView) {
        final EditText input = new EditText(requireContext());
        String textoActual = textView.getText().toString();
        if (titulo.equals("Editar Peso")) {
            textoActual = textoActual.replace(" kg", "").trim();
            input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }
        input.setText(textoActual);
        input.setPadding(40, 30, 40, 30);
        input.setTextSize(18);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(titulo);
        builder.setView(input);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nuevoTexto = input.getText().toString().trim();
            if (titulo.equals("Editar Peso")) {
                nuevoTexto = nuevoTexto + " kg";
            }
            textView.setText(nuevoTexto);
            actualizarAnimalEnBD();
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void mostrarDialogoFinca() {
        bottomViewModel.obtenerFincas();
        bottomViewModel.getFincas().observe((LifecycleOwner) requireContext(), fincas -> {
            if (fincas != null) {
                String[] nombresFincas = new String[fincas.size()];
                for (int i = 0; i < fincas.size(); i++) {
                    nombresFincas[i] = fincas.get(i).getNombre();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Selecciona una finca");
                builder.setSingleChoiceItems(nombresFincas, -1, (dialog, which) -> {
                    binding.textFinca.setText(nombresFincas[which]);
                    dialog.dismiss();

                    actualizarAnimalEnBD();
                });

                builder.setNegativeButton("Cancelar", null);
                builder.show();
            }
        });
    }

    private void actualizarAnimalEnBD() {
        String crotal = binding.textCrotal.getText().toString().trim();
        String peso = binding.textPeso.getText().toString().replace(" kg", "").trim();
        String nombreFinca = binding.textFinca.getText().toString().trim();

        String fincaId = null;
        List<Finca> fincas = bottomViewModel.getFincas().getValue();
        if (fincas != null) {
            for (Finca f : fincas) {
                if (f.getNombre().equals(nombreFinca)) {
                    fincaId = f.getId();
                    break;
                }
            }
        }

        if (fincaId == null) {
            Toast.makeText(requireContext(), "Finca no encontrada", Toast.LENGTH_SHORT).show();
            return;
        }

        double pesoDouble;
        try {
            pesoDouble = Double.parseDouble(peso);
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Peso inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!crotal.equals(animal.getCrotal())) {
            String finalFincaId = fincaId;
            observarUnaVez(bottomViewModel.verificarCrotal(crotal), getViewLifecycleOwner(), enUso -> {
                if (Boolean.TRUE.equals(enUso)) {
                    Toast.makeText(requireContext(), "El crotal ya está en uso", Toast.LENGTH_SHORT).show();
                    binding.textCrotal.requestFocus();
                    binding.textCrotal.setText(animal.getCrotal());
                    InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.showSoftInput(binding.textCrotal, InputMethodManager.SHOW_IMPLICIT);
                    }
                } else {
                    ejecutarActualizacion(animal, crotal, pesoDouble, finalFincaId);
                }
            });
        } else {
            ejecutarActualizacion(animal, crotal, pesoDouble, fincaId);
        }

    }

    private void ejecutarActualizacion(Animal animal, String crotal, double peso, String fincaId) {
        animal.setCrotal(crotal);
        animal.setPeso(peso);
        animal.setFincaId(fincaId);

        bottomViewModel.actualizarAnimal(animal.getId(), animal);
        binding.textPeso.setText(animal.getPeso() + " kg");

        observarUnaVez(bottomViewModel.getActualizado(), getViewLifecycleOwner(), resultado -> {
            if (Boolean.TRUE.equals(resultado)) {
                Toast.makeText(requireContext(), "Animal actualizado con éxito", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Error al actualizar el animal", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
