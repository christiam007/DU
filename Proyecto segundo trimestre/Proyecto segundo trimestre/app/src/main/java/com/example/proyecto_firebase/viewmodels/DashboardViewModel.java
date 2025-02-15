package com.example.proyecto_firebase.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.proyecto_firebase.models.Pelicula;
import com.example.proyecto_firebase.repositories.DashboardRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class DashboardViewModel extends ViewModel {
    private DashboardRepository dashboardRepository;
    private MutableLiveData<Boolean> navigateToLogin;
    private MutableLiveData<List<Pelicula>> peliculas;
    private MutableLiveData<String> error;
    private MutableLiveData<Boolean> isLoading;

    public DashboardViewModel() {
        dashboardRepository = new DashboardRepository();
        navigateToLogin = new MutableLiveData<>();
        peliculas = new MutableLiveData<>(new ArrayList<>());
        error = new MutableLiveData<>();
        isLoading = new MutableLiveData<>(false);
    }

    public LiveData<Boolean> getNavigateToLogin() {
        return navigateToLogin;
    }

    public LiveData<List<Pelicula>> getPeliculas() {
        return peliculas;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void cerrarSesion() {
        dashboardRepository.signOut();
        navigateToLogin.setValue(true);
    }

    public void cargarPeliculas() {
        isLoading.setValue(true);
        dashboardRepository.getPeliculas(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Pelicula> listaPeliculas = new ArrayList<>();
                try {
                    for (DataSnapshot peliculaSnapshot : dataSnapshot.getChildren()) {
                        String titulo = peliculaSnapshot.child("titulo").getValue(String.class);
                        String descripcion = peliculaSnapshot.child("descripcion").getValue(String.class);
                        String imagen = peliculaSnapshot.child("imagen").getValue(String.class);

                        Pelicula pelicula = new Pelicula();
                        pelicula.setTitulo(titulo);
                        pelicula.setDescripcion(descripcion);
                        pelicula.setImagen(imagen);
                        pelicula.generateId();

                        listaPeliculas.add(pelicula);
                    }
                    peliculas.setValue(listaPeliculas);
                } catch (Exception e) {
                    error.setValue("Error al procesar los datos: " + e.getMessage());
                } finally {
                    isLoading.setValue(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                error.setValue("Error al cargar las películas: " + databaseError.getMessage());
                isLoading.setValue(false);
            }
        });
    }
}