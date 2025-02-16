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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.proyecto_firebase.R;
import com.example.proyecto_firebase.databinding.FragmentFavouritesBinding;
import com.example.proyecto_firebase.adapters.PeliculaAdapter;
import com.example.proyecto_firebase.models.Pelicula;
import com.example.proyecto_firebase.viewmodels.FavouritesViewModel;

import java.util.ArrayList;

public class FavouritesFragment extends Fragment implements PeliculaAdapter.OnPeliculaClickListener {
    private FragmentFavouritesBinding binding;
    private FavouritesViewModel viewModel;
    private PeliculaAdapter adapter;

    public FavouritesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFavouritesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializar ViewModel
        viewModel = new ViewModelProvider(this).get(FavouritesViewModel.class);

        setupRecyclerView();
        setupObservers();

        // Cargar favoritos
        viewModel.cargarFavoritos();
    }

    private void setupRecyclerView() {
        adapter = new PeliculaAdapter(new ArrayList<>(), this);
        binding.recyclerViewFavoritos.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewFavoritos.setAdapter(adapter);
    }

    private void setupObservers() {
        viewModel.getFavoritos().observe(getViewLifecycleOwner(), favoritos -> {
            if (favoritos != null) {
                adapter.setPeliculas(favoritos);
                if (favoritos.isEmpty()) {
                    Toast.makeText(requireContext(),
                            "No tienes películas favoritas", Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            // Aquí podrías mostrar/ocultar un indicador de carga
        });
    }

    @Override
    public void onPeliculaClick(Pelicula pelicula) {
        Bundle bundle = new Bundle();
        bundle.putString("id", pelicula.getId());
        bundle.putString("titulo", pelicula.getTitulo());
        bundle.putString("descripcion", pelicula.getDescripcion());
        bundle.putString("imagen", pelicula.getImagen());

        Navigation.findNavController(requireView())
                .navigate(R.id.action_favouritesFragment_to_detailFragment, bundle);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}