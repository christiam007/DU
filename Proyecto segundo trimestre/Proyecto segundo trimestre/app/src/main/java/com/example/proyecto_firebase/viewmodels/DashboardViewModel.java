// DashboardViewModel.java
package com.example.proyecto_firebase.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.proyecto_firebase.models.Pelicula;
import com.example.proyecto_firebase.repositories.DashboardRepository;

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

    // Método actualizado para cargar películas no favoritas
    public void cargarPeliculas() {
        isLoading.setValue(true);

        // Usar el nuevo método del repositorio
        dashboardRepository.getPeliculasNoFavoritas(new DashboardRepository.OnPeliculasListener() {
            @Override
            public void onPeliculasLoaded(List<Pelicula> listaPeliculas) {
                peliculas.setValue(listaPeliculas);
                isLoading.setValue(false);
            }

            @Override
            public void onError(String mensaje) {
                error.setValue(mensaje);
                isLoading.setValue(false);
            }
        });
    }
}