package com.example.appagromanager.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.appagromanager.viewmodel.DrawerViewModel;
import com.example.appagromanager.R;
import com.example.appagromanager.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.io.FileInputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNav.setItemIconTintList(null);

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
        loadProfileImageIntoDrawer();
        DrawerViewModel drawerViewModel = new ViewModelProvider(this).get(DrawerViewModel.class);

        drawerViewModel.getUsuario().observe(this, usuario -> {
            if (usuario != null) {
                TextView textView = binding.navView.getHeaderView(0).findViewById(R.id.text);
                textView.setText(usuario.getNombre() + " " + usuario.getApellidos());
            }
        });
    }

    private void loadProfileImageIntoDrawer() {
        ImageView drawerImageView = binding.navView.getHeaderView(0).findViewById(R.id.imageView);
        try {
            FileInputStream fis = openFileInput("selected_image.png");
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            drawerImageView.setImageBitmap(bitmap);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadProfileImageIntoDrawer();
    }
}
