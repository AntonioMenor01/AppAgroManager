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
    private final MutableLiveData<Boolean> eliminado = new MutableLiveData<>();
    private final MutableLiveData<Boolean> actualizado = new MutableLiveData<>();

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

    public LiveData<Boolean> getEliminado() {
        return eliminado;
    }
    public void eliminarAnimal(String animalId) {
        agroRepository.deleteAnimal(animalId).observeForever(eliminado::postValue);
    }

    public LiveData<Boolean> getActualizado() {
        return actualizado;
    }
    public void actualizarAnimal(String animalId, Animal animalActualizado) {
        agroRepository.updateAnimal(animalId, animalActualizado).observeForever(result -> actualizado.setValue(result));
    }

    private final MutableLiveData<Boolean> creado = new MutableLiveData<>();

    public LiveData<Boolean> getCreado() {
        return creado;
    }
    public void resetCreado() {
        creado.setValue(false);
    }
    public void crearAnimal(Animal nuevoAnimal) {
        agroRepository.addAnimal(nuevoAnimal).observeForever(creado::postValue);
    }


}
