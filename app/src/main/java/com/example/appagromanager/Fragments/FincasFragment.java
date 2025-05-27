package com.example.appagromanager.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.appagromanager.R;
import com.example.appagromanager.adapter.FincaAdapter;
import com.example.appagromanager.databinding.FragmentFincasBinding;
import com.example.appagromanager.viewmodel.BottomViewModel;

import java.util.ArrayList;

public class FincasFragment extends Fragment {

    private FragmentFincasBinding binding;
    private BottomViewModel bottomViewModel;
    private FincaAdapter fincaAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFincasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bottomViewModel = new ViewModelProvider(this).get(BottomViewModel.class);

        binding.recyclerFincas.setLayoutManager(new LinearLayoutManager(getContext()));

        fincaAdapter = new FincaAdapter(new ArrayList<>(), finca -> {
            Bundle bundle = new Bundle();
            bundle.putString("finca_id", finca.getId());
            Navigation.findNavController(view).navigate(R.id.detalleFincaFragment, bundle);
        });

        binding.recyclerFincas.setAdapter(fincaAdapter);

        bottomViewModel.getFincasConAnimales().observe(getViewLifecycleOwner(), fincas -> {
            if (fincas != null) {
                fincaAdapter.updateList(fincas);
            }
        });

        binding.nuevaFinca.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.crearFincaFragment);
        });

        bottomViewModel.obtenerFincasConAnimales();
    }
}