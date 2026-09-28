package co.edu.unilibre.paqueteria.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public abstract class Envio implements Exportable, Rastreable {
    private final String codigo;
    private final String remitente;
    private final String destinatario;
    private final String ciudadOrigen;
    private final String ciudadDestino;
    private final double pesoKg;
    private final double valorDeclarado;
    private EstadoEnvio estado;
    private final LocalDateTime fechaRegistro;
    private final List<String> historialEstados = new ArrayList<>();

    protected Envio(String codigo, String remitente, String destinatario,
                    String ciudadOrigen, String ciudadDestino,
                    double pesoKg, double valorDeclarado) {
        this.codigo = codigo;
        this.remitente = remitente;
        this.destinatario = destinatario;
        this.ciudadOrigen = ciudadOrigen;
        this.ciudadDestino = ciudadDestino;
        this.pesoKg = pesoKg;
        this.valorDeclarado = valorDeclarado;
        this.estado = EstadoEnvio.REGISTRADO;
        this.fechaRegistro = LocalDateTime.now();
        historialEstados.add(describirEvento(EstadoEnvio.REGISTRADO));
    }

    public abstract double calcularCosto();

    public String getCodigo() { return codigo; }
    public String getRemitente() { return remitente; }
    public String getDestinatario() { return destinatario; }
    public String getCiudadOrigen() { return ciudadOrigen; }
    public String getCiudadDestino() { return ciudadDestino; }
    public double getPesoKg() { return pesoKg; }
    public double getValorDeclarado() { return valorDeclarado; }
    public EstadoEnvio getEstado() { return estado; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public List<String> getHistorialEstados() { return new ArrayList<>(historialEstados); }

    @Override
    public void actualizarEstado(EstadoEnvio nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El nuevo estado no puede ser nulo.");
        }
        if (estado == EstadoEnvio.CANCELADO || estado == EstadoEnvio.ENTREGADO) {
            throw new IllegalStateException("No se puede cambiar el estado de un envio cerrado.");
        }
        if (nuevoEstado == EstadoEnvio.REGISTRADO && estado != EstadoEnvio.REGISTRADO) {
            throw new IllegalStateException("No se puede regresar al estado REGISTRADO.");
        }
        this.estado = nuevoEstado;
        historialEstados.add(describirEvento(nuevoEstado));
    }

    private String describirEvento(EstadoEnvio nuevoEstado) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                + " - " + nuevoEstado;
    }

    @Override
    public String generarReporteRastreo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Codigo: ").append(codigo).append('\n');
        sb.append("Tipo: ").append(getTipo()).append('\n');
        sb.append("Ruta: ").append(ciudadOrigen).append(" -> ").append(ciudadDestino).append('\n');
        sb.append("Estado actual: ").append(estado).append('\n');
        sb.append("Historial:\n");
        for (String evento : historialEstados) {
            sb.append("  * ").append(evento).append('\n');
        }
        return sb.toString();
    }

    public abstract String getTipo();

    @Override
    public String aCSV() {
        return String.join(";",
                codigo,
                getTipo(),
                limpiar(remitente),
                limpiar(destinatario),
                limpiar(ciudadOrigen),
                limpiar(ciudadDestino),
                String.format(java.util.Locale.US, "%.2f", pesoKg),
                String.format(java.util.Locale.US, "%.2f", valorDeclarado),
                estado.name(),
                String.format(java.util.Locale.US, "%.2f", calcularCosto()),
                fechaRegistro.toString());
    }

    private String limpiar(String valor) {
        return valor.replace(';', ',').replace('\n', ' ');
    }

    @Override
    public String toString() {
        return codigo + " | " + getTipo() + " | " + remitente + " -> " + destinatario
                + " | " + ciudadOrigen + " -> " + ciudadDestino
                + " | " + estado + " | $" + String.format(java.util.Locale.US, "%.2f", calcularCosto());
    }
}
