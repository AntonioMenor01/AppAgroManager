package com.example.appagromanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.example.appagromanager.databinding.FragmentSignInBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class RegisterFragment extends Fragment {

    private FragmentSignInBinding binding;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignInBinding.inflate(inflater, container, false);

        binding.loginlink.setOnClickListener(v -> {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_sign_in_to_login);
        });

        binding.registerButton.setOnClickListener(v -> {
            registerUser();
        });

        return binding.getRoot();
    }

    private void registerUser() {
        String name = binding.nameEditText.getText().toString().trim();
        String surname = binding.surnameEditText.getText().toString().trim();
        String email = binding.emailEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString().trim();

        if (name.isEmpty() || surname.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Guardar en Supabase
                            sendUserDataToSupabase(name, surname, email);
                        }
                    } else {
                        Toast.makeText(getContext(), "Error al registrar: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendUserDataToSupabase(String name, String surname, String email) {
        OkHttpClient client = new OkHttpClient();

        String json = "{"
                + "\"nombre\":\"" + name + "\","
                + "\"apellidos\":\"" + surname + "\","
                + "\"email\":\"" + email + "\""
                + "}";

        RequestBody body = RequestBody.create(
                json, okhttp3.MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url("https://fevqfqfaekfpvcomnnhq.supabase.co/rest/v1/Usuario")
                .post(body)
                .addHeader("apikey", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZldnFmcWZhZWtmcHZjb21ubmhxIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDI0MDk1MDQsImV4cCI6MjA1Nzk4NTUwNH0.T33ffe9y6UYnljC_xMlwBnHchYsjcUgtDRaDz53C6h4")
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZldnFmcWZhZWtmcHZjb21ubmhxIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDI0MDk1MDQsImV4cCI6MjA1Nzk4NTUwNH0.T33ffe9y6UYnljC_xMlwBnHchYsjcUgtDRaDz53C6h4")
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation")
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Error al enviar datos a Supabase", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                String errorBody;
                try {
                    errorBody = response.body() != null ? response.body().string() : "Sin cuerpo de error";
                } catch (IOException e) {
                    errorBody = "Error leyendo el cuerpo: " + e.getMessage();
                }

                String finalErrorBody = errorBody; // Variable final para usar en la UI thread
                requireActivity().runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(requireActivity(), MainActivity.class);
                        startActivity(intent);
                        requireActivity().finish();
                    } else {
                        Log.e("SUPABASE_ERROR", "Código: " + response.code() + "\nCuerpo: " + finalErrorBody);

                        Toast.makeText(getContext(),
                                "Error Supabase: " + response.code() + " (ver Logcat para más info)",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}