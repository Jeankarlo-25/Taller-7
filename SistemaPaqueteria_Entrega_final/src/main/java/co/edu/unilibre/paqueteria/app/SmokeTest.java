package co.edu.unilibre.paqueteria.app;

import co.edu.unilibre.paqueteria.model.EstadoEnvio;
import co.edu.unilibre.paqueteria.model.Envio;
import co.edu.unilibre.paqueteria.service.SistemaPaqueteriaService;

import java.nio.file.Files;
import java.nio.file.Path;

public final class SmokeTest {
    private SmokeTest() { }

    public static void main(String[] args) throws Exception {
        SistemaPaqueteriaService service = new SistemaPaqueteriaService();
        Envio envio = service.registrarEnvio(false, "Remitente", "Destinatario", "Bogota", "Medellin", 2.0, 100000);
        assert service.cantidadEnvios() == 1;
        assert service.buscarPorCodigo(envio.getCodigo()) == envio;
        service.actualizarEstado(envio.getCodigo(), EstadoEnvio.EN_TRANSITO);
        service.actualizarEstado(envio.getCodigo(), EstadoEnvio.EN_CENTRO_DISTRIBUCION);
        service.actualizarEstado(envio.getCodigo(), EstadoEnvio.EN_REPARTO);
        service.actualizarEstado(envio.getCodigo(), EstadoEnvio.ENTREGADO);
        assert envio.getEstado() == EstadoEnvio.ENTREGADO;

        Path temp = Files.createTempFile("paqueteria-smoke", ".csv");
        service.exportarCSV(temp);
        assert Files.size(temp) > 0;
        Files.deleteIfExists(temp);
        System.out.println("SMOKE TEST OK: registro, rastreo, transiciones y exportacion CSV funcionando.");
    }
}
