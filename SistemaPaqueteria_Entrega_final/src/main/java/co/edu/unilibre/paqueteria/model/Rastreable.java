package co.edu.unilibre.paqueteria.model;

public interface Rastreable {
    void actualizarEstado(EstadoEnvio nuevoEstado);
    String generarReporteRastreo();
}
