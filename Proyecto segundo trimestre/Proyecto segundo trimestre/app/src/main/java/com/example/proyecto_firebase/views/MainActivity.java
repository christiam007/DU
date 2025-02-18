package com.example.proyecto_firebase.views;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import com.example.proyecto_firebase.R;
import com.example.proyecto_firebase.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // Configurar Toolbar
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        // Configurar DrawerToggle
        toggle = new ActionBarDrawerToggle(
                this,
                binding.drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Configurar el listener del Navigation Drawer
        setupDrawerListener();

        // Cargar el fragment inicial
        if (savedInstanceState == null) {
            openFragment(new DashboardFragment());
        }
    }

    private void setupDrawerListener() {
        binding.navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_dashboard) {
                openFragment(new DashboardFragment());
            } else if (id == R.id.nav_favourites) {
                openFragment(new FavouritesFragment());
            } else if (id == R.id.nav_profile) {
                openFragment(new ProfileFragment());
            } else if (id == R.id.nav_logout) {
                logoutUser();
            }

            binding.drawerLayout.closeDrawers();
            return true;
        });
    }

    private void openFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}