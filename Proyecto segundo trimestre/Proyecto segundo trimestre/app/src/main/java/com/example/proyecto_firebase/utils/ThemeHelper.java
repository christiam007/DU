// Definición del paquete donde se encuentra esta clase
package com.example.proyecto_firebase.utils;

// Importaciones necesarias para la funcionalidad
import android.content.Context; // Para acceso a recursos y SharedPreferences
import android.content.SharedPreferences; // Para almacenar preferencias de usuario
import androidx.appcompat.app.AppCompatDelegate; // Para manejar el tema de la aplicación

// Clase utilitaria para gestionar el tema (claro/oscuro) de la aplicación
public class ThemeHelper {
    // Constantes para el almacenamiento de preferencias
    private static final String PREFERENCES_NAME = "theme_prefs"; // Nombre del archivo de preferencias
    private static final String KEY_DARK_MODE = "dark_mode"; // Clave para la preferencia de modo oscuro

    // Método para establecer el modo oscuro en la aplicación
    public static void setDarkMode(Context context, boolean isDarkMode) {
        // Obtener el objeto SharedPreferences para guardar la configuración
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        // Guardar la preferencia de modo oscuro
        prefs.edit().putBoolean(KEY_DARK_MODE, isDarkMode).apply();

        // Aplicar el tema correspondiente a nivel de aplicación
        AppCompatDelegate.setDefaultNightMode(
                isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    // Método para verificar si el modo oscuro está activado
    public static boolean isDarkMode(Context context) {
        // Obtener el objeto SharedPreferences para leer la configuración
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        // Leer y devolver la preferencia de modo oscuro (falso por defecto)
        return prefs.getBoolean(KEY_DARK_MODE, false);
    }
}