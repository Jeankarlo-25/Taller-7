package co.edu.unilibre.paqueteria.exception;

public class EnvioNoEncontradoException extends PaqueteriaException {
    public EnvioNoEncontradoException(String codigo) {
        super("No se encontro el envio con codigo: " + codigo);
    }
}
