package com.example.proyecto_firebase.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.switchmaterial.SwitchMaterial;

import com.example.proyecto_firebase.R;
import com.example.proyecto_firebase.databinding.ActivityDashboardBinding;
import com.example.proyecto_firebase.adapters.PeliculaAdapter;
import com.example.proyecto_firebase.models.Pelicula;
import com.example.proyecto_firebase.utils.ThemeHelper;
import com.example.proyecto_firebase.viewmodels.DashboardViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DashboardActivity extends AppCompatActivity implements PeliculaAdapter.OnPeliculaClickListener {
    private ActivityDashboardBinding binding;
    private DashboardViewModel dashboardViewModel;
    private PeliculaAdapter peliculaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Aplicar el tema según las preferencias antes de crear la activity
        if (ThemeHelper.isDarkMode(this)) {
            setTheme(R.style.Theme_App);
        }

        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide(); //Oculta el título de la aplicación
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard);
        binding.setLifecycleOwner(this);

        // Inicializar ViewModel
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        // Configurar RecyclerView
        setupRecyclerView();

        // Configurar el switch del tema
        setupThemeSwitch();

        // Observar cambios en los datos
        observeViewModel();

        // Configurar listeners
        setupListeners();

        // Cargar datos
        dashboardViewModel.cargarPeliculas();
    }

    private void setupThemeSwitch() {
        // Configurar estado inicial del switch según el tema actual
        binding.switchTheme.setChecked(ThemeHelper.isDarkMode(this));

        // Configurar listener para cambios en el switch
        binding.switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ThemeHelper.setDarkMode(this, isChecked);
            // Recrear la activity para aplicar el nuevo tema
            recreate();
        });
    }

    private void setupRecyclerView() {
        peliculaAdapter = new PeliculaAdapter(new ArrayList<>(), this);
        binding.recyclerViewPeliculas.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewPeliculas.setAdapter(peliculaAdapter);
    }

    private void observeViewModel() {
        dashboardViewModel.getPeliculas().observe(this, new Observer<List<Pelicula>>() {
            @Override
            public void onChanged(List<Pelicula> peliculas) {
                if (peliculas != null) {
                    peliculaAdapter.setPeliculas(peliculas);
                }
            }
        });

        dashboardViewModel.getNavigateToLogin().observe(this, shouldNavigate -> {
            if (shouldNavigate) {
                startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
                finish();
            }
        });

        // Observar errores si los hay
        dashboardViewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });

        // Observar estado de carga
        dashboardViewModel.getIsLoading().observe(this, isLoading -> {
            // Aquí puedes mostrar u ocultar un indicador de carga si lo tienes
        });
    }

    private void setupListeners() {
        // Listener para el botón de cerrar sesión
        binding.btnCerrarSesion.setOnClickListener(v -> {
            dashboardViewModel.cerrarSesion();
            Toast.makeText(this, "Cerraste Sesión Exitosamente", Toast.LENGTH_SHORT).show();
        });

        // Listener para el botón de favoritos
        binding.btnFavoritos.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, FavouritesActivity.class));
        });
    }

    @Override
    public void onPeliculaClick(Pelicula pelicula) {
        // Navegar a DetailActivity cuando se hace click en una película
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("id", pelicula.getId());
        intent.putExtra("titulo", pelicula.getTitulo());
        intent.putExtra("descripcion", pelicula.getDescripcion());
        intent.putExtra("imagen", pelicula.getImagen());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Actualizar el estado del switch si el tema ha cambiado
        if (binding.switchTheme != null) {
            binding.switchTheme.setChecked(ThemeHelper.isDarkMode(this));
        }
    }
}