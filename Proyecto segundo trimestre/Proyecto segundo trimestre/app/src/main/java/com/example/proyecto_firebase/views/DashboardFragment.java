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
import com.example.proyecto_firebase.databinding.FragmentDashboardBinding;
import com.example.proyecto_firebase.adapters.PeliculaAdapter;
import com.example.proyecto_firebase.models.Pelicula;
import com.example.proyecto_firebase.utils.ThemeHelper;
import com.example.proyecto_firebase.viewmodels.DashboardViewModel;

import java.util.ArrayList;

public class DashboardFragment extends Fragment implements PeliculaAdapter.OnPeliculaClickListener {
    private FragmentDashboardBinding binding;
    private DashboardViewModel dashboardViewModel;
    private PeliculaAdapter peliculaAdapter;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
        binding.switchTheme.setChecked(ThemeHelper.isDarkMode(requireContext()));
        binding.switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ThemeHelper.setDarkMode(requireContext(), isChecked);
            requireActivity().recreate();
        });
    }

    private void setupRecyclerView() {
        peliculaAdapter = new PeliculaAdapter(new ArrayList<>(), this);
        binding.recyclerViewPeliculas.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewPeliculas.setAdapter(peliculaAdapter);
    }

    private void observeViewModel() {
        dashboardViewModel.getPeliculas().observe(getViewLifecycleOwner(), peliculas -> {
            if (peliculas != null) {
                peliculaAdapter.setPeliculas(peliculas);
            }
        });

        dashboardViewModel.getNavigateToLogin().observe(getViewLifecycleOwner(), shouldNavigate -> {
            if (shouldNavigate) {
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_dashboardFragment_to_loginActivity);
                requireActivity().finish();
            }
        });

        dashboardViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupListeners() {
        binding.btnCerrarSesion.setOnClickListener(v -> {
            dashboardViewModel.cerrarSesion();
            Toast.makeText(requireContext(),
                    "Cerraste Sesión Exitosamente", Toast.LENGTH_SHORT).show();
        });

        binding.btnFavoritos.setOnClickListener(v -> {
            Navigation.findNavController(v)
                    .navigate(R.id.action_dashboardFragment_to_favouritesFragment);
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
                .navigate(R.id.action_dashboardFragment_to_detailFragment, bundle);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}