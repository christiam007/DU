// Definición del paquete donde se encuentra esta clase
package com.example.proyecto_firebase.views;

// Importaciones necesarias para la funcionalidad de la actividad
import android.app.ProgressDialog; // Para mostrar un diálogo de progreso durante el login
import android.content.Intent; // Para navegar entre actividades
import android.os.Bundle;
import android.view.HapticFeedbackConstants; // Para retroalimentación táctil (vibración)
import android.view.View;
import android.view.accessibility.AccessibilityEvent; // Para eventos de accesibilidad
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast; // Para mostrar mensajes temporales
import androidx.appcompat.app.AppCompatActivity; // Clase base para actividades
import androidx.lifecycle.ViewModelProvider; // Para la gestión del ViewModel
import com.example.proyecto_firebase.R; // Recursos de la aplicación
import com.example.proyecto_firebase.viewmodels.LoginViewModel; // ViewModel específico para el login

import java.util.Objects;

// Clase principal de la actividad de inicio de sesión
public class LoginActivity extends AppCompatActivity {
    // Variables miembro para los elementos de la UI
    private EditText etCorreoL, etContrasenaL; // Campos para correo y contraseña
    private Button btnIngresarL; // Botón de inicio de sesión
    private TextView lblRegistrar; // Texto clickeable para ir a registro
    private ProgressDialog dialogoProgreso; // Diálogo para mostrar progreso durante el login
    private LoginViewModel loginViewModel; // ViewModel para la lógica de negocio del login

    // Método llamado cuando se crea la actividad
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main); // Establecer el layout de la actividad
        inicializarVistas(); // Inicializar referencias a las vistas
        configurarViewModel(); // Configurar el ViewModel
        configurarObservadores(); // Establecer observadores para LiveData
        configurarClickListeners(); // Configurar escuchadores de clic
        configurarAccesibilidad(); // Configurar características de accesibilidad
    }

    // Método para inicializar las referencias a las vistas y el diálogo de progreso
    private void inicializarVistas() {
        // Obtener referencias a los elementos de la UI mediante findViewById
        etCorreoL = findViewById(R.id.etCorreoL);
        etContrasenaL = findViewById(R.id.etContrasenaL);
        btnIngresarL = findViewById(R.id.btnIngresarL);
        lblRegistrar = findViewById(R.id.lblRegistrar);

        // Inicializar el diálogo de progreso
        dialogoProgreso = new ProgressDialog(this);
        dialogoProgreso.setMessage("Iniciando sesión..."); // Texto a mostrar
        dialogoProgreso.setCancelable(false); // No permitir cancelar tocando fuera
    }

    // Método para inicializar el ViewModel
    private void configurarViewModel() {
        // Obtener instancia del LoginViewModel mediante ViewModelProvider
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
    }

    // Método para configurar los observadores a los LiveData del ViewModel
    private void configurarObservadores() {
        // Observar el resultado del login
        loginViewModel.getLoginResult().observe(this, usuarioFirebase -> {
            dialogoProgreso.dismiss(); // Ocultar diálogo de progreso
            if (usuarioFirebase != null) {
                // Si el login fue exitoso, navegar a MainActivity
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish(); // Cerrar esta actividad para que no vuelva al presionar "atrás"
            }
        });

        // Observar errores de login
        loginViewModel.getLoginError().observe(this, error -> {
            dialogoProgreso.dismiss(); // Ocultar diálogo de progreso
            if (error != null) {
                // Mostrar mensaje de error
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                // Anunciar error para accesibilidad (lectores de pantalla)
                View rootView = findViewById(android.R.id.content);
                rootView.announceForAccessibility(error);
            }
        });

        // Observar estado de carga
        loginViewModel.getIsLoading().observe(this, estaCargando -> {
            if (estaCargando != null) {
                if (estaCargando) {
                    // Si está cargando, mostrar diálogo y anunciar para accesibilidad
                    dialogoProgreso.show();
                    View rootView = findViewById(android.R.id.content);
                    rootView.announceForAccessibility("Iniciando sesión");
                } else {
                    // Si no está cargando, ocultar diálogo
                    dialogoProgreso.dismiss();
                }
            }
        });
    }

    // Método para configurar los escuchadores de clic en los botones
    private void configurarClickListeners() {
        // Configurar clic en botón de login
        btnIngresarL.setOnClickListener(v -> {
            // Proporcionar retroalimentación táctil (vibración) al presionar
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            intentarLogin(); // Llamar al método que intenta iniciar sesión
        });

        // Configurar clic en texto para ir a pantalla de registro
        lblRegistrar.setOnClickListener(v -> {
            // Proporcionar retroalimentación táctil (vibración) al presionar
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            // Navegar a la actividad de registro
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    // Método para configurar características de accesibilidad
    private void configurarAccesibilidad() {
        // Configurar delegate de accesibilidad para el campo de correo
        etCorreoL.setAccessibilityDelegate(new View.AccessibilityDelegate() {
            @Override
            public void onPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
                super.onPopulateAccessibilityEvent(host, event);
                // Si se detecta un cambio en el texto
                if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
                    // Y si el campo está vacío
                    if (etCorreoL.getText().toString().isEmpty()) {
                        // Anunciar para lectores de pantalla que el campo está vacío
                        host.announceForAccessibility(getString(R.string.login_empty_user));
                    }
                }
            }
        });

        // Configurar delegate de accesibilidad para el campo de contraseña
        etContrasenaL.setAccessibilityDelegate(new View.AccessibilityDelegate() {
            @Override
            public void onPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
                super.onPopulateAccessibilityEvent(host, event);
                // Si se detecta un cambio en el texto
                if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
                    // Y si el campo está vacío
                    if (etContrasenaL.getText().toString().isEmpty()) {
                        // Anunciar para lectores de pantalla que el campo está vacío
                        host.announceForAccessibility(getString(R.string.login_empty_password));
                    }
                }
            }
        });

        // Configurar orden de navegación por teclado o gestos de accesibilidad
        // Esto define el orden en que se navegará entre elementos al usar Tab o gestos
        etCorreoL.setAccessibilityTraversalAfter(R.id.imageView2);
        etContrasenaL.setAccessibilityTraversalAfter(R.id.etCorreoL);
        btnIngresarL.setAccessibilityTraversalAfter(R.id.etContrasenaL);
        lblRegistrar.setAccessibilityTraversalAfter(R.id.btnIngresarL);
    }

    // Método para intentar el inicio de sesión
    private void intentarLogin() {
        // Obtener y limpiar los textos ingresados
        String correo = etCorreoL.getText().toString().trim();
        String contrasena = etContrasenaL.getText().toString().trim();

        // Validar que los campos no estén vacíos
        if (correo.isEmpty() || contrasena.isEmpty()) {
            // Si están vacíos, mostrar mensaje de error
            String mensaje = getString(R.string.login_error_empty_fields);
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
            // Anunciar el error para lectores de pantalla
            View rootView = findViewById(android.R.id.content);
            rootView.announceForAccessibility(mensaje);
            return; // Salir del método sin intentar login
        }

        // Si los campos están completos, intentar login a través del ViewModel
        loginViewModel.login(correo, contrasena);
    }

    // Método llamado cuando la actividad está siendo destruida
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Asegurarse de descartar el diálogo si está mostrándose para evitar fugas de memoria
        if (dialogoProgreso != null && dialogoProgreso.isShowing()) {
            dialogoProgreso.dismiss();
        }
    }
}