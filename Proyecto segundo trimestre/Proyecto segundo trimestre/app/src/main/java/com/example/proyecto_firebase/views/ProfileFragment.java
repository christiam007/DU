package com.example.proyecto_firebase.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import com.example.proyecto_firebase.databinding.FragmentProfileBinding;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Configurar estado inicial del switch
        setupInitialState();

        // Configurar listeners
        setupListeners();
    }

    private void setupInitialState() {
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("darkMode", false);
        binding.darkModeSwitch.setChecked(isDarkMode);
    }

    private void setupListeners() {
        // Listener para cambio de contraseña
        binding.changePasswordButton.setOnClickListener(v -> changePassword());

        // Listener para modo oscuro
        binding.darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                toggleDarkMode(isChecked));
    }

    private void changePassword() {
        String currentPass = binding.currentPasswordEditText.getText().toString();
        String newPass = binding.newPasswordEditText.getText().toString();

        if (currentPass.isEmpty() || newPass.isEmpty()) {
            Toast.makeText(requireContext(),
                    "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getEmail() != null) {
            // Reautenticar usuario
            AuthCredential credential = EmailAuthProvider
                    .getCredential(user.getEmail(), currentPass);

            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Actualizar contraseña
                    user.updatePassword(newPass).addOnCompleteListener(updateTask -> {
                        if (updateTask.isSuccessful()) {
                            Toast.makeText(requireContext(),
                                    "Contraseña cambiada con éxito", Toast.LENGTH_SHORT).show();
                            clearPasswordFields();
                        } else {
                            Toast.makeText(requireContext(),
                                    "Error al cambiar la contraseña", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(requireContext(),
                            "La contraseña actual no es correcta", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void toggleDarkMode(boolean enableDarkMode) {
        // Guardar preferencia
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
        prefs.edit().putBoolean("darkMode", enableDarkMode).apply();

        // Aplicar tema
        AppCompatDelegate.setDefaultNightMode(
                enableDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        // Recrear activity para aplicar cambios
        requireActivity().recreate();
    }

    private void clearPasswordFields() {
        binding.currentPasswordEditText.setText("");
        binding.newPasswordEditText.setText("");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}