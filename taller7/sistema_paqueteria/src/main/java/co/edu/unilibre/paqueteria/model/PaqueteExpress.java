package co.edu.unilibre.paqueteria.model;

public class PaqueteExpress extends Envio {
    private static final double TARIFA_BASE = 18000.0;
    private static final double TARIFA_KG = 7500.0;
    private static final double RECARGO_EXPRESS = 12000.0;

    public PaqueteExpress(String codigo, String remitente, String destinatario,
                          String ciudadOrigen, String ciudadDestino,
                          double pesoKg, double valorDeclarado) {
        super(codigo, remitente, destinatario, ciudadOrigen, ciudadDestino, pesoKg, valorDeclarado);
    }

    @Override
    public double calcularCosto() {
        double seguro = getValorDeclarado() * 0.015;
        return TARIFA_BASE + RECARGO_EXPRESS + (Math.ceil(getPesoKg()) * TARIFA_KG) + seguro;
    }

    @Override
    public String getTipo() {
        return "EXPRESS";
    }
}
