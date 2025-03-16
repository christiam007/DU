// Definición del paquete donde se encuentra esta clase
package com.example.proyecto_firebase.views;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import com.example.proyecto_firebase.R;
import com.example.proyecto_firebase.databinding.ActivityMainBinding;
import com.example.proyecto_firebase.repositories.PeliculaRepository;
import com.google.firebase.auth.FirebaseAuth;

// Clase principal de la actividad que actúa como contenedor para los fragmentos
public class MainActivity extends AppCompatActivity {

    // Variables miembro
    private ActivityMainBinding binding; // Objeto de binding para acceder a las vistas
    private ActionBarDrawerToggle toggle; // Controla el icono de menú y su animación

    // Método llamado cuando se crea la actividad
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflar el layout usando Data Binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // Configurar la barra de herramientas como ActionBar
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        // Configurar el DrawerToggle (icono de hamburguesa para abrir/cerrar el drawer)
        toggle = new ActionBarDrawerToggle(
                this, // Contexto de la actividad
                binding.drawerLayout, // Referencia al DrawerLayout
                toolbar, // Referencia a la Toolbar
                R.string.navigation_drawer_open, // String para anunciar "Drawer abierto" (accesibilidad)
                R.string.navigation_drawer_close // String para anunciar "Drawer cerrado" (accesibilidad)
        );
        // Añadir el toggle como listener del DrawerLayout
        binding.drawerLayout.addDrawerListener(toggle);
        // Sincronizar el estado del toggle (importante después de restaurar estado)
        toggle.syncState();

        // Configurar el listener para los elementos del menú lateral
        setupDrawerListener();

        // Cargar el fragmento inicial solo si es una creación nueva (no una recreación)
        if (savedInstanceState == null) {
            openFragment(new DashboardFragment());
        }
    }

    // Método para configurar el listener del Navigation Drawer
    private void setupDrawerListener() {
        // Asignar un listener a los clics en elementos del menú
        binding.navigationView.setNavigationItemSelectedListener(item -> {
            // Obtener el ID del elemento seleccionado
            int id = item.getItemId();

            // Determinar qué acción tomar según el elemento seleccionado
            if (id == R.id.nav_dashboard) {
                // Abrir fragmento de Dashboard
                openFragment(new DashboardFragment());
            } else if (id == R.id.nav_favourites) {
                // Abrir fragmento de Favoritos
                openFragment(new FavouritesFragment());
            } else if (id == R.id.nav_profile) {
                // Abrir fragmento de Perfil
                openFragment(new ProfileFragment());
            } else if (id == R.id.nav_clear_favourites) {
                // Limpiar todos los favoritos
                limpiarTodosFavoritos();
            } else if (id == R.id.nav_logout) {
                // Cerrar sesión
                logoutUser();
            }

            // Cerrar el drawer después de seleccionar una opción
            binding.drawerLayout.closeDrawers();
            // Devolver true para indicar que el evento fue manejado
            return true;
        });
    }

    // Nuevo método para limpiar todos los favoritos
    private void limpiarTodosFavoritos() {
        // Crear instancia del repositorio
        PeliculaRepository peliculaRepository = new PeliculaRepository();

        // Mostrar diálogo de confirmación
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Limpiar Favoritos")
                .setMessage("¿Estás seguro de que deseas eliminar todos tus favoritos?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    // Eliminar todos los favoritos
                    peliculaRepository.eliminarTodosFavoritos(task -> {
                        if (task.isSuccessful()) {
                            // Mostrar mensaje de éxito
                            Toast.makeText(this, "Todos los favoritos eliminados", Toast.LENGTH_SHORT).show();

                            // Cargar el fragmento de Dashboard con todas las películas
                            openFragment(new DashboardFragment());
                        } else {
                            // Mostrar mensaje de error si hay uno
                            String errorMsg = task.getException() != null ?
                                    task.getException().getMessage() :
                                    "Error desconocido";
                            Toast.makeText(this, "Error al eliminar favoritos: " + errorMsg,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("No", null)
                .show();
    }

    // Método para abrir un fragmento en el contenedor
    private void openFragment(Fragment fragment) {
        // Iniciar una transacción de fragmento
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment) // Reemplazar el contenido actual
                .commit(); // Confirmar la transacción
    }

    // Método para cerrar la sesión del usuario
    private void logoutUser() {
        // Cerrar sesión en Firebase Auth
        FirebaseAuth.getInstance().signOut();
        // Crear intent para ir a la pantalla de login
        Intent intent = new Intent(this, LoginActivity.class);
        // Iniciar la actividad de login
        startActivity(intent);
        // Finalizar esta actividad para que no se pueda volver atrás
        finish();
    }
}