package com.example.appagromanager.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.appagromanager.RetrofitClient;
import com.example.appagromanager.models.WeatherResponse;
import com.example.appagromanager.WeatherService;
import com.example.appagromanager.databinding.FragmentHomeBinding;

import retrofit2.Call; // ✅ ESTE es el bueno
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        getWeather("Madrid");

        return binding.getRoot();
    }

    private void getWeather(String city) {
        WeatherService service = RetrofitClient.getInstance().create(WeatherService.class);
        String apiKey = "TU_API_KEY"; // ← pon tu clave
        Call<WeatherResponse> call = service.getWeatherByCity(city, apiKey, "metric");

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful()) {
                    WeatherResponse weather = response.body();
                    if (weather != null && weather.main != null) {
                        binding.textView.setText("Ciudad: " + weather.name + "\nTemp: " + weather.main.temp + "°C");
                    } else {
                        binding.textView.setText("Respuesta vacía o mal formada");
                    }
                } else {
                    // Aquí muestras el código de error y posible mensaje del cuerpo
                    String errorBody = "";
                    try {
                        errorBody = response.errorBody().string();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    binding.textView.setText("Error en la respuesta: " + response.code() + "\n" + errorBody);
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                binding.textView.setText("Error de red: " + t.getMessage());
            }
        });
    }
}