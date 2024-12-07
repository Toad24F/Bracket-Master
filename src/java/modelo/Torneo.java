package modelo;

public class Torneo {
    private int id;
    private String nombre;
    private String juego;
    private String creador;

    // Constructor vacío (opcional, pero puede ser útil)
    public Torneo() {}

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getJuego() { return juego; }
    public void setJuego(String juego) { this.juego = juego; }

    public String getCreador() { return creador; }
    public void setCreador(String creador) { this.creador = creador; }
}

