package com.example.appagromanager.viewmodel;

import android.os.Build;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import com.example.appagromanager.models.Animal;
import com.example.appagromanager.models.ConfiguracionConsumo;
import com.example.appagromanager.models.Finca;
import com.example.appagromanager.models.Insumo;
import com.example.appagromanager.models.Pienso;
import com.example.appagromanager.repository.AgroRepository;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BottomViewModel extends ViewModel {

    private final AgroRepository agroRepository = new AgroRepository();
    private final MutableLiveData<List<Finca>> fincas = new MutableLiveData<>();
    private final MutableLiveData<Boolean> eliminado = new MutableLiveData<>();
    private final MutableLiveData<Boolean> actualizado = new MutableLiveData<>();
    private final MutableLiveData<ConfiguracionConsumo> configuracionConsumo = new MutableLiveData<>();
    private final MutableLiveData<List<Pienso>> piensos = new MutableLiveData<>();
    private final MutableLiveData<Boolean> consumoRegistrado = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cantidadPiensoActualizada = new MutableLiveData<>();
    private final MutableLiveData<Boolean> eliminarResult = new MutableLiveData<>();
    private final MutableLiveData<List<Insumo>> insumos = new MutableLiveData<>();
    private final MutableLiveData<List<Finca>> fincasConAnimales = new MutableLiveData<>();
    private final MutableLiveData<List<Animal>> animales = new MutableLiveData<>();
    private MutableLiveData<List<Animal>> animal = new MutableLiveData<>();

    public LiveData<List<Animal>> getAnimalesPorFinca(String fincaId) {
        return agroRepository.getAnimalesPorFinca(fincaId);
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

    public void refrescarFincas() {
        agroRepository.getFinca().observeForever(fincas::postValue);
    }


    public LiveData<Boolean> getEliminado() {
        return eliminado;
    }
    public void resetEliminado() {
        eliminado.setValue(null);
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
        LiveData<Boolean> resultado = agroRepository.addAnimal(nuevoAnimal);
        resultado.observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean success) {
                creado.postValue(success);
                resultado.removeObserver(this);
            }
        });
    }

    public LiveData<Boolean> verificarCrotal(String crotal) {
        return agroRepository.isCrotalEnUso(crotal);
    }

    public void filtrarAnimales(String crotal, String grupo, Double pesoMin, Double pesoMax, Integer edadMin, Integer edadMax) {
        agroRepository.getAnimalesPorPesoYEdad(pesoMin, pesoMax, edadMin, edadMax)
                .observeForever(animales -> {
                    if (animales != null) {
                        List<Animal> filtrados = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                            filtrados = animales.stream()
                                    .filter(a -> (crotal == null || crotal.isEmpty() || a.getCrotal().toLowerCase().contains(crotal.toLowerCase())) &&
                                            (grupo == null || grupo.isEmpty() || grupo.equals("Selecciona un grupo ...") || grupo.equals(a.getGrupo())))
                                    .toList();
                        }
                        this.animales.postValue(filtrados);
                    } else {
                        this.animales.postValue(null);
                    }
                });
    }
    public LiveData<Integer> getCantidadAnimalesPorGrupo(String grupo) {
        return agroRepository.getCantidadAnimalesPorGrupo(grupo);
    }
    public LiveData<Animal> getAnimalMasViejoPorGrupo(String grupo) {
        return agroRepository.getAnimalMasViejoPorGrupo(grupo);
    }
    public LiveData<Animal> getAnimalMasPesadoPorGrupo(String grupo) {
        return agroRepository.getAnimalMasPesadoPorGrupo(grupo);
    }

    public LiveData<List<Pienso>> getPiensosLiveData() {
        return piensos;
    }

    public LiveData<Boolean> getConsumoRegistrado() {
        return consumoRegistrado;
    }
    public LiveData<Boolean> getCantidadPiensoActualizada() {
        return cantidadPiensoActualizada;
    }

    public BottomViewModel() {
        agroRepository.getConfiguracionConsumoLiveData().observeForever(config -> {
            configuracionConsumo.postValue(config);
            Log.d("BottomViewModel", "Configuración obtenida: " + config);
        });
    }

    public LiveData<ConfiguracionConsumo> getConfiguracionConsumo() {
        return configuracionConsumo;
    }

    public void cargarConfiguracionConsumoPorGrupo(String grupo) {
        if (grupo == null || grupo.equals("Selecciona un grupo ...")) {
            configuracionConsumo.postValue(null);
            return;
        }
        agroRepository.cargarConfiguracionConsumoPorGrupo(grupo);
    }

    public void cargarPiensos() {
        agroRepository.getPiensos().observeForever(listaPiensos -> {
            piensos.postValue(listaPiensos);
        });
    }

    public void registrarConsumo(String grupo, double cantidadConsumidaKg, String piensoId) {
        agroRepository.registrarConsumo(grupo, cantidadConsumidaKg, piensoId).observeForever(success -> {
            consumoRegistrado.postValue(success);
        });
    }
    public void actualizarCantidadPienso(String piensoId, double nuevaCantidadKg) {
        agroRepository.actualizarCantidadPienso(piensoId, nuevaCantidadKg).observeForever(success -> {
            cantidadPiensoActualizada.postValue(success);
            if (success) {
                cargarPiensos();
            }
        });
    }
    public LiveData<List<Finca>> getFincasConAnimales() {
        return fincasConAnimales;
    }

    public void obtenerFincasConAnimales() {
        agroRepository.getFinca().observeForever(fincaList -> {
            if (fincaList != null) {
                agroRepository.getAnimales(null).observeForever(animalesList -> {
                    if (animalesList != null) {
                        Map<String, Integer> contador = new HashMap<>();
                        for (Animal animal : animalesList) {
                            String fincaId = animal.getFincaId();
                            contador.put(fincaId, contador.getOrDefault(fincaId, 0) + 1);
                        }

                        for (Finca finca : fincaList) {
                            finca.setAnimalesActuales(contador.getOrDefault(finca.getId(), 0));
                        }

                        Collections.sort(fincaList, Comparator.comparing(Finca::getId));
                        fincasConAnimales.postValue(fincaList);

                    } else {
                        for (Finca finca : fincaList) {
                            finca.setAnimalesActuales(0);
                        }
                        Collections.sort(fincaList, Comparator.comparing(Finca::getId));
                        fincasConAnimales.postValue(fincaList);
                    }
                });
            }
        });
    }

    public LiveData<Finca> getFincaById(String id) {
        MutableLiveData<Finca> fincaLiveData = new MutableLiveData<>();

        List<Finca> currentFincas = fincasConAnimales.getValue();
        if (currentFincas != null) {
            for (Finca f : currentFincas) {
                if (f.getId().equals(id)) {
                    fincaLiveData.postValue(f);
                    return fincaLiveData;
                }
            }
            fincaLiveData.postValue(null);
        } else {
            obtenerFincasConAnimales();
            getFincasConAnimales().observeForever(fincas -> {
                if (fincas != null) {
                    for (Finca f : fincas) {
                        if (f.getId().equals(id)) {
                            fincaLiveData.postValue(f);
                            break;
                        }
                    }
                }
            });
        }
        return fincaLiveData;
    }

    public LiveData<Boolean> insertarFinca(Finca nuevaFinca) {
        MutableLiveData<Boolean> resultado = new MutableLiveData<>();

        agroRepository.insertarFinca(nuevaFinca).observeForever(success -> {
            resultado.postValue(success);

            if (success) {
                agroRepository.getFinca().observeForever(nuevasFincas -> {
                    fincas.postValue(nuevasFincas);
                });
            }
        });

        return resultado;
    }

    public LiveData<Boolean> getEliminarResult() {
        return eliminarResult;
    }

    public LiveData<Boolean> eliminarFinca(String fincaId) {
        MutableLiveData<Boolean> resultado = new MutableLiveData<>();

        agroRepository.eliminarFinca(fincaId).observeForever(success -> {
            resultado.postValue(success);

            if (success) {
                agroRepository.getFinca().observeForever(nuevasFincas -> {
                    fincas.postValue(nuevasFincas);
                });
            }
        });

        return resultado;
    }


    public LiveData<List<Insumo>> getInsumos() {
        return insumos;
    }

    public void cargarInsumos() {
        agroRepository.getInsumos().observeForever(lista -> {
            insumos.postValue(lista);
        });
    }

    public void registrarUsoInsumo(String insumoId, String animalId, String fecha, String fincaId, double cantidadUsada) {
        agroRepository.insertarUsoInsumo(insumoId, animalId, fecha, fincaId, cantidadUsada);
    }
    public LiveData<List<Animal>> getAnimal() {
        return animal;
    }

    public void cargarAnimales() {
        agroRepository.getAnimales().observeForever(animalesList -> {
            animales.postValue(animalesList);
        });
    }

    public void actualizarCantidadInsumo(String insumoId, double nuevaCantidad) {
        agroRepository.actualizarCantidadInsumo(insumoId, nuevaCantidad);
    }

}
