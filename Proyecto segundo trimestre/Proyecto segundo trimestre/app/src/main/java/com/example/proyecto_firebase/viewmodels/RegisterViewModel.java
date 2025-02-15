package com.example.proyecto_firebase.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.util.Patterns;
import com.example.proyecto_firebase.models.User;
import com.example.proyecto_firebase.repositories.UserRepository;
import com.google.firebase.auth.FirebaseUser;

public class RegisterViewModel extends ViewModel {
    private final MutableLiveData<String> resultadoRegistro = new MutableLiveData<>();
    private final MutableLiveData<String> errorValidacion = new MutableLiveData<>();
    private final MutableLiveData<Boolean> estaCargando = new MutableLiveData<>();

    private final UserRepository repositorioUsuario;

    public RegisterViewModel() {
        repositorioUsuario = new UserRepository();
        estaCargando.setValue(false);
    }

    public void registrarUsuario(String nombre, String apellido, String correo,
                                 String contrasena, String confirmarContrasena,
                                 String telefono, String direccion) {

        if (!validarEntradas(nombre, apellido, correo, contrasena,
                confirmarContrasena, telefono, direccion)) {
            return;
        }

        estaCargando.setValue(true);

        User nuevoUsuario = new User();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setApellido(apellido);
        nuevoUsuario.setCorreo(correo);
        nuevoUsuario.setContrasena(contrasena);
        nuevoUsuario.setTelefono(telefono);
        nuevoUsuario.setDireccion(direccion);

        repositorioUsuario.registrarUsuario(
                nuevoUsuario,
                usuarioFirebase -> {
                    resultadoRegistro.setValue("SUCCESS");
                    estaCargando.setValue(false);
                },
                excepcion -> {
                    errorValidacion.setValue(excepcion.getMessage());
                    estaCargando.setValue(false);
                }
        );
    }

    private boolean validarEntradas(String nombre, String apellido, String correo,
                                    String contrasena, String confirmarContrasena,
                                    String telefono, String direccion) {

        if (nombre == null || nombre.trim().isEmpty()) {
            errorValidacion.setValue("El nombre no puede estar vacío");
            return false;
        }

        if (apellido == null || apellido.trim().isEmpty()) {
            errorValidacion.setValue("El apellido no puede estar vacío");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            errorValidacion.setValue("El correo no es válido");
            return false;
        }

        if (contrasena == null || contrasena.length() < 6) {
            errorValidacion.setValue("La contraseña debe tener al menos 6 caracteres");
            return false;
        }

        if (!contrasena.equals(confirmarContrasena)) {
            errorValidacion.setValue("Las contraseñas no coinciden");
            return false;
        }

        if (telefono == null || !telefono.matches("\\d{9}")) {
            errorValidacion.setValue("El teléfono debe tener 9 dígitos");
            return false;
        }

        if (direccion == null || direccion.length() < 20) {
            errorValidacion.setValue("La dirección debe tener al menos 20 caracteres");
            return false;
        }

        return true;
    }

    public LiveData<String> obtenerResultadoRegistro() {
        return resultadoRegistro;
    }

    public LiveData<String> obtenerErrorValidacion() {
        return errorValidacion;
    }

    public LiveData<Boolean> obtenerEstadoCarga() {
        return estaCargando;
    }

    public void limpiarErrores() {
        errorValidacion.setValue(null);
    }

    public FirebaseUser obtenerUsuarioActual() {
        return repositorioUsuario.obtenerUsuarioActual();
    }
}