package com.example.proyecto_firebase.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.proyecto_firebase.R;
import com.example.proyecto_firebase.adapters.PeliculaAdapter;
import com.example.proyecto_firebase.databinding.ActivityFavouritesBinding;
import com.example.proyecto_firebase.models.Pelicula;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FavouritesActivity extends AppCompatActivity implements PeliculaAdapter.OnPeliculaClickListener {
    private ActivityFavouritesBinding binding;
    private PeliculaAdapter adapter;
    private DatabaseReference favoritosRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide(); //Oculta el título de la aplicación
        binding = DataBindingUtil.setContentView(this, R.layout.activity_favourites);

        // Inicializar Firebase
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        favoritosRef = FirebaseDatabase.getInstance().getReference()
                .child("usuarios")
                .child(userId)
                .child("favoritos");

        setupRecyclerView();
        loadFavorites();
    }

    private void setupRecyclerView() {
        adapter = new PeliculaAdapter(new ArrayList<>(), this);
        binding.recyclerViewFavoritos.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewFavoritos.setAdapter(adapter);
    }

    private void loadFavorites() {
        favoritosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Pelicula> favoritesList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        Pelicula pelicula = snapshot.getValue(Pelicula.class);
                        if (pelicula != null) {
                            favoritesList.add(pelicula);
                        }
                    } catch (Exception e) {
                        // Si hay un error al convertir, lo saltamos
                        continue;
                    }
                }
                adapter.setPeliculas(favoritesList);

                if (favoritesList.isEmpty()) {
                    Toast.makeText(FavouritesActivity.this,
                            "No tienes películas favoritas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(FavouritesActivity.this,
                        "Error al cargar favoritos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPeliculaClick(Pelicula pelicula) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("titulo", pelicula.getTitulo());
        intent.putExtra("descripcion", pelicula.getDescripcion());
        intent.putExtra("imagen", pelicula.getImagen());
        startActivity(intent);
    }
}