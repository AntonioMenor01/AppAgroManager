package com.example.appagromanager;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.appagromanager.models.Animal;
import com.example.appagromanager.models.Finca;
import com.example.appagromanager.models.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.List;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AgroRepository {

    private final static String APIKEY="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZldnFmcWZhZWtmcHZjb21ubmhxIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDI0MDk1MDQsImV4cCI6MjA1Nzk4NTUwNH0.T33ffe9y6UYnljC_xMlwBnHchYsjcUgtDRaDz53C6h4";
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final OkHttpClient client = new OkHttpClient();

    public LiveData<Boolean> updateAnimal(String crotal, Animal animalActualizado) {
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();

        Gson gson = new Gson();
        String json = gson.toJson(animalActualizado);

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        String url = "https://fevqfqfaekfpvcomnnhq.supabase.co/rest/v1/Animal?crotal=eq." + crotal;

        Request request = new Request.Builder()
                .url(url)
                .patch(body)
                .addHeader("apikey", APIKEY)
                .addHeader("Authorization", "Bearer " + APIKEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=minimal")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("AgroRepository", "Error al actualizar el animal: " + e.getMessage());
                resultLiveData.postValue(false);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.isSuccessful()) {
                    Log.d("AgroRepository", "Animal actualizado correctamente");
                    resultLiveData.postValue(true);
                } else {
                    Log.e("AgroRepository", "Error al actualizar: " + response.code());
                    resultLiveData.postValue(false);
                }
            }
        });

        return resultLiveData;
    }



    public LiveData<Boolean> deleteAnimal(String crotal) {
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();

        String url = "https://fevqfqfaekfpvcomnnhq.supabase.co/rest/v1/Animal?crotal=eq." + crotal;

        Request request = new Request.Builder()
                .url(url)
                .delete()
                .addHeader("apikey", APIKEY)
                .addHeader("Authorization", "Bearer " + APIKEY)
                .addHeader("Prefer", "return=minimal")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("AgroRepository", "Error al eliminar el animal: " + e.getMessage());
                resultLiveData.postValue(false);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.isSuccessful()) {
                    Log.d("AgroRepository", "Animal eliminado correctamente");
                    resultLiveData.postValue(true);
                } else {
                    Log.e("AgroRepository", "Error en la respuesta al eliminar: " + response.code());
                    resultLiveData.postValue(false);
                }
            }
        });

        return resultLiveData;
    }

    public LiveData<List<Finca>> getFinca() {
        MutableLiveData<List<Finca>> fincasLiveData = new MutableLiveData<>();

        String url = "https://fevqfqfaekfpvcomnnhq.supabase.co/rest/v1/Finca";
        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", APIKEY)
                .addHeader("Authorization", "Bearer " + APIKEY)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                fincasLiveData.postValue(null);
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    Log.d("AgroRepository", "Respuesta de la API: " + responseBody);
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Finca>>() {}.getType();
                    List<Finca> fincas = gson.fromJson(responseBody, listType);
                    fincasLiveData.postValue(fincas);
                } else {
                    fincasLiveData.postValue(null);
                }
            }
        });

        return fincasLiveData;
    }

    public LiveData<List<Animal>> getAnimales(String grupoFiltro) {
        MutableLiveData<List<Animal>> animalesLiveData = new MutableLiveData<>();

        String url;
        if (grupoFiltro != null && !grupoFiltro.trim().isEmpty() && !grupoFiltro.equals("Selecciona un grupo ...")) {
            grupoFiltro = grupoFiltro.trim();
            grupoFiltro = grupoFiltro.substring(0, 1).toUpperCase() + grupoFiltro.substring(1).toLowerCase();
            url = "https://fevqfqfaekfpvcomnnhq.supabase.co/rest/v1/Animal?select=*&grupo=eq." + grupoFiltro;
        } else {
            url = "https://fevqfqfaekfpvcomnnhq.supabase.co/rest/v1/Animal?select=*"; // Sin filtro
            Log.w("AgroRepository", "grupoFiltro vacío o nulo. Se consultan todos los animales.");
        }

        Log.d("AgroRepository", "URL final: " + url);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", APIKEY)
                .addHeader("Authorization", "Bearer " + APIKEY)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                Log.e("AgroRepository", "Error al obtener los animales: " + e.getMessage());
                animalesLiveData.postValue(null);
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    Log.d("AgroRepository", "Respuesta de la API: " + responseBody);
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Animal>>() {}.getType();
                    List<Animal> animales = gson.fromJson(responseBody, listType);
                    animalesLiveData.postValue(animales);
                } else {
                    Log.e("AgroRepository", "Error al obtener los animales, código de respuesta: " + response.code());
                    animalesLiveData.postValue(null);
                }
            }
        });

        return animalesLiveData;
    }

    public interface AuthCallback {
        void onSuccess();
        void onError(String message);
    }

    public void login(String email, String password, AuthCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onError("Error al iniciar sesión: " + task.getException().getMessage());
                    }
                });
    }

    public void register(String email, String password, Usuario usuario, AuthCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (currentUser != null) {
                            sendUserDataToSupabase(usuario, callback);
                        } else {
                            callback.onError("Usuario creado pero no se pudo obtener información.");
                        }
                    } else {
                        callback.onError("Error al registrar: " + task.getException().getMessage());
                    }
                });
    }

    public boolean isUserLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }

    private void sendUserDataToSupabase(Usuario usuario, AuthCallback callback) {
        Gson gson = new Gson();
        String json = gson.toJson(usuario);

        RequestBody body = RequestBody.create(
                json, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url("https://fevqfqfaekfpvcomnnhq.supabase.co/rest/v1/Usuario")
                .post(body)
                .addHeader("apikey", APIKEY)
                .addHeader("Authorization", "Bearer "+APIKEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onError("Error al conectar con Supabase: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    String responseBody = response.body() != null ? response.body().string() : "Sin cuerpo";
                    Log.d("SUPABASE_ERROR", "Código: " + response.code() + " | Cuerpo: " + responseBody);
                    callback.onError("Supabase error: Código " + response.code());
                }
            }
        });
    }
}