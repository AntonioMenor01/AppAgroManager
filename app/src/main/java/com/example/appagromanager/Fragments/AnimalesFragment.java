package com.example.appagromanager.Fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

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

public class AnimalesFragment extends Fragment {

    private FragmentAnimalesBinding binding;
    private BottomViewModel viewModel;
    private AnimalAdapter adapter;

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

        viewModel = new ViewModelProvider(this).get(BottomViewModel.class);

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

        viewModel.getAnimales().observe(getViewLifecycleOwner(), animales -> {
            Log.d("AnimalesFragment", "Animales obtenidos del ViewModel: " + (animales != null ? animales.size() : 0));
            if (animales != null) {
                adapter.setAnimales(animales);
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
                viewModel.obtenerAnimalesPorGrupo(grupoSeleccionado);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        binding.busquedaEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("AnimalesFragment", "Texto de búsqueda cambiado: " + s.toString());
                adapter.filtrarPorCrotal(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.obtenerAnimalesPorGrupo("");
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}