// Definición del paquete donde se encuentra esta clase
package com.example.proyecto_firebase.viewmodels;

// Importaciones necesarias para la funcionalidad del ViewModel
import androidx.lifecycle.LiveData; // Para exponer datos observables de solo lectura
import androidx.lifecycle.MutableLiveData; // Para datos observables modificables
import androidx.lifecycle.ViewModel; // Clase base para ViewModels
import android.util.Patterns; // Para validación de patrones como email
import com.example.proyecto_firebase.models.User; // Modelo de datos para usuario
import com.example.proyecto_firebase.repositories.UserRepository; // Repositorio para operaciones con usuarios
import com.google.firebase.auth.FirebaseUser; // Modelo de usuario de Firebase

// Clase ViewModel para la pantalla de registro
public class RegisterViewModel extends ViewModel {
    // LiveData mutables para manejar los estados internamente
    private final MutableLiveData<String> resultadoRegistro = new MutableLiveData<>(); // Resultado del proceso
    private final MutableLiveData<String> errorValidacion = new MutableLiveData<>(); // Errores de validación
    private final MutableLiveData<Boolean> estaCargando = new MutableLiveData<>(); // Estado de carga

    // Repositorio para operaciones con usuarios (Firebase)
    private final UserRepository repositorioUsuario;

    // Constructor del ViewModel
    public RegisterViewModel() {
        // Inicializar el repositorio
        repositorioUsuario = new UserRepository();
        // Establecer estado inicial de carga como falso
        estaCargando.setValue(false);
    }

    // Método principal para registrar un nuevo usuario
    public void registrarUsuario(String nombre, String apellido, String correo,
                                 String contrasena, String confirmarContrasena,
                                 String telefono, String direccion) {

        // Validar las entradas antes de proceder
        if (!validarEntradas(nombre, apellido, correo, contrasena,
                confirmarContrasena, telefono, direccion)) {
            return; // Si la validación falla, salir del método
        }

        // Activar estado de carga
        estaCargando.setValue(true);

        // Crear y configurar objeto de usuario con los datos proporcionados
        User nuevoUsuario = new User();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setApellido(apellido);
        nuevoUsuario.setCorreo(correo);
        nuevoUsuario.setContrasena(contrasena);
        nuevoUsuario.setTelefono(telefono);
        nuevoUsuario.setDireccion(direccion);

        // Llamar al repositorio para registrar el usuario en Firebase
        repositorioUsuario.registrarUsuario(
                nuevoUsuario,
                usuarioFirebase -> {
                    // Callback de éxito
                    resultadoRegistro.setValue("SUCCESS");
                    estaCargando.setValue(false);
                },
                excepcion -> {
                    // Callback de error
                    errorValidacion.setValue(excepcion.getMessage());
                    estaCargando.setValue(false);
                }
        );
    }

    // Método para validar todas las entradas del formulario
    private boolean validarEntradas(String nombre, String apellido, String correo,
                                    String contrasena, String confirmarContrasena,
                                    String telefono, String direccion) {

        // Validar nombre
        if (nombre == null || nombre.trim().isEmpty()) {
            errorValidacion.setValue("El nombre no puede estar vacío");
            return false;
        }

        // Validar apellido
        if (apellido == null || apellido.trim().isEmpty()) {
            errorValidacion.setValue("El apellido no puede estar vacío");
            return false;
        }

        // Validar formato de correo electrónico
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            errorValidacion.setValue("El correo no es válido");
            return false;
        }

        // Validar longitud mínima de contraseña
        if (contrasena == null || contrasena.length() < 6) {
            errorValidacion.setValue("La contraseña debe tener al menos 6 caracteres");
            return false;
        }

        // Validar que las contraseñas coincidan
        if (!contrasena.equals(confirmarContrasena)) {
            errorValidacion.setValue("Las contraseñas no coinciden");
            return false;
        }

        // Validar formato de teléfono (9 dígitos)
        if (telefono == null || !telefono.matches("\\d{9}")) {
            errorValidacion.setValue("El teléfono debe tener 9 dígitos");
            return false;
        }

        // Validar longitud mínima de dirección
        if (direccion == null || direccion.length() < 20) {
            errorValidacion.setValue("La dirección debe tener al menos 20 caracteres");
            return false;
        }

        // Si todas las validaciones pasan
        return true;
    }

    // Método para obtener el resultado del registro (como LiveData de solo lectura)
    public LiveData<String> obtenerResultadoRegistro() {
        return resultadoRegistro;
    }

    // Método para obtener errores de validación (como LiveData de solo lectura)
    public LiveData<String> obtenerErrorValidacion() {
        return errorValidacion;
    }

    // Método para obtener estado de carga (como LiveData de solo lectura)
    public LiveData<Boolean> obtenerEstadoCarga() {
        return estaCargando;
    }

    // Método para limpiar errores de validación
    public void limpiarErrores() {
        errorValidacion.setValue(null);
    }

    // Método para obtener el usuario actual de Firebase
    public FirebaseUser obtenerUsuarioActual() {
        return repositorioUsuario.obtenerUsuarioActual();
    }
}