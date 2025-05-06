package com.example.appagromanager;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.appagromanager.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host);
        NavController navController = navHostFragment.getNavController();

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.homeFragment,
                R.id.piensoFragment,
                R.id.animalesFragment,
                R.id.insumosFragment,
                R.id.fincasFragment,
                R.id.detallesUser,
                R.id.cambiarPasswd,
                R.id.cerrarSesion
        ).setOpenableLayout(binding.drawerLayout).build();

        setSupportActionBar(binding.toolbar);
        NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfiguration);

        NavigationUI.setupWithNavController(binding.navView, navController);

        NavigationUI.setupWithNavController(binding.bottomNav, navController);
        binding.bottomNav.setLabelVisibilityMode(
                com.google.android.material.navigation.NavigationBarView.LABEL_VISIBILITY_UNLABELED
        );
    }
}
