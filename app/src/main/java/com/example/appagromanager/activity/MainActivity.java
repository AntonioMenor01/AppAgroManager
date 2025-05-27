package com.example.appagromanager.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
        NavigationUI.setupWithNavController(binding.bottomNav, navController);
        NavigationUI.setupWithNavController(binding.navView, navController);

        binding.bottomNav.setLabelVisibilityMode(
                com.google.android.material.navigation.NavigationBarView.LABEL_VISIBILITY_UNLABELED
        );

        binding.navView.getMenu().findItem(R.id.cerrarSesion).setOnMenuItemClickListener(item -> {
            mostrarDialogoCerrarSesion();
            return true;
        });

        loadProfileImageIntoDrawer();

        DrawerViewModel drawerViewModel = new ViewModelProvider(this).get(DrawerViewModel.class);
        drawerViewModel.getUsuario().observe(this, usuario -> {
            if (usuario != null) {
                TextView textView = binding.navView.getHeaderView(0).findViewById(R.id.text);
                textView.setText(usuario.getNombre() + " " + usuario.getApellidos());
            }
        });
    }

    private void mostrarDialogoCerrarSesion() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Seguro que quieres cerrar sesión?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
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