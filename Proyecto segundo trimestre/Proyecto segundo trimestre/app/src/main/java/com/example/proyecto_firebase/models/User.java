// Definición del paquete donde se encuentra esta clase
package com.example.proyecto_firebase.models;

// Importaciones necesarias
import java.util.HashMap;
import java.util.Map;

// Clase modelo para representar un usuario en la aplicación
public class User {
    // Atributos de la clase
    private String uid;         // Identificador único del usuario (proporcionado por Firebase Auth)
    private String nombre;      // Nombre del usuario
    private String apellido;    // Apellido del usuario
    private String correo;      // Correo electrónico (usado para autenticación)
    private String contrasena;  // Contraseña (usada solo para autenticación, no se almacena en la BD)
    private String telefono;    // Número de teléfono del usuario
    private String direccion;   // Dirección física del usuario

    // Constructor vacío (necesario para Firebase)
    // Firebase requiere este constructor para deserializar objetos
    public User() {
    }

    // Constructor con todos los campos para crear un usuario completo
    public User(String uid, String nombre, String apellido, String correo,
                String contrasena, String telefono, String direccion) {
        this.uid = uid;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.contrasena = contrasena;
        this.telefono = telefono;
        this.direccion = direccion;
    }

    // Constructor simplificado para operaciones de login
    // Solo necesita correo y contraseña para autenticarse
    public User(String correo, String contrasena) {
        this.correo = correo;
        this.contrasena = contrasena;
    }

    // Getters y Setters - Métodos para acceder y modificar los atributos

    // Obtener el UID
    public String getUid() {
        return uid;
    }

    // Establecer el UID
    public void setUid(String uid) {
        this.uid = uid;
    }

    // Obtener el nombre
    public String getNombre() {
        return nombre;
    }

    // Establecer el nombre
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // Obtener el apellido
    public String getApellido() {
        return apellido;
    }

    // Establecer el apellido
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    // Obtener el correo
    public String getCorreo() {
        return correo;
    }

    // Establecer el correo
    public void setCorreo(String correo) {
        this.correo = correo;
    }

    // Obtener la contraseña
    public String getContrasena() {
        return contrasena;
    }

    // Establecer la contraseña
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    // Obtener el teléfono
    public String getTelefono() {
        return telefono;
    }

    // Establecer el teléfono
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    // Obtener la dirección
    public String getDireccion() {
        return direccion;
    }

    // Establecer la dirección
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    // Método para convertir el objeto a un Map (necesario para Firebase)
    // Esto facilita almacenar el usuario en la base de datos
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("nombre", nombre);
        result.put("apellido", apellido);
        result.put("correo", correo);
        result.put("telefono", telefono);
        result.put("direccion", direccion);

        // Nota: la contraseña no se incluye en el mapa por razones de seguridad

        return result;
    }
}