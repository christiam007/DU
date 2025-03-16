// Definición del paquete donde se encuentra esta clase
package com.example.proyecto_firebase.views;

// Importaciones necesarias para la funcionalidad del fragmento
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast; // Para mostrar mensajes temporales
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment; // Clase base para fragmentos
import androidx.lifecycle.ViewModelProvider; // Para la gestión del ViewModel
import com.bumptech.glide.Glide; // Biblioteca para cargar imágenes eficientemente
import com.example.proyecto_firebase.R; // Recursos de la aplicación
import com.example.proyecto_firebase.databinding.FragmentDetailBinding; // View Binding para el layout
import com.example.proyecto_firebase.models.Pelicula; // Modelo de datos para películas
import com.example.proyecto_firebase.viewmodels.DetailViewModel; // ViewModel específico para esta pantalla

// Clase del fragmento de detalle que muestra información específica de una película
public class DetailFragment extends Fragment {
    // Variables miembro de la clase
    private FragmentDetailBinding binding; // Objeto de binding para acceder a las vistas
    private DetailViewModel viewModel; // ViewModel para la lógica de negocio
    private Pelicula pelicula; // Objeto que contiene los datos de la película

    // Constructor vacío requerido para los fragmentos en Android
    public DetailFragment() {
        // Required empty public constructor
    }

    // Método llamado para crear la vista del fragmento
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el layout usando View Binding
        binding = FragmentDetailBinding.inflate(inflater, container, false);
        return binding.getRoot(); // Devolver la vista raíz del binding
    }

    // Método llamado después de que la vista ha sido creada
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Configurar la barra de acción con el título "Detalles"
        if (getActivity() != null && ((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Detalles");
        }

        // Inicializar el ViewModel mediante ViewModelProvider
        viewModel = new ViewModelProvider(this).get(DetailViewModel.class);

        // Recuperar los datos de la película pasados como argumentos
        if (getArguments() != null) {
            // Crear un nuevo objeto Película y establecer sus propiedades
            pelicula = new Pelicula();
            pelicula.setId(getArguments().getString("id"));
            pelicula.setTitulo(getArguments().getString("titulo"));
            pelicula.setDescripcion(getArguments().getString("descripcion"));
            pelicula.setImagen(getArguments().getString("imagen"));

            // Establecer el objeto película en el binding para data binding
            binding.setPelicula(pelicula);

            // Configurar elementos de accesibilidad para usuarios con discapacidades
            setupAccessibility();

            // Cargar la imagen de la película usando Glide
            loadImage();

            // Informar al ViewModel sobre qué película estamos viendo para verificar si es favorita
            viewModel.setPeliculaId(pelicula.getId());
        }

        // Configurar observadores para los LiveData del ViewModel
        setupObservers();

        // Configurar el comportamiento del botón flotante (FAB)
        setupFabClick();
    }

    // Método para configurar la accesibilidad de los elementos de la UI
    private void setupAccessibility() {
        // Establecer descripción de contenido para la imagen (usado por lectores de pantalla)
        binding.ivPeliculaDetalle.setContentDescription(
                "Imagen detallada de " + pelicula.getTitulo());

        // Establecer descripción para el título
        binding.tvTituloDetalle.setContentDescription(
                "Título: " + pelicula.getTitulo());

        // Establecer descripción para la descripción
        binding.tvDescripcionDetalle.setContentDescription(
                "Descripción: " + pelicula.getDescripcion());
    }

    // Método para cargar la imagen de la película usando Glide
    private void loadImage() {
        // Verificar que la URL de la imagen no sea nula o vacía
        if (pelicula.getImagen() != null && !pelicula.getImagen().isEmpty()) {
            // Usar Glide para cargar la imagen desde la URL
            Glide.with(requireContext())
                    .load(pelicula.getImagen())
                    .centerCrop() // Ajustar la imagen al centro y recortar si es necesario
                    .into(binding.ivPeliculaDetalle); // Asignar al ImageView
        }
    }

    // Método para configurar los observadores de LiveData
    private void setupObservers() {
        // Observar cambios en el estado de favorito de la película
        viewModel.getEsFavorito().observe(getViewLifecycleOwner(), isFavorite -> {
            // Actualizar icono del FAB según estado de favorito
            updateFabIcon(isFavorite);
            // Actualizar información de accesibilidad del FAB
            updateFabAccessibility(isFavorite);
        });

        // Observar mensajes de error
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                // Mostrar mensaje de error en un Toast
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                // Anunciar el error para lectores de pantalla
                announceForAccessibility("Error: " + error);
            }
        });
    }

    // Método para actualizar el icono del FAB según el estado de favorito
    private void updateFabIcon(boolean isFavorite) {
        // Cambiar el icono: corazón lleno si es favorito, contorno si no lo es
        binding.fabFavorito.setImageResource(
                isFavorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_border
        );
    }

    // Método para actualizar la accesibilidad del FAB según el estado de favorito
    private void updateFabAccessibility(boolean isFavorite) {
        // Obtener la descripción adecuada desde los recursos de strings
        String description = isFavorite ?
                getString(R.string.fab_remove_favorite) : // "Quitar de favoritos"
                getString(R.string.fab_add_favorite);     // "Añadir a favoritos"

        // Establecer la descripción para lectores de pantalla
        binding.fabFavorito.setContentDescription(description);
        // Anunciar el cambio para lectores de pantalla
        announceForAccessibility(description);
    }

    // Método para configurar el comportamiento del botón de favorito
    private void setupFabClick() {
        // Asignar listener de clic al FAB
        binding.fabFavorito.setOnClickListener(v -> {
            // Proporcionar retroalimentación táctil (vibración) al presionar
            v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
            // Alternar el estado de favorito de la película en el ViewModel
            viewModel.toggleFavorito(pelicula);
        });
    }

    // Método auxiliar para anunciar mensajes para accesibilidad
    private void announceForAccessibility(String message) {
        // Hacer que el lector de pantalla anuncie el mensaje
        binding.getRoot().announceForAccessibility(message);
    }

    // Método llamado cuando la vista del fragmento está siendo destruida
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Liberar el binding para evitar fugas de memoria
    }
}