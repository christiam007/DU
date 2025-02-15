package com.example.proyecto_firebase.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.proyecto_firebase.models.Pelicula;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class FavouritesViewModel extends ViewModel {
    private MutableLiveData<List<Pelicula>> favoritos;
    private DatabaseReference favoritosRef;
    private DatabaseReference peliculasRef;
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<String> error;
    private String userId;

    public FavouritesViewModel() {
        favoritos = new MutableLiveData<>(new ArrayList<>());
        isLoading = new MutableLiveData<>(false);
        error = new MutableLiveData<>();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        favoritosRef = FirebaseDatabase.getInstance().getReference()
                .child("usuarios")
                .child(userId)
                .child("favoritos");
        peliculasRef = FirebaseDatabase.getInstance().getReference().child("peliculas");
    }

    public LiveData<List<Pelicula>> getFavoritos() {
        return favoritos;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void cargarFavoritos() {
        isLoading.setValue(true);
        favoritosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    List<Pelicula> listaFavoritos = new ArrayList<>();
                    for (DataSnapshot favoritoSnapshot : dataSnapshot.getChildren()) {
                        String peliculaId = favoritoSnapshot.getKey();
                        cargarPeliculaFavorita(peliculaId, listaFavoritos);
                    }
                } catch (Exception e) {
                    error.setValue("Error al cargar favoritos: " + e.getMessage());
                    isLoading.setValue(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                error.setValue("Error en la base de datos: " + databaseError.getMessage());
                isLoading.setValue(false);
            }
        });
    }

    private void cargarPeliculaFavorita(String peliculaId, final List<Pelicula> listaFavoritos) {
        peliculasRef.child(peliculaId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    Pelicula pelicula = dataSnapshot.getValue(Pelicula.class);
                    if (pelicula != null) {
                        pelicula.setId(dataSnapshot.getKey());
                        pelicula.setFavorite(true);
                        listaFavoritos.add(pelicula);
                        favoritos.setValue(listaFavoritos);
                    }
                } catch (Exception e) {
                    error.setValue("Error al procesar película favorita: " + e.getMessage());
                } finally {
                    isLoading.setValue(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                error.setValue("Error al cargar película favorita: " + databaseError.getMessage());
                isLoading.setValue(false);
            }
        });
    }

    public void toggleFavorito(Pelicula pelicula) {
        isLoading.setValue(true);
        favoritosRef.child(pelicula.getId()).removeValue()
                .addOnSuccessListener(aVoid -> {
                    List<Pelicula> currentList = favoritos.getValue();
                    if (currentList != null) {
                        currentList.remove(pelicula);
                        favoritos.setValue(currentList);
                    }
                    isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    error.setValue("Error al eliminar de favoritos: " + e.getMessage());
                    isLoading.setValue(false);
                });
    }
}