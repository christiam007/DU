// Definición del paquete donde se encuentra este archivo
package com.example.proyecto_firebase.views;

// Importaciones necesarias para la funcionalidad del fragmento
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast; // Para mostrar mensajes temporales al usuario
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment; // Clase base para fragmentos
import androidx.lifecycle.ViewModelProvider; // Para la gestión del ViewModel
import androidx.recyclerview.widget.LinearLayoutManager; // Para organizar elementos en lista vertical

// Importaciones específicas del proyecto
import com.example.proyecto_firebase.databinding.FragmentDashboardBinding; // View Binding para el layout
import com.example.proyecto_firebase.adapters.PeliculaAdapter; // Adaptador para la lista de películas
import com.example.proyecto_firebase.models.Pelicula; // Modelo de datos para películas
import com.example.proyecto_firebase.viewmodels.DashboardViewModel; // ViewModel para la lógica de negocio
import com.example.proyecto_firebase.R; // Recursos de la aplicación

import java.util.ArrayList; // Para manejar colecciones de películas

// Definición de la clase del fragmento de Dashboard que implementa el listener para clicks en películas
public class DashboardFragment extends Fragment implements PeliculaAdapter.OnPeliculaClickListener {
    // Variables miembro de la clase
    private FragmentDashboardBinding binding; // Objeto de binding para acceder a las vistas
    private DashboardViewModel dashboardViewModel; // ViewModel que contiene la lógica y datos
    private PeliculaAdapter peliculaAdapter; // Adaptador para manejar la lista de películas

    // Constructor vacío requerido para los fragmentos en Android
    public DashboardFragment() {
        // Required empty public constructor
    }

    // Método llamado para crear la vista del fragmento
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el layout usando View Binding
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot(); // Devolver la vista raíz del binding
    }

    // Método llamado después de que la vista ha sido creada
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Configurar la barra de acción con el título "Explorar Películas" (cambiado de "Dashboard")
        if (getActivity() != null && ((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Explorar Películas");
        }

        // Inicializar el ViewModel mediante ViewModelProvider
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        // Configurar el RecyclerView llamando al método auxiliar
        setupRecyclerView();

        // Establecer observadores para los LiveData del ViewModel
        observeViewModel();

        // Solicitar la carga de películas
        dashboardViewModel.cargarPeliculas();
    }

    // Método llamado cuando el fragmento se hace visible al usuario
    @Override
    public void onResume() {
        super.onResume();
        // Recargar películas cada vez que el fragmento se hace visible
        // Esto asegura que después de eliminar favoritos la lista se actualice
        dashboardViewModel.cargarPeliculas();
    }

    // Método para configurar el RecyclerView
    private void setupRecyclerView() {
        // Crear adaptador con lista vacía y este fragmento como listener
        peliculaAdapter = new PeliculaAdapter(new ArrayList<>(), this);
        // Configurar layout manager para mostrar elementos en lista vertical
        binding.recyclerViewPeliculas.setLayoutManager(new LinearLayoutManager(requireContext()));
        // Asignar el adaptador al RecyclerView
        binding.recyclerViewPeliculas.setAdapter(peliculaAdapter);
    }

    // Método para observar los cambios en el ViewModel
    private void observeViewModel() {
        // Observar cambios en la lista de películas
        dashboardViewModel.getPeliculas().observe(getViewLifecycleOwner(), peliculas -> {
            if (peliculas != null) {
                // Actualizar el adaptador con la nueva lista
                peliculaAdapter.setPeliculas(peliculas);

                // Gestionar la visibilidad de componentes según si hay películas o no
                boolean isEmpty = peliculas.isEmpty();
                binding.recyclerViewPeliculas.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
                binding.emptyStateContainer.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            }
        });

        // Observar mensajes de error
        dashboardViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                // Mostrar mensaje de error mediante Toast
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
            }
        });

        // Observar estado de carga
        dashboardViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            // Aquí puedes mostrar u ocultar un indicador de carga si lo tienes
        });
    }

    // Implementación del método de la interfaz OnPeliculaClickListener
    // Se ejecuta cuando el usuario hace clic en una película de la lista
    @Override
    public void onPeliculaClick(Pelicula pelicula) {
        // Crear una nueva instancia del fragmento de detalles
        DetailFragment detailFragment = new DetailFragment();

        // Crear un bundle para pasar datos a través de fragmentos
        Bundle bundle = new Bundle();
        // Añadir información de la película al bundle
        bundle.putString("id", pelicula.getId());
        bundle.putString("titulo", pelicula.getTitulo());
        bundle.putString("descripcion", pelicula.getDescripcion());
        bundle.putString("imagen", pelicula.getImagen());

        // Asignar el bundle como argumentos al fragmento de detalles
        detailFragment.setArguments(bundle);

        // Realizar la transacción para reemplazar el fragmento actual con el de detalles
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, detailFragment)
                .addToBackStack(null) // Permite volver atrás con el botón de retroceso
                .commit();
    }

    // Método llamado cuando la vista del fragmento está siendo destruida
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Liberar el binding para evitar fugas de memoria
    }
}