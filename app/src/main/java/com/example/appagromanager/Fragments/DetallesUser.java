package com.example.appagromanager.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.appagromanager.viewmodel.DrawerViewModel;
import com.example.appagromanager.databinding.FragmentDetallesUserBinding;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DetallesUser extends Fragment {

    private FragmentDetallesUserBinding binding;
    private DrawerViewModel viewModel;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private static final String IMAGE_FILENAME = "selected_image.png";

    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDetallesUserBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(this).get(DrawerViewModel.class);

        viewModel.getUsuario().observe(getViewLifecycleOwner(), usuario -> {
            if (usuario != null) {
                binding.editNombre.setText(usuario.getNombre());
                binding.editApellidos.setText(usuario.getApellidos());
                binding.editEmail.setText(usuario.getEmail());
            } else {
                binding.tvNombre.setText("Usuario no encontrado");
                binding.tvEmail.setText("");
            }
        });

        Bitmap savedImage = loadImageFromStorage();
        if (savedImage != null) {
            binding.icon.setImageBitmap(savedImage);
        }

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            try {
                                // Usar getScaledBitmap para cargar la imagen con tamaño adecuado
                                int targetWidth = binding.icon.getWidth() > 0 ? binding.icon.getWidth() : 120; // fallback a 120dp si width no está definido aún
                                int targetHeight = binding.icon.getHeight() > 0 ? binding.icon.getHeight() : 120; // fallback a 120dp
                                Bitmap selectedImageBitmap = getScaledBitmap(selectedImageUri, targetWidth, targetHeight);
                                binding.icon.setImageBitmap(selectedImageBitmap);
                                saveImageToStorage(selectedImageBitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(context, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );

        binding.iconEdit.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        return binding.getRoot();
    }

    private Bitmap getScaledBitmap(Uri imageUri, int targetWidth, int targetHeight) throws IOException {
        // 1. Obtener opciones solo para obtener dimensiones
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try (InputStream input = context.getContentResolver().openInputStream(imageUri)) {
            BitmapFactory.decodeStream(input, null, options);
        }

        // 2. Calcular factor de escala (mínimo 1 para evitar errores)
        int photoWidth = options.outWidth;
        int photoHeight = options.outHeight;
        int scaleFactor = Math.max(1, Math.min(photoWidth / targetWidth, photoHeight / targetHeight));

        // 3. Decodificar imagen con escala
        options.inJustDecodeBounds = false;
        options.inSampleSize = scaleFactor;
        options.inPurgeable = true;

        try (InputStream input = context.getContentResolver().openInputStream(imageUri)) {
            return BitmapFactory.decodeStream(input, null, options);
        }
    }

    private void saveImageToStorage(Bitmap bitmap) {
        try {
            FileOutputStream fos = context.openFileOutput(IMAGE_FILENAME, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error al guardar la imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap loadImageFromStorage() {
        try {
            FileInputStream fis = context.openFileInput(IMAGE_FILENAME);
            return BitmapFactory.decodeStream(fis);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}