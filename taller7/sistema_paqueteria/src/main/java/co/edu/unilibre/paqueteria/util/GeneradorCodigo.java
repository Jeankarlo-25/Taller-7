package co.edu.unilibre.paqueteria.util;

public class GeneradorCodigo {
    private static int secuencia = 1000;

    public static String generar() {
        return "PK-" + secuencia++;
    }
}
