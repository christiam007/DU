// DashboardRepository.java
package com.example.proyecto_firebase.repositories;

import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.proyecto_firebase.models.Pelicula;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DashboardRepository {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference peliculasRef;
    private DatabaseReference usuariosRef;

    // Constructor del repositorio
    public DashboardRepository() {
        // Inicializar Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        // Inicializar referencia a películas
        peliculasRef = FirebaseDatabase.getInstance().getReference().child("peliculas");
        // Inicializar referencia a usuarios
        usuariosRef = FirebaseDatabase.getInstance().getReference().child("usuarios");
    }

    // Método para cerrar sesión
    public void signOut() {
        firebaseAuth.signOut();
    }

    // Obtener usuario actual
    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    // Método para obtener películas que no están en favoritos
    public void getPeliculasNoFavoritas(final OnPeliculasListener listener) {
        if (getCurrentUser() == null) {
            listener.onError("Usuario no autenticado");
            return;
        }

        final String userId = getCurrentUser().getUid();

        // Usamos ValueEventListener para recibir actualizaciones en tiempo real
        usuariosRef.child(userId).child("favoritos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Crear un conjunto para almacenar los IDs de favoritos
                final Set<String> favoritosIds = new HashSet<>();

                // Recopilar todos los IDs de favoritos
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String peliculaId = snapshot.getKey();
                    if (peliculaId != null) {
                        favoritosIds.add(peliculaId);
                    }
                }

                // Obtenemos todas las películas y filtramos
                peliculasRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Pelicula> peliculasNoFavoritas = new ArrayList<>();

                        for (DataSnapshot peliculaSnapshot : dataSnapshot.getChildren()) {
                            String peliculaId = peliculaSnapshot.getKey();

                            // Si no está en favoritos, la añadimos a la lista
                            if (peliculaId != null && !favoritosIds.contains(peliculaId)) {
                                Pelicula pelicula = peliculaSnapshot.getValue(Pelicula.class);
                                if (pelicula != null) {
                                    pelicula.setId(peliculaId);
                                    peliculasNoFavoritas.add(pelicula);
                                }
                            }
                        }

                        // Devolver la lista filtrada
                        listener.onPeliculasLoaded(peliculasNoFavoritas);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        listener.onError("Error al cargar películas: " + databaseError.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onError("Error al cargar favoritos: " + databaseError.getMessage());
            }
        });
    }

    // Interfaz para comunicar los resultados
    public interface OnPeliculasListener {
        void onPeliculasLoaded(List<Pelicula> peliculas);
        void onError(String error);
    }

    // Los métodos anteriores se mantienen para compatibilidad
    public void getPelicula(String peliculaId, ValueEventListener listener) {
        peliculasRef.child(peliculaId).addListenerForSingleValueEvent(listener);
    }

    public void getPeliculas(ValueEventListener listener) {
        peliculasRef.addValueEventListener(listener);
    }

    public void getPeliculasOnce(ValueEventListener listener) {
        peliculasRef.addListenerForSingleValueEvent(listener);
    }
}