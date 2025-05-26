package com.example.appagromanager.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.appagromanager.adapter.PiensoPagerAdapter;
import com.example.appagromanager.databinding.FragmentPiensoBinding;
import com.google.android.material.tabs.TabLayoutMediator;


public class PiensoFragment extends Fragment {

    private FragmentPiensoBinding binding;
    private PiensoPagerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPiensoBinding.inflate(inflater, container, false);

        adapter = new PiensoPagerAdapter(this);
        binding.viewPager.setAdapter(adapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Suministrar");
                            break;
                        case 1:
                            tab.setText("Almacen");
                            break;
                    }
                }
        ).attach();

        return binding.getRoot();
    }
}
