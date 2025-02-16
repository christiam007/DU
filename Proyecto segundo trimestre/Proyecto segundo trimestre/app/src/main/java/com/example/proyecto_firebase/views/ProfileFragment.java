package com.example.proyecto_firebase.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.proyecto_firebase.databinding.FragmentProfileBinding;
import com.example.proyecto_firebase.utils.ThemeHelper;
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

        // Configurar estado inicial del switch de tema
        binding.switchTheme.setChecked(ThemeHelper.isDarkMode(requireContext()));

        // Mostrar email del usuario
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            binding.tvUserEmail.setText(user.getEmail());
        }

        // Configurar listeners
        setupListeners();
    }

    private void setupListeners() {
        // Switch de tema
        binding.switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ThemeHelper.setDarkMode(requireContext(), isChecked);
            requireActivity().recreate();
        });

        // Botón de cambiar contraseña
        binding.btnChangePassword.setOnClickListener(v -> {
            String newPassword = binding.etNewPassword.getText().toString();
            if (!newPassword.isEmpty() && newPassword.length() >= 6) {
                changePassword(newPassword);
            } else {
                Toast.makeText(requireContext(),
                        "La contraseña debe tener al menos 6 caracteres",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changePassword(String newPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.updatePassword(newPassword)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(requireContext(),
                                "Contraseña actualizada",
                                Toast.LENGTH_SHORT).show();
                        binding.etNewPassword.setText("");
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(requireContext(),
                                    "Error al actualizar contraseña",
                                    Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}