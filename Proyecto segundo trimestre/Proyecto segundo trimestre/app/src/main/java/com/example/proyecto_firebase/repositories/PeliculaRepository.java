package com.example.proyecto_firebase.repositories;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.proyecto_firebase.models.Pelicula;

import java.util.ArrayList;

public class PeliculaRepository {
    private DatabaseReference peliculasRef;
    private DatabaseReference usuariosRef;
    private FirebaseAuth firebaseAuth;

    public PeliculaRepository() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        peliculasRef = database.getReference("peliculas");
        usuariosRef = database.getReference("usuarios");
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void getFavoritos(ValueEventListener listener) {
        String userId = firebaseAuth.getCurrentUser().getUid();
        usuariosRef
                .child(userId)
                .child("favoritos")
                .addValueEventListener(listener);
    }

    public void toggleFavorito(String peliculaId, OnCompleteListener<Void> listener) {
        if (peliculaId == null || peliculaId.isEmpty()) {
            listener.onComplete(Tasks.forException(new Exception("ID de película inválido")));
            return;
        }

        String userId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference favoritosRef = usuariosRef
                .child(userId)
                .child("favoritos");

        favoritosRef.get().addOnSuccessListener(snapshot -> {
            ArrayList<String> favoritosIds = new ArrayList<>();

            if (snapshot.exists()) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    String id = child.getValue(String.class);
                    if (id != null && !id.equals(peliculaId)) {
                        favoritosIds.add(id);
                    }
                }
            }

            if (!favoritosIds.contains(peliculaId)) {
                favoritosIds.add(peliculaId);
            }

            favoritosRef.setValue(favoritosIds).addOnCompleteListener(listener);
        }).addOnFailureListener(e ->
                listener.onComplete(Tasks.forException(e))
        );
    }

    public void isFavorite(String peliculaId, OnSuccessListener<Boolean> listener) {
        if (peliculaId == null || peliculaId.isEmpty()) {
            listener.onSuccess(false);
            return;
        }

        String userId = firebaseAuth.getCurrentUser().getUid();
        usuariosRef
                .child(userId)
                .child("favoritos")
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        for (DataSnapshot child : snapshot.getChildren()) {
                            String id = child.getValue(String.class);
                            if (peliculaId.equals(id)) {
                                listener.onSuccess(true);
                                return;
                            }
                        }
                    }
                    listener.onSuccess(false);
                });
    }

    public void getAllPeliculas(ValueEventListener listener) {
        peliculasRef.addListenerForSingleValueEvent(listener);
    }

    public void getPelicula(String peliculaId, ValueEventListener listener) {
        if (peliculaId == null || peliculaId.isEmpty()) return;
        peliculasRef.child(peliculaId).addListenerForSingleValueEvent(listener);
    }

    public void removeFavorito(String peliculaId, OnCompleteListener<Void> listener) {
        if (peliculaId == null || peliculaId.isEmpty()) {
            listener.onComplete(Tasks.forException(new Exception("ID de película inválido")));
            return;
        }

        String userId = firebaseAuth.getCurrentUser().getUid();
        usuariosRef
                .child(userId)
                .child("favoritos")
                .child(peliculaId)
                .removeValue()
                .addOnCompleteListener(listener);
    }

    public void addFavorito(String peliculaId, OnCompleteListener<Void> listener) {
        if (peliculaId == null || peliculaId.isEmpty()) {
            listener.onComplete(Tasks.forException(new Exception("ID de película inválido")));
            return;
        }

        String userId = firebaseAuth.getCurrentUser().getUid();
        usuariosRef
                .child(userId)
                .child("favoritos")
                .child(peliculaId)
                .setValue(true)
                .addOnCompleteListener(listener);
    }
}