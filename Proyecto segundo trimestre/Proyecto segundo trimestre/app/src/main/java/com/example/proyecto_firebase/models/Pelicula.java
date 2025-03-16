// Definición del paquete donde se encuentra esta clase
package com.example.proyecto_firebase.models;

// Clase modelo para representar una película
public class Pelicula {
    // Atributos de la clase
    private String id;          // Identificador único de la película
    private String titulo;      // Título de la película
    private String descripcion; // Descripción o sinopsis de la película
    private String imagen;      // URL de la imagen de portada
    private boolean favorite;   // Indica si la película está marcada como favorita

    // Constructor vacío (requerido para Firebase)
    // Firebase necesita un constructor sin parámetros para deserializar objetos
    public Pelicula() {
    }

    // Constructor con parámetros para crear una película con valores iniciales
    public Pelicula(String titulo, String descripcion, String imagen) {
        this.id = titulo.replace(" ", "_").toLowerCase(); // Generamos el ID a partir del título
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.imagen = imagen;
        this.favorite = false; // Por defecto no es favorito
    }

    // Getters y Setters - Métodos para acceder y modificar los atributos

    // Obtener el ID
    public String getId() {
        return id;
    }

    // Establecer el ID
    public void setId(String id) {
        this.id = id;
    }

    // Obtener el título
    public String getTitulo() {
        return titulo;
    }

    // Establecer el título
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    // Obtener la descripción
    public String getDescripcion() {
        return descripcion;
    }

    // Establecer la descripción
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    // Obtener la URL de la imagen
    public String getImagen() {
        return imagen;
    }

    // Establecer la URL de la imagen
    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    // Verificar si es favorito (nótese el prefijo 'is' para booleanos)
    public boolean isFavorite() {
        return favorite;
    }

    // Establecer si es favorito
    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    // Método para generar el ID basado en el título
    // Útil cuando se crea una película sin ID específico
    public void generateId() {
        if (this.titulo != null) {
            this.id = this.titulo.replace(" ", "_").toLowerCase();
        }
    }
}