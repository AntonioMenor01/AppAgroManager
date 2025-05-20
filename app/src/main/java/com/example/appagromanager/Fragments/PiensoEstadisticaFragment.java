package com.example.appagromanager.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.appagromanager.R;
import com.example.appagromanager.databinding.FragmentPiensoDetalleBinding;
import com.example.appagromanager.databinding.FragmentPiensoEstadisticaBinding;

public class PiensoEstadisticaFragment extends Fragment {

    private FragmentPiensoEstadisticaBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        binding = FragmentPiensoEstadisticaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}