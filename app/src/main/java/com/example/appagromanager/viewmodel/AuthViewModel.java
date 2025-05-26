package com.example.appagromanager.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.appagromanager.models.Usuario;
import com.example.appagromanager.repository.AgroRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthViewModel extends ViewModel {

    private final AgroRepository repository = new AgroRepository();

    private final MutableLiveData<Boolean> authSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> authError = new MutableLiveData<>();
    private final MutableLiveData<FirebaseUser> currentUser = new MutableLiveData<>();

    public LiveData<FirebaseUser> getCurrentUser() {
        return currentUser;
    }

    private void updateCurrentUser() {
        currentUser.postValue(FirebaseAuth.getInstance().getCurrentUser());
    }

    public boolean isUserLoggedIn() {
        return repository.isUserLoggedIn();
    }


    public LiveData<Boolean> getAuthSuccess() {
        return authSuccess;
    }

    public LiveData<String> getAuthError() {
        return authError;
    }

    public void login(String email, String password) {
        repository.login(email, password, new AgroRepository.AuthCallback() {
            @Override
            public void onSuccess() {
                authSuccess.postValue(true);
            }

            @Override
            public void onError(String message) {
                authError.postValue(message);
            }
        });
    }

    public void register(String email, String password, Usuario usuario) {
        repository.register(email, password, usuario, new AgroRepository.AuthCallback() {
            @Override
            public void onSuccess() {
                authSuccess.postValue(true);
            }

            @Override
            public void onError(String message) {
                authError.postValue(message);
            }
        });
    }
}
