// Definición del paquete donde se encuentra esta clase
package com.example.proyecto_firebase.repositories;

// Importaciones necesarias para la funcionalidad
import androidx.lifecycle.MutableLiveData;

import androidx.lifecycle.MutableLiveData; // Importación duplicada
import com.google.android.gms.tasks.OnFailureListener; // Interfaz para manejar fallos
import com.google.android.gms.tasks.OnSuccessListener; // Interfaz para manejar éxitos
import com.google.firebase.auth.FirebaseAuth; // Para autenticación de Firebase
import com.google.firebase.auth.FirebaseUser; // Modelo de usuario de Firebase
import com.google.firebase.database.DatabaseReference; // Referencia a ubicación en la base de datos
import com.google.firebase.database.FirebaseDatabase; // Punto de entrada a Firebase Database
import com.example.proyecto_firebase.models.User; // Modelo de datos para usuario

// Clase repositorio para operaciones relacionadas con usuarios
public class UserRepository {
    private final FirebaseAuth firebaseAuth; // Para operaciones de autenticación
    private final DatabaseReference databaseReference; // Referencia a la ubicación de usuarios en la base de datos

    // Constructor del repositorio
    public UserRepository() {
        // Inicializar Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        // Inicializar referencia a la sección "usuarios" de la base de datos
        databaseReference = FirebaseDatabase.getInstance().getReference("usuarios");
    }

    // Método para iniciar sesión utilizando MutableLiveData para comunicar resultados
    public void iniciarSesion(User usuario, MutableLiveData<FirebaseUser> resultado,
                              MutableLiveData<String> error) {
        // Llamar al método de autenticación de Firebase con email y contraseña
        firebaseAuth.signInWithEmailAndPassword(usuario.getCorreo(), usuario.getContrasena())
                .addOnSuccessListener(authResult ->
                        // Si es exitoso, actualizar el LiveData con el usuario autenticado
                        resultado.setValue(firebaseAuth.getCurrentUser()))
                .addOnFailureListener(e ->
                        // Si falla, actualizar el LiveData de error con el mensaje
                        error.setValue("Error al iniciar sesión: " + e.getMessage()));
    }

    // Método para registrar un usuario nuevo utilizando callbacks para comunicar resultados
    public void registrarUsuario(User usuario,
                                 OnSuccessListener<FirebaseUser> oyenteExito,
                                 OnFailureListener oyenteFallo) {
        // Crear nuevo usuario en Firebase Auth con email y contraseña
        firebaseAuth.createUserWithEmailAndPassword(usuario.getCorreo(), usuario.getContrasena())
                .addOnSuccessListener(authResult -> {
                    // Si se crea el usuario en Auth, obtener su UID
                    FirebaseUser usuarioFirebase = firebaseAuth.getCurrentUser();
                    if (usuarioFirebase != null) {
                        // Establecer el UID en el objeto usuario
                        usuario.setUid(usuarioFirebase.getUid());
                        // Guardar datos adicionales del usuario en la base de datos
                        guardarDatosUsuario(usuario, oyenteExito, oyenteFallo);
                    }
                })
                .addOnFailureListener(oyenteFallo); // Propagar error si falla la creación
    }

    // Método privado para guardar datos del usuario en Realtime Database
    private void guardarDatosUsuario(User usuario,
                                     OnSuccessListener<FirebaseUser> oyenteExito,
                                     OnFailureListener oyenteFallo) {
        // Crear/actualizar el nodo del usuario con su información
        databaseReference.child(usuario.getUid())
                .setValue(usuario.toMap()) // Convertir objeto a Map para almacenar
                .addOnSuccessListener(aVoid ->
                        // Si es exitoso, notificar con el usuario actual
                        oyenteExito.onSuccess(firebaseAuth.getCurrentUser()))
                .addOnFailureListener(e -> {
                    // Si falla el guardado de datos adicionales
                    if (firebaseAuth.getCurrentUser() != null) {
                        // Eliminar el usuario de Auth para no dejar un usuario incompleto
                        firebaseAuth.getCurrentUser().delete();
                    }
                    // Propagar el error
                    oyenteFallo.onFailure(e);
                });
    }

    // Método para obtener el usuario actualmente autenticado
    public FirebaseUser obtenerUsuarioActual() {
        return firebaseAuth.getCurrentUser();
    }
}