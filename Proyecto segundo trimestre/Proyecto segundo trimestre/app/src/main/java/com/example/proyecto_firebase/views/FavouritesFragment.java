// Definición del paquete donde se encuentra esta clase
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
import com.example.proyecto_firebase.databinding.FragmentFavouritesBinding; // View Binding para el layout
import com.example.proyecto_firebase.adapters.PeliculaAdapter; // Adaptador para la lista de películas
import com.example.proyecto_firebase.models.Pelicula; // Modelo de datos para películas
import com.example.proyecto_firebase.viewmodels.FavouritesViewModel; // ViewModel específico para favoritos
import com.example.proyecto_firebase.R; // Recursos de la aplicación
import java.util.ArrayList; // Para manejar colecciones de películas

// Clase del fragmento de favoritos que implementa el listener para clicks en películas
public class FavouritesFragment extends Fragment implements PeliculaAdapter.OnPeliculaClickListener {
    // Variables miembro de la clase
    private FragmentFavouritesBinding binding; // Objeto de binding para acceder a las vistas
    private FavouritesViewModel viewModel; // ViewModel para la lógica de negocio
    private PeliculaAdapter adapter; // Adaptador para manejar la lista de películas favoritas

    // Constructor vacío requerido para los fragmentos en Android
    public FavouritesFragment() {
        // Required empty public constructor
    }

    // Método llamado para crear la vista del fragmento
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el layout usando View Binding
        binding = FragmentFavouritesBinding.inflate(inflater, container, false);
        return binding.getRoot(); // Devolver la vista raíz del binding
    }

    // Método llamado después de que la vista ha sido creada
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Configurar la barra de acción con el título "Mis Favoritos"
        if (getActivity() != null && ((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Mis Favoritos");
        }

        // Inicializar el ViewModel mediante ViewModelProvider
        viewModel = new ViewModelProvider(this).get(FavouritesViewModel.class);

        // Configurar el RecyclerView para mostrar la lista de favoritos
        setupRecyclerView();

        // Establecer observadores para los LiveData del ViewModel
        setupObservers();

        // Solicitar la carga de películas favoritas (probablemente desde Firebase)
        viewModel.cargarFavoritos();
    }

    // Método para configurar el RecyclerView
    private void setupRecyclerView() {
        // Crear adaptador con lista vacía y este fragmento como listener
        adapter = new PeliculaAdapter(new ArrayList<>(), this);
        // Configurar layout manager para mostrar elementos en lista vertical
        binding.recyclerViewFavoritos.setLayoutManager(new LinearLayoutManager(requireContext()));
        // Asignar el adaptador al RecyclerView
        binding.recyclerViewFavoritos.setAdapter(adapter);
    }

    // Método para configurar los observadores de LiveData
    private void setupObservers() {
        // Observar cambios en la lista de películas favoritas
        viewModel.getFavoritos().observe(getViewLifecycleOwner(), favoritos -> {
            if (favoritos != null) {
                // Actualizar el adaptador con la nueva lista
                adapter.setPeliculas(favoritos);

                // Gestionar la visibilidad de componentes según si hay favoritos o no
                // Si la lista está vacía, ocultar RecyclerView y mostrar mensaje de estado vacío
                binding.recyclerViewFavoritos.setVisibility(favoritos.isEmpty() ? View.GONE : View.VISIBLE);
                binding.emptyStateContainer.setVisibility(favoritos.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });

        // Observar mensajes de error
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                // Mostrar mensaje de error mediante Toast
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        // Observar estado de carga
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            // Aquí podrías mostrar/ocultar un indicador de carga
        });
    }

    // Implementación del método de la interfaz OnPeliculaClickListener
    // Se ejecuta cuando el usuario hace clic en una película de la lista de favoritos
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