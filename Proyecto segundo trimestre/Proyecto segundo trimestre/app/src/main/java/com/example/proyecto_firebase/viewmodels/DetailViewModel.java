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

// Clase ViewModel para la pantalla de detalle de una película
public class DetailViewModel extends ViewModel {
    private MutableLiveData<Boolean> esFavorito; // Estado de si la película es favorita
    private MutableLiveData<Boolean> isLoading; // Estado de carga
    private MutableLiveData<String> error; // Mensajes de error
    private DatabaseReference favoritosRef; // Referencia a la ubicación de favoritos en Firebase
    private String peliculaId; // ID de la película actual
    private String userId; // ID del usuario actual

    // Constructor del ViewModel
    public DetailViewModel() {
        // Inicializar LiveData con valores por defecto
        esFavorito = new MutableLiveData<>(false);
        isLoading = new MutableLiveData<>(false);
        error = new MutableLiveData<>();

        // Obtener ID del usuario actualmente autenticado
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Inicializar referencia a la ubicación de favoritos del usuario
        favoritosRef = FirebaseDatabase.getInstance().getReference()
                .child("usuarios")
                .child(userId)
                .child("favoritos");
    }

    // Método para obtener si la película es favorita (como LiveData de solo lectura)
    public LiveData<Boolean> getEsFavorito() {
        return esFavorito;
    }

    // Método para obtener estado de carga (como LiveData de solo lectura)
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    // Método para obtener mensajes de error (como LiveData de solo lectura)
    public LiveData<String> getError() {
        return error;
    }

    // Método para establecer el ID de la película y verificar su estado de favorito
    public void setPeliculaId(String peliculaId) {
        this.peliculaId = peliculaId;
        verificarSiEsFavorito(); // Verificar estado de favorito automáticamente
    }

    // Método privado para verificar si la película actual es favorita
    private void verificarSiEsFavorito() {
        // Activar estado de carga
        isLoading.setValue(true);

        // Añadir listener para observar el nodo específico de esta película en favoritos
        favoritosRef.child(peliculaId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Si el nodo existe, la película es favorita
                esFavorito.setValue(dataSnapshot.exists());
                // Desactivar estado de carga
                isLoading.setValue(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar error en caso de que la operación sea cancelada
                error.setValue("Error al verificar favorito: " + databaseError.getMessage());
                // Desactivar estado de carga
                isLoading.setValue(false);
            }
        });
    }

    // Método para alternar el estado de favorito de la película actual
    public void toggleFavorito(Pelicula pelicula) {
        // Activar estado de carga
        isLoading.setValue(true);

        // Verificar el estado actual de favorito
        if (esFavorito.getValue() != null && esFavorito.getValue()) {
            // Si ya es favorito, eliminar de favoritos
            favoritosRef.child(peliculaId).removeValue()
                    .addOnSuccessListener(aVoid -> isLoading.setValue(false)) // Éxito: desactivar carga
                    .addOnFailureListener(e -> {
                        // Fallo: reportar error y desactivar carga
                        error.setValue("Error al eliminar de favoritos: " + e.getMessage());
                        isLoading.setValue(false);
                    });
        } else {
            // Si no es favorito, agregar a favoritos guardando el objeto completo
            favoritosRef.child(peliculaId).setValue(pelicula)
                    .addOnSuccessListener(aVoid -> isLoading.setValue(false)) // Éxito: desactivar carga
                    .addOnFailureListener(e -> {
                        // Fallo: reportar error y desactivar carga
                        error.setValue("Error al agregar a favoritos: " + e.getMessage());
                        isLoading.setValue(false);
                    });
        }
    }
}