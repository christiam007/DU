package com.example.proyecto_firebase.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.example.proyecto_firebase.R;
import com.example.proyecto_firebase.databinding.FragmentDetailBinding;
import com.example.proyecto_firebase.models.Pelicula;
import com.example.proyecto_firebase.viewmodels.DetailViewModel;

public class DetailFragment extends Fragment {
    private FragmentDetailBinding binding;
    private DetailViewModel viewModel;
    private Pelicula pelicula;

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializar ViewModel
        viewModel = new ViewModelProvider(this).get(DetailViewModel.class);

        // Obtener datos del Bundle
        if (getArguments() != null) {
            pelicula = new Pelicula();
            pelicula.setId(getArguments().getString("id"));
            pelicula.setTitulo(getArguments().getString("titulo"));
            pelicula.setDescripcion(getArguments().getString("descripcion"));
            pelicula.setImagen(getArguments().getString("imagen"));

            // Establecer los datos en el binding
            binding.setPelicula(pelicula);

            // Configurar accesibilidad
            setupAccessibility();

            // Cargar imagen
            loadImage();

            // Verificar estado de favorito
            viewModel.setPeliculaId(pelicula.getId());
        }

        // Configurar observadores
        setupObservers();

        // Configurar FAB
        setupFabClick();
    }

    private void setupAccessibility() {
        binding.ivPeliculaDetalle.setContentDescription(
                "Imagen detallada de " + pelicula.getTitulo());

        binding.tvTituloDetalle.setContentDescription(
                "Título: " + pelicula.getTitulo());

        binding.tvDescripcionDetalle.setContentDescription(
                "Descripción: " + pelicula.getDescripcion());
    }

    private void loadImage() {
        if (pelicula.getImagen() != null && !pelicula.getImagen().isEmpty()) {
            Glide.with(requireContext())
                    .load(pelicula.getImagen())
                    .centerCrop()
                    .into(binding.ivPeliculaDetalle);
        }
    }

    private void setupObservers() {
        viewModel.getEsFavorito().observe(getViewLifecycleOwner(), isFavorite -> {
            updateFabIcon(isFavorite);
            updateFabAccessibility(isFavorite);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                announceForAccessibility("Error: " + error);
            }
        });
    }

    private void updateFabIcon(boolean isFavorite) {
        binding.fabFavorito.setImageResource(
                isFavorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_border
        );
    }

    private void updateFabAccessibility(boolean isFavorite) {
        String description = isFavorite ?
                getString(R.string.fab_remove_favorite) :
                getString(R.string.fab_add_favorite);
        binding.fabFavorito.setContentDescription(description);
        announceForAccessibility(description);
    }

    private void setupFabClick() {
        binding.fabFavorito.setOnClickListener(v -> {
            v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
            viewModel.toggleFavorito();
        });
    }

    private void announceForAccessibility(String message) {
        binding.getRoot().announceForAccessibility(message);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}