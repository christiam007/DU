// Definición del paquete donde se encuentra esta clase
package com.example.proyecto_firebase.repositories;

// Importaciones necesarias para la funcionalidad
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener; // Para manejar finalización de tareas
import com.google.android.gms.tasks.OnFailureListener; // Para manejar fallos
import com.google.android.gms.tasks.OnSuccessListener; // Para manejar éxitos
import com.google.android.gms.tasks.Tasks; // Utilidades para tareas de Firebase
import com.google.firebase.auth.FirebaseAuth; // Para autenticación y obtener usuario actual
import com.google.firebase.database.DataSnapshot; // Representa datos de Firebase Database
import com.google.firebase.database.DatabaseError; // Para manejar errores de la base de datos
import com.google.firebase.database.DatabaseReference; // Referencia a ubicación en la base de datos
import com.google.firebase.database.FirebaseDatabase; // Punto de entrada a Firebase Database
import com.google.firebase.database.ValueEventListener; // Para escuchar cambios en los datos
import com.example.proyecto_firebase.models.Pelicula; // Modelo de datos para películas

import java.util.ArrayList;

// Clase repositorio para operaciones relacionadas con películas
public class PeliculaRepository {
    private DatabaseReference peliculasRef; // Referencia a la ubicación de películas en la base de datos
    private DatabaseReference usuariosRef; // Referencia a la ubicación de usuarios en la base de datos
    private FirebaseAuth firebaseAuth; // Para operaciones de autenticación

    // Constructor del repositorio
    public PeliculaRepository() {
        // Obtener instancia de la base de datos
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        // Inicializar referencia a la sección "peliculas" de la base de datos
        peliculasRef = database.getReference("peliculas");
        // Inicializar referencia a la sección "usuarios" de la base de datos
        usuariosRef = database.getReference("usuarios");
        // Inicializar Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
    }

    // Método para obtener las películas favoritas del usuario actual
    public void getFavoritos(ValueEventListener listener) {
        // Obtener ID del usuario actualmente autenticado
        String userId = firebaseAuth.getCurrentUser().getUid();
        // Añadir listener a la ubicación de favoritos del usuario
        usuariosRef
                .child(userId)
                .child("favoritos")
                .addValueEventListener(listener);
    }

    // Método para alternar estado de favorito de una película (añadir/quitar)
    public void toggleFavorito(String peliculaId, OnCompleteListener<Void> listener) {
        // Validar que el ID de película sea válido
        if (peliculaId == null || peliculaId.isEmpty()) {
            // Completar con error si el ID no es válido
            listener.onComplete(Tasks.forException(new Exception("ID de película inválido")));
            return;
        }

        // Obtener ID del usuario actual
        String userId = firebaseAuth.getCurrentUser().getUid();
        // Obtener referencia a la ubicación de favoritos del usuario
        DatabaseReference favoritosRef = usuariosRef
                .child(userId)
                .child("favoritos");

        // Obtener lista actual de favoritos
        favoritosRef.get().addOnSuccessListener(snapshot -> {
            // Crear lista para IDs de favoritos
            ArrayList<String> favoritosIds = new ArrayList<>();

            // Si existen favoritos, añadirlos a la lista (excepto el actual si existe)
            if (snapshot.exists()) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    String id = child.getValue(String.class);
                    if (id != null && !id.equals(peliculaId)) {
                        favoritosIds.add(id);
                    }
                }
            }

            // Añadir el ID actual si no está en la lista (toggle)
            if (!favoritosIds.contains(peliculaId)) {
                favoritosIds.add(peliculaId);
            }

            // Guardar la lista actualizada
            favoritosRef.setValue(favoritosIds).addOnCompleteListener(listener);
        }).addOnFailureListener(e ->
                // Propagar error si falla la obtención de favoritos
                listener.onComplete(Tasks.forException(e))
        );
    }

    // Método para verificar si una película es favorita
    public void isFavorite(String peliculaId, OnSuccessListener<Boolean> listener) {
        // Validar que el ID de película sea válido
        if (peliculaId == null || peliculaId.isEmpty()) {
            // Devolver falso si el ID no es válido
            listener.onSuccess(false);
            return;
        }

        // Obtener ID del usuario actual
        String userId = firebaseAuth.getCurrentUser().getUid();
        // Obtener lista de favoritos y verificar
        usuariosRef
                .child(userId)
                .child("favoritos")
                .get()
                .addOnSuccessListener(snapshot -> {
                    // Verificar si la película está en favoritos
                    if (snapshot.exists()) {
                        for (DataSnapshot child : snapshot.getChildren()) {
                            String id = child.getValue(String.class);
                            if (peliculaId.equals(id)) {
                                // Si está, devolver verdadero
                                listener.onSuccess(true);
                                return;
                            }
                        }
                    }
                    // Si no está, devolver falso
                    listener.onSuccess(false);
                });
    }

    // Método para obtener todas las películas
    public void getAllPeliculas(ValueEventListener listener) {
        // Añadir listener para obtener los datos una sola vez
        peliculasRef.addListenerForSingleValueEvent(listener);
    }

    // Método para obtener una película específica por ID
    public void getPelicula(String peliculaId, ValueEventListener listener) {
        // Validar que el ID de película sea válido
        if (peliculaId == null || peliculaId.isEmpty()) return;
        // Añadir listener al nodo específico de la película
        peliculasRef.child(peliculaId).addListenerForSingleValueEvent(listener);
    }

    // Método para eliminar una película de favoritos
    public void removeFavorito(String peliculaId, OnCompleteListener<Void> listener) {
        // Validar que el ID de película sea válido
        if (peliculaId == null || peliculaId.isEmpty()) {
            // Completar con error si el ID no es válido
            listener.onComplete(Tasks.forException(new Exception("ID de película inválido")));
            return;
        }

        // Obtener ID del usuario actual
        String userId = firebaseAuth.getCurrentUser().getUid();
        // Eliminar la película de favoritos
        usuariosRef
                .child(userId)
                .child("favoritos")
                .child(peliculaId)
                .removeValue()
                .addOnCompleteListener(listener);
    }

    // Método para añadir una película a favoritos
    public void addFavorito(String peliculaId, OnCompleteListener<Void> listener) {
        // Validar que el ID de película sea válido
        if (peliculaId == null || peliculaId.isEmpty()) {
            // Completar con error si el ID no es válido
            listener.onComplete(Tasks.forException(new Exception("ID de película inválido")));
            return;
        }

        // Obtener ID del usuario actual
        String userId = firebaseAuth.getCurrentUser().getUid();
        // Añadir la película a favoritos (con valor true)
        usuariosRef
                .child(userId)
                .child("favoritos")
                .child(peliculaId)
                .setValue(true)
                .addOnCompleteListener(listener);
    }
}