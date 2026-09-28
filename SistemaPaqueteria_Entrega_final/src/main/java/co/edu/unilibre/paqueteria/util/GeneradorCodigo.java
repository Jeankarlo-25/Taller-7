package co.edu.unilibre.paqueteria.util;

import java.util.concurrent.atomic.AtomicInteger;

public final class GeneradorCodigo {
    private static final AtomicInteger SECUENCIA = new AtomicInteger(1000);

    private GeneradorCodigo() { }

    public static String generar() {
        return "PK-" + SECUENCIA.getAndIncrement();
    }
}
