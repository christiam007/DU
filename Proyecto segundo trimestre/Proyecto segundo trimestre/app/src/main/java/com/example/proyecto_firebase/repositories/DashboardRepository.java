package com.example.proyecto_firebase.repositories;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DashboardRepository {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    public DashboardRepository() {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("peliculas");
    }

    public void signOut() {
        firebaseAuth.signOut();
    }

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    // Mantener el método existente para obtener una película específica
    public void getPelicula(String peliculaId, ValueEventListener listener) {
        databaseReference.child(peliculaId).addListenerForSingleValueEvent(listener);
    }

    // Agregar nuevo método para obtener todas las películas
    public void getPeliculas(ValueEventListener listener) {
        databaseReference.addValueEventListener(listener);
    }

    // Método opcional para obtener películas una sola vez
    public void getPeliculasOnce(ValueEventListener listener) {
        databaseReference.addListenerForSingleValueEvent(listener);
    }
}