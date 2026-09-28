package co.edu.unilibre.paqueteria.exception;

public class CodigoDuplicadoException extends PaqueteriaException {
    public CodigoDuplicadoException(String codigo) {
        super("Ya existe un envio con el codigo: " + codigo);
    }
}
