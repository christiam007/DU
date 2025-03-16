// Definición del paquete donde se encuentra esta clase
package com.example.proyecto_firebase.viewmodels;

// Importaciones necesarias para la funcionalidad del ViewModel
import androidx.lifecycle.LiveData; // Para exponer datos observables de solo lectura
import androidx.lifecycle.MutableLiveData; // Para datos observables modificables
import androidx.lifecycle.ViewModel; // Clase base para ViewModels
import com.example.proyecto_firebase.models.Pelicula; // Modelo de datos para películas
import com.google.firebase.auth.FirebaseAuth; // Para autenticación y obtener usuario actual
import com.google.firebase.database.DataSnapshot; // Representa datos de Firebase Database
import com.google.firebase.database.DatabaseError; // Para manejar errores de la base de datos
import com.google.firebase.database.DatabaseReference; // Referencia a ubicación en la base de datos
import com.google.firebase.database.FirebaseDatabase; // Punto de entrada a Firebase Database
import com.google.firebase.database.ValueEventListener; // Para escuchar cambios en los datos
import java.util.ArrayList;
import java.util.List;

// Clase ViewModel para la pantalla de películas favoritas
public class FavouritesViewModel extends ViewModel {
    private MutableLiveData<List<Pelicula>> favoritos; // Lista de películas favoritas
    private DatabaseReference favoritosRef; // Referencia a la ubicación de favoritos en Firebase
    private DatabaseReference peliculasRef; // Referencia a la ubicación de películas en Firebase
    private MutableLiveData<Boolean> isLoading; // Estado de carga
    private MutableLiveData<String> error; // Mensajes de error
    private String userId; // ID del usuario actual

    // Constructor del ViewModel
    public FavouritesViewModel() {
        // Inicializar LiveData con valores por defecto
        favoritos = new MutableLiveData<>(new ArrayList<>());
        isLoading = new MutableLiveData<>(false);
        error = new MutableLiveData<>();

        // Obtener ID del usuario actualmente autenticado
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Inicializar referencia a la ubicación de favoritos del usuario
        favoritosRef = FirebaseDatabase.getInstance().getReference()
                .child("usuarios")
                .child(userId)
                .child("favoritos");

        // Inicializar referencia a todas las películas
        peliculasRef = FirebaseDatabase.getInstance().getReference().child("peliculas");
    }

    // Método para obtener la lista de favoritos (como LiveData de solo lectura)
    public LiveData<List<Pelicula>> getFavoritos() {
        return favoritos;
    }

    // Método para obtener estado de carga (como LiveData de solo lectura)
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    // Método para obtener mensajes de error (como LiveData de solo lectura)
    public LiveData<String> getError() {
        return error;
    }

    // Método para cargar la lista de películas favoritas del usuario
    public void cargarFavoritos() {
        // Activar estado de carga
        isLoading.setValue(true);

        // Añadir listener para cambios en la lista de favoritos
        favoritosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Crear nueva lista para almacenar resultados
                List<Pelicula> listaFavoritos = new ArrayList<>();

                // Iterar sobre cada favorito en la base de datos
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        // Convertir snapshot a objeto Pelicula
                        Pelicula pelicula = snapshot.getValue(Pelicula.class);
                        if (pelicula != null) {
                            // Establecer ID de la película desde la clave del snapshot
                            pelicula.setId(snapshot.getKey());
                            // Marcar como favorito
                            pelicula.setFavorite(true);
                            // Añadir a la lista
                            listaFavoritos.add(pelicula);
                        }
                    } catch (Exception e) {
                        // Capturar y reportar errores en la conversión
                        error.setValue("Error al procesar favorito: " + e.getMessage());
                    }
                }

                // Actualizar LiveData con la nueva lista
                favoritos.setValue(listaFavoritos);
                // Desactivar estado de carga
                isLoading.setValue(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar error en caso de que la operación sea cancelada
                error.setValue("Error al cargar favoritos: " + databaseError.getMessage());
                // Desactivar estado de carga
                isLoading.setValue(false);
            }
        });
    }

    // Método para añadir/eliminar una película de favoritos
    public void toggleFavorito(Pelicula pelicula) {
        // Activar estado de carga
        isLoading.setValue(true);

        // Eliminar la película de favoritos (esta implementación solo elimina)
        favoritosRef.child(pelicula.getId()).removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Actualizar la lista local de favoritos
                    List<Pelicula> currentList = favoritos.getValue();
                    if (currentList != null) {
                        // Eliminar la película de la lista
                        currentList.remove(pelicula);
                        // Actualizar LiveData
                        favoritos.setValue(currentList);
                    }
                    // Desactivar estado de carga
                    isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    // Manejar error en caso de fallo
                    error.setValue("Error al eliminar de favoritos: " + e.getMessage());
                    // Desactivar estado de carga
                    isLoading.setValue(false);
                });
    }
}