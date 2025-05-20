package com.example.appagromanager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.appagromanager.Fragments.PiensoDetalleFragment;
import com.example.appagromanager.Fragments.PiensoEstadisticaFragment;

public class PiensoPagerAdapter extends FragmentStateAdapter {
    public PiensoPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new PiensoDetalleFragment();
            case 1:
                return new PiensoEstadisticaFragment();
            default:
                return new Fragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
