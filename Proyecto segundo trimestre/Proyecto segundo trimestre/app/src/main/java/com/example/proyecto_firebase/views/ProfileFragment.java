// Definición del paquete donde se encuentra esta clase
package com.example.proyecto_firebase.views;

// Importaciones necesarias para la funcionalidad del fragmento
import android.content.Context;
import android.content.SharedPreferences; // Para guardar preferencias de usuario
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast; // Para mostrar mensajes temporales
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate; // Para cambiar entre modos claro/oscuro
import androidx.fragment.app.Fragment; // Clase base para fragmentos
import com.example.proyecto_firebase.databinding.FragmentProfileBinding; // View Binding para el layout
import com.google.firebase.auth.AuthCredential; // Para reautenticar al usuario
import com.google.firebase.auth.EmailAuthProvider; // Proveedor de autenticación por email
import com.google.firebase.auth.FirebaseAuth; // Para autenticación de Firebase
import com.google.firebase.auth.FirebaseUser; // Modelo de usuario de Firebase

// Clase del fragmento de perfil de usuario
public class ProfileFragment extends Fragment {
    // Variable miembro para el binding
    private FragmentProfileBinding binding; // Objeto de binding para acceder a las vistas

    // Constructor vacío requerido para los fragmentos
    public ProfileFragment() {
        // Required empty public constructor
    }

    // Método llamado para crear la vista del fragmento
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el layout usando View Binding
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot(); // Devolver la vista raíz del binding
    }

    // Método llamado después de que la vista ha sido creada
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Configurar el título en la barra de acción como "Perfil"
        if (getActivity() != null && ((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Perfil");
        }

        // Configurar el estado inicial del switch de modo oscuro según preferencias
        setupInitialState();

        // Configurar listeners para los elementos interactivos
        setupListeners();
    }

    // Método para configurar el estado inicial del switch de modo oscuro
    private void setupInitialState() {
        // Obtener preferencias guardadas
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
        // Leer si el modo oscuro está activado (falso por defecto)
        boolean isDarkMode = prefs.getBoolean("darkMode", false);
        // Establecer el switch según el valor guardado
        binding.darkModeSwitch.setChecked(isDarkMode);
    }

    // Método para configurar los listeners de los elementos interactivos
    private void setupListeners() {
        // Listener para el botón de cambio de contraseña
        binding.changePasswordButton.setOnClickListener(v -> changePassword());

        // Listener para el switch de modo oscuro
        binding.darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                toggleDarkMode(isChecked));
    }

    // Método para cambiar la contraseña del usuario
    private void changePassword() {
        // Obtener textos de los campos de contraseña
        String currentPass = binding.currentPasswordEditText.getText().toString();
        String newPass = binding.newPasswordEditText.getText().toString();

        // Validar que los campos no estén vacíos
        if (currentPass.isEmpty() || newPass.isEmpty()) {
            Toast.makeText(requireContext(),
                    "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener el usuario actual de Firebase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getEmail() != null) {
            // Crear credencial para reautenticar al usuario
            AuthCredential credential = EmailAuthProvider
                    .getCredential(user.getEmail(), currentPass);

            // Reautenticar usuario para verificar que la contraseña actual es correcta
            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Si la reautenticación es exitosa, actualizar contraseña
                    user.updatePassword(newPass).addOnCompleteListener(updateTask -> {
                        if (updateTask.isSuccessful()) {
                            // Si el cambio es exitoso, mostrar mensaje y limpiar campos
                            Toast.makeText(requireContext(),
                                    "Contraseña cambiada con éxito", Toast.LENGTH_SHORT).show();
                            clearPasswordFields();
                        } else {
                            // Si hay error al cambiar, mostrar mensaje
                            Toast.makeText(requireContext(),
                                    "Error al cambiar la contraseña", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Si la reautenticación falla, la contraseña actual es incorrecta
                    Toast.makeText(requireContext(),
                            "La contraseña actual no es correcta", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Método para activar/desactivar el modo oscuro
    private void toggleDarkMode(boolean enableDarkMode) {
        // Guardar la preferencia del usuario
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
        prefs.edit().putBoolean("darkMode", enableDarkMode).apply();

        // Aplicar el tema correspondiente en toda la aplicación
        AppCompatDelegate.setDefaultNightMode(
                enableDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        // Recrear la actividad para aplicar los cambios de tema
        requireActivity().recreate();
    }

    // Método para limpiar los campos de contraseña
    private void clearPasswordFields() {
        binding.currentPasswordEditText.setText("");
        binding.newPasswordEditText.setText("");
    }

    // Método llamado cuando la vista del fragmento está siendo destruida
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Liberar el binding para evitar fugas de memoria
    }
}