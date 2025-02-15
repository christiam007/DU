package com.example.proyecto_firebase.views;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import com.bumptech.glide.Glide;
import com.example.proyecto_firebase.R;
import com.example.proyecto_firebase.databinding.ActivityDetailBinding;
import com.example.proyecto_firebase.models.Pelicula;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class DetailActivity extends AppCompatActivity {
    private ActivityDetailBinding binding;
    private DatabaseReference favoritosRef;
    private Pelicula pelicula;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide(); //Oculta el título de la aplicación
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        // Inicializar Firebase
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        favoritosRef = FirebaseDatabase.getInstance().getReference()
                .child("usuarios")
                .child(userId)
                .child("favoritos");

        // Obtener datos del Intent
        pelicula = new Pelicula();
        pelicula.setTitulo(getIntent().getStringExtra("titulo"));
        pelicula.setDescripcion(getIntent().getStringExtra("descripcion"));
        pelicula.setImagen(getIntent().getStringExtra("imagen"));
        pelicula.generateId();

        // Establecer los datos en el binding
        binding.setPelicula(pelicula);

        // Configurar accesibilidad para los elementos de detalle
        setupAccessibility();

        // Cargar la imagen con Glide
        if (pelicula.getImagen() != null && !pelicula.getImagen().isEmpty()) {
            Glide.with(this)
                    .load(pelicula.getImagen())
                    .centerCrop()
                    .into(binding.ivPeliculaDetalle);
        }

        // Verificar si es favorito!!
        checkFavoriteStatus();

        // Configurar el click del FAB
        setupFabClick();
    }

    private void setupAccessibility() {
        // Configurar descripciones para accesibilidad
        binding.ivPeliculaDetalle.setContentDescription(
                "Imagen detallada de " + pelicula.getTitulo());

        binding.tvTituloDetalle.setContentDescription(
                "Título: " + pelicula.getTitulo());

        binding.tvDescripcionDetalle.setContentDescription(
                "Descripción: " + pelicula.getDescripcion());
    }

    private void checkFavoriteStatus() {
        favoritosRef.child(pelicula.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                isFavorite = dataSnapshot.exists() && dataSnapshot.getValue(Pelicula.class) != null;
                updateFabIcon();
                updateFabAccessibility();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DetailActivity.this,
                        "Error al verificar favoritos", Toast.LENGTH_SHORT).show();
                announceForAccessibility("Error al verificar estado de favoritos");
            }
        });
    }

    private void updateFabIcon() {
        binding.fabFavorito.setImageResource(
                isFavorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_border
        );
    }

    private void updateFabAccessibility() {
        if (binding != null && binding.fabFavorito != null) {
            String description = isFavorite ?
                    getString(R.string.fab_remove_favorite) :
                    getString(R.string.fab_add_favorite);
            binding.fabFavorito.setContentDescription(description);
            binding.fabFavorito.announceForAccessibility(description);
        }
    }

    private void setupFabClick() {
        binding.fabFavorito.setOnClickListener(v -> {
            v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
            if (isFavorite) {
                // Eliminar de favoritos
                favoritosRef.child(pelicula.getId()).removeValue()
                        .addOnSuccessListener(aVoid -> {
                            announceForAccessibility("Eliminado de favoritos");
                            Toast.makeText(this, "Eliminado de favoritos",
                                    Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            announceForAccessibility("Error al eliminar de favoritos");
                            Toast.makeText(this, "Error al eliminar de favoritos",
                                    Toast.LENGTH_SHORT).show();
                        });
            } else {
                // Guardar la película completa
                pelicula.setFavorite(true);
                favoritosRef.child(pelicula.getId()).setValue(pelicula)
                        .addOnSuccessListener(aVoid -> {
                            announceForAccessibility("Agregado a favoritos");
                            Toast.makeText(this, "Agregado a favoritos",
                                    Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            announceForAccessibility("Error al agregar a favoritos");
                            Toast.makeText(this, "Error al agregar a favoritos",
                                    Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    private void announceForAccessibility(String message) {
        binding.getRoot().announceForAccessibility(message);
    }
}