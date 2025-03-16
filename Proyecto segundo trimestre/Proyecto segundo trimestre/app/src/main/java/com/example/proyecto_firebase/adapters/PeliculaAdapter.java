// Definición del paquete donde se encuentra esta clase
package com.example.proyecto_firebase.adapters;

// Importaciones necesarias para la funcionalidad
import android.view.HapticFeedbackConstants; // Para retroalimentación táctil (vibración)
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil; // Para el uso de Data Binding
import androidx.recyclerview.widget.RecyclerView; // Base para adaptadores de listas
import com.example.proyecto_firebase.R; // Recursos de la aplicación
import com.example.proyecto_firebase.databinding.ItemPeliculaBinding; // Binding generado para el layout del ítem
import com.example.proyecto_firebase.models.Pelicula; // Modelo de datos para películas
import com.bumptech.glide.Glide; // Biblioteca para cargar imágenes eficientemente
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions; // Para transiciones en Glide
import java.util.List;

// Adaptador para mostrar lista de películas en un RecyclerView
public class PeliculaAdapter extends RecyclerView.Adapter<PeliculaAdapter.PeliculaViewHolder> {
    private List<Pelicula> peliculas; // Lista de películas a mostrar
    private OnPeliculaClickListener clickListener; // Listener para manejar clics en ítems

    // Interfaz para comunicar clics al fragmento/actividad
    public interface OnPeliculaClickListener {
        void onPeliculaClick(Pelicula pelicula);
    }

    // Constructor del adaptador
    public PeliculaAdapter(List<Pelicula> peliculas, OnPeliculaClickListener listener) {
        this.peliculas = peliculas;
        this.clickListener = listener;
    }

    // Método para actualizar la lista de películas
    public void setPeliculas(List<Pelicula> peliculas) {
        this.peliculas = peliculas;
        notifyDataSetChanged(); // Notificar al RecyclerView que debe actualizarse
    }

    // Método llamado cuando se necesita crear un nuevo ViewHolder
    @NonNull
    @Override
    public PeliculaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el layout del ítem usando Data Binding
        ItemPeliculaBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_pelicula, // Layout del ítem
                parent,
                false
        );
        return new PeliculaViewHolder(binding); // Crear y devolver nuevo ViewHolder
    }

    // Método llamado para vincular datos a un ViewHolder existente
    @Override
    public void onBindViewHolder(@NonNull PeliculaViewHolder holder, int position) {
        // Obtener la película en la posición actual
        Pelicula pelicula = peliculas.get(position);
        // Vincular la película al ViewHolder
        holder.bind(pelicula, clickListener);
    }

    // Método para obtener el número total de ítems
    @Override
    public int getItemCount() {
        return peliculas != null ? peliculas.size() : 0;
    }

    // Clase ViewHolder que mantiene las referencias a las vistas de cada ítem
    static class PeliculaViewHolder extends RecyclerView.ViewHolder {
        private final ItemPeliculaBinding binding; // Binding para el layout del ítem

        // Constructor del ViewHolder
        public PeliculaViewHolder(@NonNull ItemPeliculaBinding binding) {
            super(binding.getRoot()); // Pasar vista raíz al constructor de la superclase
            this.binding = binding; // Guardar referencia al binding
        }

        // Método para vincular una película al ViewHolder
        public void bind(Pelicula pelicula, OnPeliculaClickListener listener) {
            // Establecer la película en el binding (para data binding automático)
            binding.setPelicula(pelicula);

            // Configurar descripción de contenido para accesibilidad del ítem completo
            binding.getRoot().setContentDescription(
                    "Película " + pelicula.getTitulo() + ". " +
                            pelicula.getDescripcion() + ". Toca dos veces para ver detalles.");

            // Configurar accesibilidad para la imagen
            binding.ivPelicula.setContentDescription("Portada de la película " + pelicula.getTitulo());
            binding.ivPelicula.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);

            // Configurar accesibilidad para los textos (importante para lectores de pantalla)
            binding.tvTitulo.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
            binding.tvDescripcion.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);

            // Hacer el ítem focuseable para TalkBack y otros servicios de accesibilidad
            binding.getRoot().setFocusable(true);
            binding.getRoot().setClickable(true);

            // Configurar listener de clic con retroalimentación táctil
            binding.getRoot().setOnClickListener(v -> {
                // Proporcionar retroalimentación táctil (vibración) al presionar
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                // Notificar el clic si hay un listener
                if (listener != null) {
                    listener.onPeliculaClick(pelicula);
                }
            });

            // Cargar imagen con Glide (biblioteca eficiente para imágenes)
            Glide.with(binding.getRoot().getContext())
                    .load(pelicula.getImagen()) // URL o recurso de la imagen
                    .centerCrop() // Escalar y recortar la imagen
                    .transition(DrawableTransitionOptions.withCrossFade()) // Añadir transición suave
                    .into(binding.ivPelicula); // ImageView destino

            // Aplicar vinculaciones pendientes inmediatamente
            binding.executePendingBindings();
        }
    }
}