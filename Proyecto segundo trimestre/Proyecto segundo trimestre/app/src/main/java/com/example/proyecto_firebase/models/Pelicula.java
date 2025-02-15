package com.example.proyecto_firebase.models;

public class Pelicula {
    private String id;
    private String titulo;
    private String descripcion;
    private String imagen;
    private boolean favorite;

    // Constructor vacío (requerido para Firebase)
    public Pelicula() {
    }

    // Constructor con parámetros
    public Pelicula(String titulo, String descripcion, String imagen) {
        this.id = titulo.replace(" ", "_").toLowerCase(); // Generamos el ID a partir del título
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.imagen = imagen;
        this.favorite = false; // Por defecto no es favorito
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    // Método para generar el ID basado en el título
    public void generateId() {
        if (this.titulo != null) {
            this.id = this.titulo.replace(" ", "_").toLowerCase();
        }
    }
}