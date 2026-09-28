package co.edu.unilibre.paqueteria.util;

import co.edu.unilibre.paqueteria.exception.DatoInvalidoException;

public final class Validador {
    private Validador() { }

    public static String texto(String valor, String campo) throws DatoInvalidoException {
        if (valor == null || valor.isBlank()) {
            throw new DatoInvalidoException("El campo " + campo + " es obligatorio.");
        }
        return valor.trim();
    }

    public static double positivo(double valor, String campo) throws DatoInvalidoException {
        if (valor <= 0) {
            throw new DatoInvalidoException("El campo " + campo + " debe ser mayor que cero.");
        }
        return valor;
    }
}
