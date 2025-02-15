package com.example.proyecto_firebase.views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.proyecto_firebase.R;
import com.example.proyecto_firebase.viewmodels.LoginViewModel;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private EditText etCorreoL, etContrasenaL;
    private Button btnIngresarL;
    private TextView lblRegistrar;
    private ProgressDialog dialogoProgreso;
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide(); //Oculta el título de la aplicación
        setContentView(R.layout.login_main);
        inicializarVistas();
        configurarViewModel();
        configurarObservadores();
        configurarClickListeners();
        configurarAccesibilidad();
    }

    private void inicializarVistas() {
        etCorreoL = findViewById(R.id.etCorreoL);
        etContrasenaL = findViewById(R.id.etContrasenaL);
        btnIngresarL = findViewById(R.id.btnIngresarL);
        lblRegistrar = findViewById(R.id.lblRegistrar);

        // Inicializar DialogoProgreso
        dialogoProgreso = new ProgressDialog(this);
        dialogoProgreso.setMessage("Iniciando sesión...");
        dialogoProgreso.setCancelable(false);
    }

    private void configurarViewModel() {
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
    }

    private void configurarObservadores() {
        // Observar resultado del login
        loginViewModel.getLoginResult().observe(this, usuarioFirebase -> {
            dialogoProgreso.dismiss();
            if (usuarioFirebase != null) {
                // Login exitoso
                startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                finish();
            }
        });

        // Observar errores
        loginViewModel.getLoginError().observe(this, error -> {
            dialogoProgreso.dismiss();
            if (error != null) {
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                // Anunciar error para accesibilidad
                View rootView = findViewById(android.R.id.content);
                rootView.announceForAccessibility(error);
            }
        });

        // Observar estado de carga
        loginViewModel.getIsLoading().observe(this, estaCargando -> {
            if (estaCargando != null) {
                if (estaCargando) {
                    dialogoProgreso.show();
                    // Anunciar para accesibilidad
                    View rootView = findViewById(android.R.id.content);
                    rootView.announceForAccessibility("Iniciando sesión");
                } else {
                    dialogoProgreso.dismiss();
                }
            }
        });
    }

    private void configurarClickListeners() {
        btnIngresarL.setOnClickListener(v -> {
            // Añadir retroalimentación háptica
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            intentarLogin();
        });

        lblRegistrar.setOnClickListener(v -> {
            // Añadir retroalimentación háptica
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void configurarAccesibilidad() {
        // Configurar delegate de accesibilidad para el campo de usuario
        etCorreoL.setAccessibilityDelegate(new View.AccessibilityDelegate() {
            @Override
            public void onPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
                super.onPopulateAccessibilityEvent(host, event);
                if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
                    if (etCorreoL.getText().toString().isEmpty()) {
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
                if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
                    if (etContrasenaL.getText().toString().isEmpty()) {
                        host.announceForAccessibility(getString(R.string.login_empty_password));
                    }
                }
            }
        });

        // Configurar TraversalOrder para una navegación lógica
        etCorreoL.setAccessibilityTraversalAfter(R.id.imageView2);
        etContrasenaL.setAccessibilityTraversalAfter(R.id.etCorreoL);
        btnIngresarL.setAccessibilityTraversalAfter(R.id.etContrasenaL);
        lblRegistrar.setAccessibilityTraversalAfter(R.id.btnIngresarL);
    }

    private void intentarLogin() {
        String correo = etCorreoL.getText().toString().trim();
        String contrasena = etContrasenaL.getText().toString().trim();

        if (correo.isEmpty() || contrasena.isEmpty()) {
            String mensaje = getString(R.string.login_error_empty_fields);
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
            View rootView = findViewById(android.R.id.content);
            rootView.announceForAccessibility(mensaje);
            return;
        }

        loginViewModel.login(correo, contrasena);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialogoProgreso != null && dialogoProgreso.isShowing()) {
            dialogoProgreso.dismiss();
        }
    }
}