// Definición del paquete donde se encuentra esta clase
package com.example.proyecto_firebase.views;

// Importaciones necesarias para la funcionalidad de la actividad
import android.app.ProgressDialog; // Para mostrar diálogo de progreso durante el registro
import android.content.Intent; // Para navegación entre actividades
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast; // Para mostrar mensajes temporales
import androidx.appcompat.app.AppCompatActivity; // Clase base para actividades
import androidx.lifecycle.ViewModelProvider; // Para la gestión del ViewModel
import com.example.proyecto_firebase.R; // Recursos de la aplicación
import com.example.proyecto_firebase.viewmodels.RegisterViewModel; // ViewModel específico para registro

import java.util.Objects;

// Clase principal de la actividad de registro
public class RegisterActivity extends AppCompatActivity {

    // Variables para los elementos de la interfaz
    private EditText etNombre, etApellido, etCorreo, etContrasena,
            etConfirmContrasena, etTelefono, etDirrecion;
    private Button btnRegistrar;
    private TextView lblLoginR;
    private ProgressDialog progressDialog; // Diálogo para mostrar progreso
    private RegisterViewModel viewModel; // ViewModel para la lógica de registro

    // Método llamado cuando se crea la actividad
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide(); // Oculta la barra de acción
        setContentView(R.layout.activity_registro); // Establece el layout de la actividad

        // Llamada a los métodos de inicialización
        inicializarVistas(); // Inicializar referencias a las vistas
        configurarViewModel(); // Configurar el ViewModel
        configurarObservadores(); // Establecer observadores para LiveData
        configurarClickListeners(); // Configurar escuchadores de clic
    }

    /**
     * Inicializa todas las vistas de la actividad
     */
    private void inicializarVistas() {
        // Enlazar las vistas con sus IDs del layout mediante findViewById
        etNombre = findViewById(R.id.etNombre);
        etApellido = findViewById(R.id.etApellido);
        etCorreo = findViewById(R.id.etCorreo);
        etContrasena = findViewById(R.id.etContrasena);
        etConfirmContrasena = findViewById(R.id.etConfirmContrasena);
        etTelefono = findViewById(R.id.etTelefono);
        etDirrecion = findViewById(R.id.etDirrecion);
        btnRegistrar = findViewById(R.id.btnRgistrar);
        lblLoginR = findViewById(R.id.lblLoginR);

        // Configurar el diálogo de progreso que se mostrará durante el registro
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Espere por favor..."); // Título del diálogo
        progressDialog.setCanceledOnTouchOutside(false); // No se puede cancelar tocando fuera
    }

    /**
     * Configura el ViewModel para la actividad
     */
    private void configurarViewModel() {
        // Obtener la instancia del ViewModel mediante ViewModelProvider
        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
    }

    /**
     * Configura los observadores para los cambios en el ViewModel
     */
    private void configurarObservadores() {
        // Observar el resultado del proceso de registro
        viewModel.obtenerResultadoRegistro().observe(this, resultado -> {
            if ("SUCCESS".equals(resultado)) {
                // Si el registro fue exitoso
                Toast.makeText(this, "Usuario Creado con Éxito", Toast.LENGTH_SHORT).show();
                // Navegar a la actividad principal
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                finish(); // Cerrar esta actividad para que no vuelva al presionar "atrás"
            } else {
                // Si hubo un error en el registro, mostrar el mensaje
                Toast.makeText(this, resultado, Toast.LENGTH_SHORT).show();
            }
        });

        // Observar errores de validación (campos incompletos, contraseñas no coinciden, etc.)
        viewModel.obtenerErrorValidacion().observe(this, error ->
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show());

        // Observar el estado de carga para mostrar/ocultar el diálogo de progreso
        viewModel.obtenerEstadoCarga().observe(this, cargando -> {
            if (cargando) {
                // Si está cargando, mostrar el diálogo
                progressDialog.setMessage("Registrando Usuario...");
                progressDialog.show();
            } else {
                // Si no está cargando, ocultar el diálogo
                progressDialog.dismiss();
            }
        });
    }

    /**
     * Configura los listeners para los eventos de click
     */
    private void configurarClickListeners() {
        // Configurar clic en el botón de registro
        btnRegistrar.setOnClickListener(v -> {
            // Obtener los valores de los campos de texto
            String nombre = etNombre.getText().toString();
            String apellido = etApellido.getText().toString();
            String correo = etCorreo.getText().toString();
            String contrasena = etContrasena.getText().toString();
            String confirmarContrasena = etConfirmContrasena.getText().toString();
            String telefono = etTelefono.getText().toString();
            String direccion = etDirrecion.getText().toString();

            // Llamar al método de registro en el ViewModel pasando todos los datos
            viewModel.registrarUsuario(nombre, apellido, correo, contrasena,
                    confirmarContrasena, telefono, direccion);
        });

        // Configurar clic en el texto para ir a la pantalla de login
        lblLoginR.setOnClickListener(view ->
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));
    }

    // Método llamado cuando la actividad está siendo destruida
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cerrar el diálogo de progreso si está abierto al destruir la actividad
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}