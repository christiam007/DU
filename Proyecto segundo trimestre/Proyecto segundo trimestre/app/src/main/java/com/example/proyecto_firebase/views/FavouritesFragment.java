package com.example.proyecto_firebase.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.proyecto_firebase.databinding.FragmentFavouritesBinding;
import com.example.proyecto_firebase.adapters.PeliculaAdapter;
import com.example.proyecto_firebase.models.Pelicula;
import com.example.proyecto_firebase.viewmodels.FavouritesViewModel;
import com.example.proyecto_firebase.R;
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

        if (getActivity() != null && ((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Mis Favoritos");
        }

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
                // Mostrar u ocultar el estado vacío
                binding.recyclerViewFavoritos.setVisibility(favoritos.isEmpty() ? View.GONE : View.VISIBLE);
                binding.emptyStateContainer.setVisibility(favoritos.isEmpty() ? View.VISIBLE : View.GONE);
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
        DetailFragment detailFragment = new DetailFragment();

        Bundle bundle = new Bundle();
        bundle.putString("id", pelicula.getId());
        bundle.putString("titulo", pelicula.getTitulo());
        bundle.putString("descripcion", pelicula.getDescripcion());
        bundle.putString("imagen", pelicula.getImagen());

        detailFragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, detailFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}