package com.example.appagromanager.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.appagromanager.models.Usuario;
import com.example.appagromanager.repository.AgroRepository;

public class DrawerViewModel extends ViewModel {
    private AgroRepository agroRepository = new AgroRepository();
    private MutableLiveData<Usuario> usuarioLiveData = new MutableLiveData<>();

    public LiveData<Usuario> getUsuario() {
        return agroRepository.obtenerDatosUsuario();
    }
}
