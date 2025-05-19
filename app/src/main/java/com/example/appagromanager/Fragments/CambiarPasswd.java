package com.example.appagromanager.Fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.appagromanager.databinding.FragmentCambiarPasswdBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CambiarPasswd extends Fragment {

    private FragmentCambiarPasswdBinding binding;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCambiarPasswdBinding.inflate(inflater, container, false);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.getEmail() != null) {
            binding.editEmail.setText(currentUser.getEmail());
            binding.editEmail.setEnabled(true);
        }

        binding.btnSendResetEmail.setOnClickListener(v -> enviarEmailDeRecuperacion());

        return binding.getRoot();
    }

    private void enviarEmailDeRecuperacion() {
        String email = binding.editEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getContext(), "Introduce tu correo electrónico", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Correo enviado. Revisa tu bandeja de entrada", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "Error al enviar el correo", Toast.LENGTH_SHORT).show();
            }
        });
    }
}