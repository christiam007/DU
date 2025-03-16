// Definición del paquete donde se encuentra esta clase
package com.example.proyecto_firebase.views;

// Importaciones necesarias para la funcionalidad de la actividad
import android.content.Context; // Para acceder a SharedPreferences
import android.content.Intent; // Para navegar entre actividades
import android.content.SharedPreferences; // Para manipular SharedPreferences
import android.os.Bundle;
import androidx.appcompat.app.ActionBarDrawerToggle; // Para manejar el icono del menú lateral
import androidx.appcompat.app.AppCompatActivity; // Clase base para actividades
import androidx.appcompat.widget.Toolbar; // Barra de herramientas moderna
import androidx.databinding.DataBindingUtil; // Para Data Binding
import androidx.fragment.app.Fragment; // Para manejar fragmentos
import com.example.proyecto_firebase.R; // Recursos de la aplicación
import com.example.proyecto_firebase.databinding.ActivityMainBinding; // Binding generado para el layout
import com.example.proyecto_firebase.repositories.PeliculaRepository; // Repositorio para películas
import com.google.firebase.auth.FirebaseAuth; // Autenticación de Firebase
import android.widget.Toast; // Para mostrar mensajes al usuario

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

    // Método para verificar la autenticación cuando la actividad comienza
    @Override
    protected void onStart() {
        super.onStart();

        // Verificar si el usuario está autenticado en Firebase
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Si no hay usuario autenticado, eliminar de SharedPreferences y redirigir a login
            SharedPreferences sharedPref = getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
            sharedPref.edit().remove("userId").apply();

            startActivity(new Intent(this, LoginActivity.class));
            finish();
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

        // Eliminar usuario de SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
        sharedPref.edit().remove("userId").apply();

        // Crear intent para ir a la pantalla de login
        Intent intent = new Intent(this, LoginActivity.class);
        // Iniciar la actividad de login
        startActivity(intent);
        // Finalizar esta actividad para que no se pueda volver atrás
        finish();
    }

    // Método para limpiar todos los favoritos
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
}