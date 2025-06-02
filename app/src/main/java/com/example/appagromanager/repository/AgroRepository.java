package com.example.appagromanager.repository;

import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.appagromanager.models.Animal;
import com.example.appagromanager.models.ConfiguracionConsumo;
import com.example.appagromanager.models.Finca;
import com.example.appagromanager.models.Insumo;
import com.example.appagromanager.models.Pienso;
import com.example.appagromanager.models.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Map;

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

    public LiveData<List<Animal>> getAnimalesPorFinca(String fincaId) {
        MutableLiveData<List<Animal>> liveData = new MutableLiveData<>();

        String url = "https://fevqfqfaekfpvcomnnhq.supabase.co/rest/v1/Animal?fincaId=eq." + fincaId;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", APIKEY)
                .addHeader("Authorization", "Bearer " + APIKEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("AgroRepository", "Error al obtener animales por finca: " + e.getMessage());
                liveData.postValue(null);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String body = response.body().string();
                    List<Animal> list = new Gson().fromJson(body, new TypeToken<List<Animal>>(){}.getType());
                    liveData.postValue(list);
                } else {
                    liveData.postValue(null);
                }
            }
        });

        return liveData;
    }

    public void actualizarCantidadInsumo(String insumoId, double nuevaCantidad) {
        String url = "https://fevqfqfaekfpvcomnnhq.supabase.co/rest/v1/Insumo?id=eq." + insumoId;

        JsonObject json = new JsonObject();
        json.addProperty("cantidad", nuevaCantidad);

        RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(url)
                .patch(body)
                .addHeader("apikey", APIKEY)
                .addHeader("Authorization", "Bearer " + APIKEY)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("AgroRepository", "Error actualizando cantidad de insumo: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("AgroRepository", "Cantidad de insumo actualizada correctamente");
                } else {
                    Log.e("AgroRepository", "Error en respuesta al actualizar cantidad. Código: " + response.code());
                }
            }
        });
    }

    public LiveData<List<Animal>> getAnimales() {
        MutableLiveData<List<Animal>> liveData = new MutableLiveData<>();

        String url = "https://fevqfqfaekfpvcomnnhq.supabase.co/rest/v1/Animal?select=*";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", APIKEY)
                .addHeader("Authorization", "Bearer " + APIKEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("AgroRepository", "Error al obtener animales: " + e.getMessage());
                liveData.postValue(null);
            }

            @Override public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String body = response.body().string();
                    List<Animal> list = new Gson().fromJson(body, new TypeToken<List<Animal>>(){}.getType());
                    liveData.postValue(list);
                } else {
                    liveData.postValue(null);
                }
            }
        });

        return liveData;
    }


    public void insertarUsoInsumo(String insumoId, String animalId, String fecha, String fincaId, double cantidadUsada) {
        String url = "https://fevqfqfaekfpvcomnnhq.supabase.co/rest/v1/RegistroUsoInsumo";

        JsonObject json = new JsonObject();
        json.addProperty("insumoId", insumoId);
        json.addProperty("animalid", animalId);
        json.addProperty("fecha", fecha);
        json.addProperty("fincaId", fincaId);
        json.addProperty("cantidadUsada", cantidadUsada);

        RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("apikey", APIKEY)
                .addHeader("Authorization", "Bearer " + APIKEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("AgroRepository", "Error al registrar uso de insumo: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("AgroRepository", "Uso de insumo registrado con éxito");
                } else {
                    Log.e("AgroRepository", "Fallo al registrar uso de insumo. Código: " + response.code());
                    Log.e("AgroRepository", "Respuesta: " + (response.body() != null ? response.body().string() : "sin cuerpo"));
                }
            }
        });
    }


    public LiveData<List<Insumo>> getInsumos() {
        MutableLiveData<List<Insumo>> liveData = new MutableLiveData<>();

        String url = "https://fevqfqfaekfpvcomnnhq.supabase.co/rest/v1/Insumo?select=*";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", APIKEY)
                .addHeader("Authorization", "Bearer " + APIKEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("AgroRepository", "Error al obtener insumos: " + e.getMessage());
                liveData.postValue(null);
            }

            @Override public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String body = response.body().string();
                    List<Insumo> list = new Gson().fromJson(body, new TypeToken<List<Insumo>>() {}.getType());
                    liveData.postValue(list);
                } else {
                    liveData.postValue(null);
                }
            }
        });

        return liveData;
    }

    public LiveData<Boolean> eliminarFinca(String fincaId) {
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();

        Request request = new Request.Builder()
                .url("https://fevqfqfaekfpvcomnnhq.supabase.co/rest/v1/Finca?id=eq." + fincaId)
                .delete()
                .addHeader("apikey", APIKEY)
                .addHeader("Authorization", "Bearer " + APIKEY)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("AgroRepository", "Error al eliminar la finca: " + e.getMessage());
                resultLiveData.postValue(false);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.isSuccessful()) {
                    Log.d("AgroRepository", "Finca eliminada correctamente");
                    resultLiveData.postValue(true);
                } else {
                    try {
                        Log.e("AgroRepository", "Error al eliminar la finca: " + response.code() + " - " + response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    resultLiveData.postValue(false);
                }
            }
        });

        return resultLiveData;
    }

    public LiveData<Boolean> insertarFinca(Finca nuevaFinca) {
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String json = gson.toJson(nuevaFinca);

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url("https://fevqfqfaekfpvcomnnhq.supabase.co/rest/v1/Finca")
                .post(body)
                .addHeader("apikey", APIKEY)
                .addHeader("Authorization", "Bearer " + APIKEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("AgroRepository", "Error al crear la finca: " + e.getMessage());
                resultLiveData.postValue(false);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.isSuccessful()) {
                    Log.d("AgroRepository", "Finca creada correctamente");
                    resultLiveData.postValue(true);
                } else {
                    try {
                        Log.e("AgroRepository", "Error al crear la finca: " + response.code() + " - " + response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    resultLiveData.postValue(false);
                }
            }
        });

        return resultLiveData;
    }

    private MutableLiveData<ConfiguracionConsumo> configuracionConsumoLiveData = new MutableLiveData<>();
    public LiveData<ConfiguracionConsumo> getConfiguracionConsumoLiveData() {
        return configuracionConsumoLiveData;
    }
    public void cargarConfiguracionConsumoPorGrupo(String grupo) {
        String grupoEnum = mapGrupoUsuarioToEnum(grupo);
        String url = "https://fevqfqfaekfpvcomnnhq.supabase.co/rest/v1/ConfiguracionConsumo?grupoAnimal=eq." + grupoEnum;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", APIKEY)
                .addHeader("Authorization", "Bearer " + APIKEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("AgroRepository", "Error config consumo: " + e.getMessage());
                configuracionConsumoLiveData.postValue(null);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String body = response.body().string();
                    Log.d("AgroRepository", "JSON ConfiguracionConsumo: " + body);
                    List<ConfiguracionConsumo> list = new Gson().fromJson(body, new TypeToken<List<ConfiguracionConsumo>>(){}.getType());
                    if (list != null && !list.isEmpty()) {
                        configuracionConsumoLiveData.postValue(list.get(0));
                    } else {
                        configuracionConsumoLiveData.postValue(null);
                    }
                } else {
                    configuracionConsumoLiveData.postValue(null);
                }
            }
        });
    }

    public LiveData<List<Pienso>> getPiensos() {
        MutableLiveData<List<Pienso>> liveData = new MutableLiveData<>();

        String url = "https://fevqfqfaekfpvcomnnhq.supabase.co/rest/v1/Pienso?select=*";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", APIKEY)
                .addHeader("Authorization", "Bearer " + APIKEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("AgroRepository", "Error al obtener piensos: " + e.getMessage());
                liveData.postValue(null);
            }

            @Override public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String body = response.body().string();
                    List<Pienso> list = new Gson().fromJson(body, new TypeToken<List<Pienso>>(){}.getType());
                    liveData.postValue(list);
                } else {
                    liveData.postValue(null);
                }
            }
        });

        return liveData;
    }


    public LiveData<Boolean> registrarConsumo(String grupo, double cantidadConsumidaKg, String piensoId) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();

        String url = "https://fevqfqfaekfpvcomnnhq.supabase.co/rest/v1/RegistroConsumoPienso";

        Map<String, Object> registro = new HashMap<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registro.put("fecha", LocalDate.now().toString());
        }
        registro.put("grupo", mapGrupoUsuarioToEnum(grupo));
        registro.put("cantidadConsumidaKg", cantidadConsumidaKg);
        registro.put("piensoId", piensoId);

        Gson gson = new Gson();
        String json = gson.toJson(registro);

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("apikey", APIKEY)
                .addHeader("Authorization", "Bearer " + APIKEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("AgroRepository", "Error insertando consumo: " + e.getMessage());
                liveData.postValue(false);
            }

            @Override public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                liveData.postValue(response.isSuccessful());
            }
        });

        return liveData;
    }


    public LiveData<Boolean> actualizarCantidadPienso(String piensoId, double nuevaCantidadKg) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();

        String url = "https://fevqfqfaekfpvcomnnhq.supabase.co/rest/v1/Pienso?id=eq." + piensoId;

        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("cantidadActualKg", nuevaCantidadKg);

        Gson gson = new Gson();
        String json = gson.toJson(updateMap);

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(url)
                .patch(body)
                .addHeader("apikey", APIKEY)
                .addHeader("Authorization", "Bearer " + APIKEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("AgroRepository", "Error actualizando pienso: " + e.getMessage());
                liveData.postValue(false);
            }

            @Override public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                liveData.postValue(response.isSuccessful());
            }
        });

        return liveData;
    }

    public LiveData<Animal> getAnimalMasPesadoPorGrupo(String grupoUsuario) {
        MutableLiveData<Animal> animalPesadoLiveData = new MutableLiveData<>();
        String grupoEnum = mapGrupoUsuarioToEnum(grupoUsuario);

        String url = "https://fevqfqfaekfpvcomnnhq.supabase.co/rest/v1/Animal?select=*&grupo=eq." + grupoEnum;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", APIKEY)
                .addHeader("Authorization", "Bearer " + APIKEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("AgroRepository", "Error al obtener animales: " + e.getMessage());
                animalPesadoLiveData.postValue(null);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    List<Animal> animales = new Gson().fromJson(responseBody, new TypeToken<List<Animal>>() {}.getType());

                    if (animales != null && !animales.isEmpty()) {
                        Animal masPesado = animales.get(0);
                        for (Animal a : animales) {
                            if (a.getPeso() != 0 && masPesado.getPeso() != 0 && a.getPeso() > masPesado.getPeso()) {
                                masPesado = a;
                            }
                        }
                        animalPesadoLiveData.postValue(masPesado);
                    } else {
                        animalPesadoLiveData.postValue(null);
                    }
                } else {
                    Log.e("AgroRepository", "Error al obtener animales: " + response.code());
                    animalPesadoLiveData.postValue(null);
                }
            }
        });

        return animalPesadoLiveData;
    }

    public LiveData<Animal> getAnimalMasViejoPorGrupo(String grupoUsuario) {
        MutableLiveData<Animal> animalViejoLiveData = new MutableLiveData<>();
        String grupoEnum = mapGrupoUsuarioToEnum(grupoUsuario);

        String url = "https://fevqfqfaekfpvcomnnhq.supabase.co/rest/v1/Animal?select=*&grupo=eq." + grupoEnum;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", APIKEY)
                .addHeader("Authorization", "Bearer " + APIKEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("AgroRepository", "Error al obtener animales: " + e.getMessage());
                animalViejoLiveData.postValue(null);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    List<Animal> animales = new Gson().fromJson(responseBody, new TypeToken<List<Animal>>() {}.getType());

                    if (animales != null && !animales.isEmpty()) {
                        Animal masViejo = animales.get(0);
                        for (Animal a : animales) {
                            if (a.getFechaNacimiento() != null && masViejo.getFechaNacimiento() != null &&
                                    a.getFechaNacimiento().compareTo(masViejo.getFechaNacimiento()) < 0) {
                                masViejo = a;
                            }
                        }
                        animalViejoLiveData.postValue(masViejo);
                    } else {
                        animalViejoLiveData.postValue(null);
                    }
                } else {
                    Log.e("AgroRepository", "Error al obtener animales: " + response.code());
                    animalViejoLiveData.postValue(null);
                }
            }
        });

        return animalViejoLiveData;
    }

    public LiveData<Integer> getCantidadAnimalesPorGrupo(String grupo) {
        MutableLiveData<Integer> cantidadLiveData = new MutableLiveData<>();
        String grupoEnum = mapGrupoUsuarioToEnum(grupo);
        String url = "https://fevqfqfaekfpvcomnnhq.supabase.co/rest/v1/Animal?grupo=eq." + grupoEnum + "&select=*";

        Log.d("AgroRepository", "URL final para contar: " + url);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", APIKEY)
                .addHeader("Authorization", "Bearer " + APIKEY)
                .addHeader("Prefer", "count=exact")
                .addHeader("Range", "0-0")
                .build();

        Log.d("AgroRepository", "Headers: Prefer=count=exact, Range=0-0, Authorization y apikey incluidos.");

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("AgroRepository", "Error al contar animales: " + e.getMessage());
                cantidadLiveData.postValue(null);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                Log.d("AgroRepository", "Respuesta código: " + response.code());
                if (response.isSuccessful()) {
                    String contentRange = response.header("Content-Range");
                    Log.d("AgroRepository", "Content-Range: " + contentRange);
                    if (contentRange != null && contentRange.contains("/")) {
                        try {
                            String totalStr = contentRange.split("/")[1];
                            int total = Integer.parseInt(totalStr.trim());
                            cantidadLiveData.postValue(total);
                        } catch (Exception e) {
                            Log.e("AgroRepository", "Error parseando conteo: " + e.getMessage());
                            cantidadLiveData.postValue(null);
                        }
                    } else {
                        Log.w("AgroRepository", "Content-Range ausente o mal formado");
                        cantidadLiveData.postValue(null);
                    }
                } else {
                    try {
                        String errorBody = response.body() != null ? response.body().string() : "No body";
                        Log.e("AgroRepository", "Error respuesta contando: código " + response.code() + " / cuerpo: " + errorBody);
                    } catch (Exception ex) {
                        Log.e("AgroRepository", "Error leyendo cuerpo de respuesta: " + ex.getMessage());
                    }
                    cantidadLiveData.postValue(null);
                }
            }
        });

        return cantidadLiveData;
    }

    private String mapGrupoUsuarioToEnum(String grupoUsuario) {
        switch (grupoUsuario.toLowerCase()) {
            case "vacas":
                return "Vacuno";
            case "cerdos":
                return "Porcino";
            case "ovejas":
                return "Ovino";
            default:
                return grupoUsuario;
        }
    }

    public LiveData<Usuario> obtenerDatosUsuario() {
        MutableLiveData<Usuario> usuarioLiveData = new MutableLiveData<>();
        String email = mAuth.getCurrentUser().getEmail();
        String url = "https://fevqfqfaekfpvcomnnhq.supabase.co/rest/v1/Usuario?email=eq." + email;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", APIKEY)
                .addHeader("Authorization", "Bearer " + APIKEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("AgroRepository", "Error al obtener usuario: " + e.getMessage());
                usuarioLiveData.postValue(null);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    Log.d("AgroRepository", "Usuario encontrado: " + responseBody);

                    List<Usuario> usuarios = new Gson().fromJson(responseBody, new TypeToken<List<Usuario>>() {}.getType());

                    if (!usuarios.isEmpty()) {
                        usuarioLiveData.postValue(usuarios.get(0));
                    } else {
                        usuarioLiveData.postValue(null);
                    }
                } else {
                    Log.e("AgroRepository", "Error en la respuesta: " + response.code());
                    usuarioLiveData.postValue(null);
                }
            }
        });

        return usuarioLiveData;
    }
    public LiveData<Boolean> addAnimal(Animal nuevoAnimal) {
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();

        Gson gson = new Gson();
        String json = gson.toJson(nuevoAnimal);

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url("https://fevqfqfaekfpvcomnnhq.supabase.co/rest/v1/Animal")
                .post(body)
                .addHeader("apikey", APIKEY)
                .addHeader("Authorization", "Bearer " + APIKEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("AgroRepository", "Error al crear el animal: " + e.getMessage());
                resultLiveData.postValue(false);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.isSuccessful()) {
                    Log.d("AgroRepository", "Animal creado correctamente");
                    resultLiveData.postValue(true);
                } else {
                    try {
                        Log.e("AgroRepository", "Error al crear el animal: " + response.code() + " - " + response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    resultLiveData.postValue(false);
                }
            }
        });

        return resultLiveData;
    }

    public LiveData<Boolean> updateAnimal(String animalId, Animal animalActualizado) {
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();

        Gson gson = new Gson();
        String json = gson.toJson(animalActualizado);

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        String url = "https://fevqfqfaekfpvcomnnhq.supabase.co/rest/v1/Animal?id=eq." + animalId;

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

    public LiveData<Boolean> deleteAnimal(String animalId) {
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();

        String url = "https://fevqfqfaekfpvcomnnhq.supabase.co/rest/v1/Animal?id=eq." + animalId;

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

    public LiveData<Boolean> isCrotalEnUso(String crotal) {
        MutableLiveData<Boolean> enUsoLiveData = new MutableLiveData<>();

        String url = "https://fevqfqfaekfpvcomnnhq.supabase.co/rest/v1/Animal?crotal=eq." + crotal;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", APIKEY)
                .addHeader("Authorization", "Bearer " + APIKEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("AgroRepository", "Error verificando crotal: " + e.getMessage());
                enUsoLiveData.postValue(false);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    enUsoLiveData.postValue(!responseBody.equals("[]"));
                } else {
                    enUsoLiveData.postValue(false);
                }
            }
        });

        return enUsoLiveData;
    }

    public LiveData<List<Animal>> getAnimalesPorPesoYEdad(Double pesoMin, Double pesoMax, Integer edadMin, Integer edadMax) {
        MutableLiveData<List<Animal>> animalesLiveData = new MutableLiveData<>();

        StringBuilder urlBuilder = new StringBuilder("https://fevqfqfaekfpvcomnnhq.supabase.co/rest/v1/Animal?select=*");

        if (pesoMin != null) urlBuilder.append("&peso=gte.").append(pesoMin);
        if (pesoMax != null) urlBuilder.append("&peso=lte.").append(pesoMax);

        LocalDate fechaActual = null;
        if (edadMin != null || edadMax != null) {
            LocalDate fechaInicio = null;
            LocalDate fechaFin = null;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                fechaActual = LocalDate.now();
                if (edadMax != null) fechaFin = fechaActual.minusYears(edadMax);
                if (edadMin != null) fechaInicio = fechaActual.minusYears(edadMin);
            }

            if (fechaInicio != null) urlBuilder.append("&fechaNacimiento=lte.").append(fechaInicio);
            if (fechaFin != null) urlBuilder.append("&fechaNacimiento=gte.").append(fechaFin);
        }

        String url = urlBuilder.toString();

        Log.d("AgroRepository", "URL filtro peso+edad: " + url);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", APIKEY)
                .addHeader("Authorization", "Bearer " + APIKEY)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                animalesLiveData.postValue(null);
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    List<Animal> animales = new Gson().fromJson(responseBody, new TypeToken<List<Animal>>() {}.getType());
                    animalesLiveData.postValue(animales);
                } else {
                    animalesLiveData.postValue(null);
                }
            }
        });

        return animalesLiveData;
    }

    public LiveData<List<Animal>> getAnimales(String grupoFiltro) {
        MutableLiveData<List<Animal>> animalesLiveData = new MutableLiveData<>();

        String url;
        if (grupoFiltro != null && !grupoFiltro.trim().isEmpty() && !grupoFiltro.equals("Selecciona un grupo ...")) {
            grupoFiltro = grupoFiltro.trim();
            grupoFiltro = grupoFiltro.substring(0, 1).toUpperCase() + grupoFiltro.substring(1).toLowerCase();
            url = "https://fevqfqfaekfpvcomnnhq.supabase.co/rest/v1/Animal?select=*&grupo=eq." + grupoFiltro;
        } else {
            url = "https://fevqfqfaekfpvcomnnhq.supabase.co/rest/v1/Animal?select=*";
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