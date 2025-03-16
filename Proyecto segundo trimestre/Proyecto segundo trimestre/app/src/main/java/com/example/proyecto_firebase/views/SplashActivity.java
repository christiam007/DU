// Definición del paquete donde se encuentra esta clase
package com.example.proyecto_firebase.views;

// Importaciones necesarias para la funcionalidad de la actividad
import android.content.Intent; // Para navegación entre actividades
import android.os.Bundle;
import android.os.Handler; // Para crear retrasos en la ejecución
import androidx.appcompat.app.AppCompatActivity; // Clase base para actividades
import androidx.appcompat.app.AppCompatDelegate; // Para gestionar el tema de la aplicación
import com.example.proyecto_firebase.R; // Recursos de la aplicación
import com.example.proyecto_firebase.utils.ThemeHelper; // Clase auxiliar para gestionar el tema

// Clase de la actividad de bienvenida (splash screen)
public class SplashActivity extends AppCompatActivity {

    // Método llamado cuando se crea la actividad
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Aplicar el tema oscuro antes de crear la actividad si está configurado
        if (ThemeHelper.isDarkMode(this)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        // Llamar al método onCreate de la clase padre
        super.onCreate(savedInstanceState);
        // Establecer el layout de la actividad
        setContentView(R.layout.activity_splash);

        // Crear un retraso de 3 segundos (3000 ms) antes de continuar
        new Handler().postDelayed(() -> {
            // Iniciar la actividad de login
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            // Finalizar esta actividad para que no vuelva al presionar "atrás"
            finish();
        }, 3000);
    }
}