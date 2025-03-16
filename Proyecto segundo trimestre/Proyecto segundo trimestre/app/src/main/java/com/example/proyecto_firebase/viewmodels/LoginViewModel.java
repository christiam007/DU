// Definición del paquete donde se encuentra esta clase
package com.example.proyecto_firebase.viewmodels;

// Importaciones necesarias para la funcionalidad del ViewModel
import androidx.lifecycle.LiveData; // Para exponer datos observables de solo lectura
import androidx.lifecycle.MutableLiveData; // Para datos observables modificables
import androidx.lifecycle.ViewModel; // Clase base para ViewModels

import com.example.proyecto_firebase.models.User; // Modelo de datos para usuario
import com.example.proyecto_firebase.repositories.UserRepository; // Repositorio para operaciones con usuarios
import com.google.firebase.auth.FirebaseUser; // Modelo de usuario de Firebase
import android.util.Patterns; // Para validación de patrones como email

// Clase ViewModel para la pantalla de inicio de sesión
public class LoginViewModel extends ViewModel {
    private UserRepository userRepository; // Repositorio para operaciones con usuarios (Firebase)
    private MutableLiveData<FirebaseUser> loginResult = new MutableLiveData<>(); // Resultado del inicio de sesión
    private MutableLiveData<String> loginError = new MutableLiveData<>(); // Errores de inicio de sesión
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(); // Estado de carga

    // Constructor del ViewModel
    public LoginViewModel() {
        // Inicializar el repositorio de usuarios
        userRepository = new UserRepository();
    }

    // Método principal para iniciar sesión
    public void login(String email, String password) {
        // Validar las entradas antes de proceder
        if (!validateInput(email, password)) return; // Si la validación falla, salir del método

        // Activar estado de carga
        isLoading.setValue(true);

        // Crear objeto usuario con credenciales
        User user = new User(email, password);

        // Llamar al repositorio para iniciar sesión en Firebase
        userRepository.iniciarSesion(user, loginResult, loginError);
    }

    // Método de validación de entradas
    private boolean validateInput(String email, String password) {
        // Validar que el email no esté vacío
        if (email == null || email.trim().isEmpty()) {
            loginError.setValue("El email no puede estar vacío");
            return false;
        }

        // Validar formato de email usando Patterns
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            loginError.setValue("Email inválido");
            return false;
        }

        // Validar que la contraseña no esté vacía
        if (password == null || password.trim().isEmpty()) {
            loginError.setValue("La contraseña no puede estar vacía");
            return false;
        }

        // Validar longitud mínima de contraseña
        if (password.length() < 6) {
            loginError.setValue("La contraseña debe tener al menos 6 caracteres");
            return false;
        }

        // Si todas las validaciones pasan
        return true;
    }

    // Método para obtener el resultado del login (como LiveData de solo lectura)
    public LiveData<FirebaseUser> getLoginResult() {
        return loginResult;
    }

    // Método para obtener errores de login (como LiveData de solo lectura)
    public LiveData<String> getLoginError() {
        return loginError;
    }

    // Método para obtener estado de carga (como LiveData de solo lectura)
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}