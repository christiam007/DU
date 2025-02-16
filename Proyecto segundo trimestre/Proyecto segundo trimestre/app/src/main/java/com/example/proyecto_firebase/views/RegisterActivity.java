package com.example.proyecto_firebase.views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.proyecto_firebase.R;
import com.example.proyecto_firebase.viewmodels.RegisterViewModel;

import java.util.Objects;


public class RegisterActivity extends AppCompatActivity {

    // Variables para los elementos de la interfaz
    private EditText etNombre, etApellido, etCorreo, etContrasena,
            etConfirmContrasena, etTelefono, etDirrecion;
    private Button btnRegistrar;
    private TextView lblLoginR;
    private ProgressDialog progressDialog;
    private RegisterViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide(); //Oculta el título de la aplicación
        setContentView(R.layout.activity_registro);

        // Llamada a los métodos de inicialización
        inicializarVistas();
        configurarViewModel();
        configurarObservadores();
        configurarClickListeners();
    }

    /**
     * Inicializa todas las vistas de la actividad
     */
    private void inicializarVistas() {
        // Enlazar las vistas con sus IDs del layout
        etNombre = findViewById(R.id.etNombre);
        etApellido = findViewById(R.id.etApellido);
        etCorreo = findViewById(R.id.etCorreo);
        etContrasena = findViewById(R.id.etContrasena);
        etConfirmContrasena = findViewById(R.id.etConfirmContrasena);
        etTelefono = findViewById(R.id.etTelefono);
        etDirrecion = findViewById(R.id.etDirrecion);
        btnRegistrar = findViewById(R.id.btnRgistrar);
        lblLoginR = findViewById(R.id.lblLoginR);

        // Configurar el diálogo de progreso
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Espere por favor...");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    /**
     * Configura el ViewModel para la actividad
     */
    private void configurarViewModel() {
        // Obtener la instancia del ViewModel
        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
    }

    /**
     * Configura los observadores para los cambios en el ViewModel
     */
    private void configurarObservadores() {
        // Observar el resultado del registro
        viewModel.obtenerResultadoRegistro().observe(this, resultado -> {
            if ("SUCCESS".equals(resultado)) {
                Toast.makeText(this, "Usuario Creado con Éxito", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, resultado, Toast.LENGTH_SHORT).show();
            }
        });

        // Observar errores de validación
        viewModel.obtenerErrorValidacion().observe(this, error ->
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show());

        // Observar el estado de carga
        viewModel.obtenerEstadoCarga().observe(this, cargando -> {
            if (cargando) {
                progressDialog.setMessage("Registrando Usuario...");
                progressDialog.show();
            } else {
                progressDialog.dismiss();
            }
        });
    }

    /**
     * Configura los listeners para los eventos de click
     */
    private void configurarClickListeners() {
        // Click en el botón de registro
        btnRegistrar.setOnClickListener(v -> {
            // Obtener los valores de los campos
            String nombre = etNombre.getText().toString();
            String apellido = etApellido.getText().toString();
            String correo = etCorreo.getText().toString();
            String contrasena = etContrasena.getText().toString();
            String confirmarContrasena = etConfirmContrasena.getText().toString();
            String telefono = etTelefono.getText().toString();
            String direccion = etDirrecion.getText().toString();

            // Llamar al método de registro en el ViewModel
            viewModel.registrarUsuario(nombre, apellido, correo, contrasena,
                    confirmarContrasena, telefono, direccion);
        });

        // Click en el enlace para ir al login
        lblLoginR.setOnClickListener(view ->
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cerrar el diálogo de progreso si está abierto al destruir la actividad
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}