package com.example.appagromanager;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.appagromanager.models.Animal;
import com.example.appagromanager.models.Finca;

import java.util.List;

public class BottomViewModel extends ViewModel {

    private final AgroRepository agroRepository = new AgroRepository();
    private final MutableLiveData<List<Animal>> animales = new MutableLiveData<>();
    private final MutableLiveData<List<Finca>> fincas = new MutableLiveData<>();

    public BottomViewModel() {
        obtenerAnimalesPorGrupo("");
    }

    public LiveData<List<Animal>> getAnimales() {
        return animales;
    }
    public void obtenerAnimalesPorGrupo(String grupo) {
        agroRepository.getAnimales(grupo).observeForever(animales::postValue);
    }

    public LiveData<List<Finca>> getFincas() {
        return fincas;
    }
    public void obtenerFincas() {
        if (fincas.getValue() == null || fincas.getValue().isEmpty()) {
            agroRepository.getFinca().observeForever(fincas::postValue);
        }
    }
}
