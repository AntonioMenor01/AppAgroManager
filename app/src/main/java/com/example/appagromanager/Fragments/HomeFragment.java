package com.example.appagromanager.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.appagromanager.R;
import com.example.appagromanager.api.RetrofitClient;
import com.example.appagromanager.api.WeatherResponse;
import com.example.appagromanager.api.WeatherService;
import com.example.appagromanager.databinding.FragmentHomeBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private static final String API_KEY = "a8d13e1ad64f4e64a52194257251905";
    private static final String PREFS_NAME = "weather_prefs";
    private static final String KEY_LAST_CITY = "last_city";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String lastCity = prefs.getString(KEY_LAST_CITY, "Cáceres");

        binding.etCitySearch.setText(lastCity);
        getForecast(lastCity);

        binding.btnSearch.setOnClickListener(v -> {
            String city = binding.etCitySearch.getText().toString().trim();
            if (!TextUtils.isEmpty(city)) {
                prefs.edit().putString(KEY_LAST_CITY, city).apply();

                getForecast(city);
                binding.etCitySearch.onEditorAction(EditorInfo.IME_ACTION_DONE);
            } else {
                Toast.makeText(getContext(), "Introduce una ciudad válida", Toast.LENGTH_SHORT).show();
            }
        });

        binding.imageOvejas.setOnClickListener(v -> openAnimalDetail("Ovejas"));
        binding.imageVacas.setOnClickListener(v -> openAnimalDetail("Vacas"));
        binding.imageCerdos.setOnClickListener(v -> openAnimalDetail("Cerdos"));

        return binding.getRoot();
    }

    private void getForecast(String city) {
        WeatherService service = RetrofitClient.getInstance().create(WeatherService.class);

        Call<WeatherResponse> call = service.getForecastByCity(API_KEY, city, 1, "no", "no");

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful()) {
                    WeatherResponse weather = response.body();
                    if (weather != null &&
                            weather.location != null &&
                            weather.forecast != null &&
                            !weather.forecast.forecastday.isEmpty()) {

                        WeatherResponse.Day day = weather.forecast.forecastday.get(0).day;

                        binding.tvCity.setText(weather.location.name);
                        binding.tvTempActual.setText(weather.current.temp_c + "°C");
                        binding.tvTempMax.setText(day.maxtemp_c + "°C");
                        binding.tvTempMin.setText(day.mintemp_c + "°C");
                        binding.tvRainChance.setText(day.daily_chance_of_rain + "%");

                    } else {
                        binding.tvCity.setText("Datos incompletos.");
                    }

                } else {
                    String errorMessage;
                    switch (response.code()) {
                        case 400:
                        case 404:
                            errorMessage = "Ciudad no encontrada";
                            break;
                        case 401:
                            errorMessage = "API Key inválida";
                            break;
                        case 500:
                            errorMessage = "Error del servidor";
                            break;
                        default:
                            errorMessage = "Error: " + response.code();
                            break;
                    }
                    binding.tvCity.setText(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                binding.tvCity.setText("Error de red: " + t.getMessage());
            }
        });
    }
    private void openAnimalDetail(String animalName) {
        Bundle bundle = new Bundle();
        bundle.putString("animal_name", animalName);

        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_homeFragment_to_homeDetallesFragment, bundle);
    }

}