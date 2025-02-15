package com.example.proyecto_firebase.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetailViewModel extends ViewModel {
    private MutableLiveData<Boolean> esFavorito;
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<String> error;
    private DatabaseReference favoritosRef;
    private String peliculaId;
    private String userId;

    public DetailViewModel() {
        esFavorito = new MutableLiveData<>(false);
        isLoading = new MutableLiveData<>(false);
        error = new MutableLiveData<>();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        favoritosRef = FirebaseDatabase.getInstance().getReference()
                .child("usuarios")
                .child(userId)
                .child("favoritos");
    }

    public LiveData<Boolean> getEsFavorito() {
        return esFavorito;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void setPeliculaId(String peliculaId) {
        this.peliculaId = peliculaId;
        verificarSiEsFavorito();
    }

    private void verificarSiEsFavorito() {
        isLoading.setValue(true);
        favoritosRef.child(peliculaId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                esFavorito.setValue(dataSnapshot.exists());
                isLoading.setValue(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                error.setValue("Error al verificar favorito: " + databaseError.getMessage());
                isLoading.setValue(false);
            }
        });
    }

    public void toggleFavorito() {
        isLoading.setValue(true);
        Boolean favorito = esFavorito.getValue();
        if (favorito != null) {
            if (favorito) {
                favoritosRef.child(peliculaId).removeValue()
                        .addOnSuccessListener(aVoid -> isLoading.setValue(false))
                        .addOnFailureListener(e -> {
                            error.setValue("Error al eliminar de favoritos: " + e.getMessage());
                            isLoading.setValue(false);
                        });
            } else {
                favoritosRef.child(peliculaId).setValue(true)
                        .addOnSuccessListener(aVoid -> isLoading.setValue(false))
                        .addOnFailureListener(e -> {
                            error.setValue("Error al agregar a favoritos: " + e.getMessage());
                            isLoading.setValue(false);
                        });
            }
        }
    }
}