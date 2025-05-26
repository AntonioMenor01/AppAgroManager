package com.example.appagromanager.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.appagromanager.viewmodel.AuthViewModel;
import com.example.appagromanager.activity.MainActivity;
import com.example.appagromanager.R;
import com.example.appagromanager.databinding.FragmentSignInBinding;
import com.example.appagromanager.models.Usuario;

public class RegisterFragment extends Fragment {

    private FragmentSignInBinding binding;
    private AuthViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignInBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        viewModel.getAuthSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(getContext(), "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(requireActivity(), MainActivity.class));
                requireActivity().finish();
            }
        });

        viewModel.getAuthError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_LONG).show();
                binding.registerButton.setEnabled(true);
                binding.registerButton.setText("Registrar");
            }
        });

        binding.loginlink.setOnClickListener(v -> {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_sign_in_to_login);
        });

        binding.registerButton.setOnClickListener(v -> {
            String nombre = binding.nameEditText.getText().toString().trim();
            String apellidos = binding.surnameEditText.getText().toString().trim();
            String email = binding.emailEditText.getText().toString().trim();
            String password = binding.passwordEditText.getText().toString().trim();

            if (nombre.isEmpty() || apellidos.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show();
                return;
            }

            binding.registerButton.setEnabled(false);
            binding.registerButton.setText("Registrando...");

            Usuario nuevoUsuario = new Usuario(nombre, apellidos, email);
            viewModel.register(email, password, nuevoUsuario);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}