package co.edu.unilibre.paqueteria.model;

public class PaqueteEstandar extends Envio {
    private static final double TARIFA_BASE = 9000.0;
    private static final double TARIFA_KG = 5000.0;

    public PaqueteEstandar(String codigo, String remitente, String destinatario,
                           String ciudadOrigen, String ciudadDestino,
                           double pesoKg, double valorDeclarado) {
        super(codigo, remitente, destinatario, ciudadOrigen, ciudadDestino, pesoKg, valorDeclarado);
    }

    @Override
    public double calcularCosto() {
        double seguro = getValorDeclarado() * 0.01;
        return TARIFA_BASE + (Math.ceil(getPesoKg()) * TARIFA_KG) + seguro;
    }

    @Override
    public String getTipo() {
        return "ESTANDAR";
    }
}
