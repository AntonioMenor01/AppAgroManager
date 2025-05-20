package com.example.appagromanager.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.example.appagromanager.databinding.FragmentPiensoDetalleBinding;
public class PiensoDetalleFragment extends Fragment {
    private FragmentPiensoDetalleBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPiensoDetalleBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}
