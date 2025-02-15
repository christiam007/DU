package com.example.proyecto_firebase.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.proyecto_firebase.models.User;
import com.example.proyecto_firebase.repositories.UserRepository;
import com.google.firebase.auth.FirebaseUser;
import android.util.Patterns;

public class LoginViewModel extends ViewModel {
    private UserRepository userRepository;
    private MutableLiveData<FirebaseUser> loginResult = new MutableLiveData<>();
    private MutableLiveData<String> loginError = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public LoginViewModel() {
        userRepository = new UserRepository();
    }

    public void login(String email, String password) {
        if (!validateInput(email, password)) return;
        isLoading.setValue(true);
        User user = new User(email, password);
        userRepository.iniciarSesion(user, loginResult, loginError);
    }

    // Método de validación
    private boolean validateInput(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            loginError.setValue("El email no puede estar vacío");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            loginError.setValue("Email inválido");
            return false;
        }

        if (password == null || password.trim().isEmpty()) {
            loginError.setValue("La contraseña no puede estar vacía");
            return false;
        }

        if (password.length() < 6) {
            loginError.setValue("La contraseña debe tener al menos 6 caracteres");
            return false;
        }

        return true;
    }

    // Getters para LiveData
    public LiveData<FirebaseUser> getLoginResult() {
        return loginResult;
    }

    public LiveData<String> getLoginError() {
        return loginError;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}
