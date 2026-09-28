package co.edu.unilibre.paqueteria.service;

import co.edu.unilibre.paqueteria.exception.CodigoDuplicadoException;
import co.edu.unilibre.paqueteria.exception.DatoInvalidoException;
import co.edu.unilibre.paqueteria.exception.EnvioNoEncontradoException;
import co.edu.unilibre.paqueteria.exception.EstadoInvalidoException;
import co.edu.unilibre.paqueteria.exception.PaqueteriaException;
import co.edu.unilibre.paqueteria.model.EstadoEnvio;
import co.edu.unilibre.paqueteria.model.Envio;
import co.edu.unilibre.paqueteria.model.PaqueteEstandar;
import co.edu.unilibre.paqueteria.model.PaqueteExpress;
import co.edu.unilibre.paqueteria.util.GeneradorCodigo;
import co.edu.unilibre.paqueteria.util.Validador;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Capa de servicio: concentra las reglas del sistema para que la interfaz
 * grafica no manipule directamente las colecciones.
 */
public class SistemaPaqueteriaService {
    private final Map<String, Envio> enviosPorCodigo = new LinkedHashMap<>();
    private final List<Envio> historialEnvios = new ArrayList<>();
    private final Set<String> ciudadesDestino = new HashSet<>();
    private final Queue<String> colaDespacho = new ArrayDeque<>();

    public Envio registrarEnvio(boolean express, String remitente, String destinatario,
                                String origen, String destino, double peso, double valor)
            throws PaqueteriaException {
        Validador.texto(remitente, "remitente");
        Validador.texto(destinatario, "destinatario");
        Validador.texto(origen, "ciudad de origen");
        Validador.texto(destino, "ciudad de destino");
        Validador.positivo(peso, "peso");
        Validador.positivo(valor, "valor declarado");

        String codigo = GeneradorCodigo.generar();
        if (enviosPorCodigo.containsKey(codigo)) {
            throw new CodigoDuplicadoException(codigo);
        }

        Envio envio;
        if (express) {
            envio = new PaqueteExpress(codigo, remitente.trim(), destinatario.trim(),
                    origen.trim(), destino.trim(), peso, valor);
        } else {
            envio = new PaqueteEstandar(codigo, remitente.trim(), destinatario.trim(),
                    origen.trim(), destino.trim(), peso, valor);
        }

        enviosPorCodigo.put(codigo, envio);
        historialEnvios.add(envio);
        ciudadesDestino.add(destino.trim().toUpperCase());
        colaDespacho.offer(codigo);
        return envio;
    }

    public Envio buscarPorCodigo(String codigo) throws EnvioNoEncontradoException {
        if (codigo == null || codigo.isBlank()) {
            throw new EnvioNoEncontradoException(String.valueOf(codigo));
        }
        Envio envio = enviosPorCodigo.get(codigo.trim().toUpperCase());
        if (envio == null) {
            throw new EnvioNoEncontradoException(codigo);
        }
        return envio;
    }

    public void actualizarEstado(String codigo, EstadoEnvio nuevoEstado) throws PaqueteriaException {
        Envio envio = buscarPorCodigo(codigo);
        try {
            validarTransicion(envio.getEstado(), nuevoEstado);
            envio.actualizarEstado(nuevoEstado);
            if (nuevoEstado == EstadoEnvio.CANCELADO || nuevoEstado == EstadoEnvio.ENTREGADO) {
                colaDespacho.remove(envio.getCodigo());
            }
        } catch (IllegalStateException | IllegalArgumentException e) {
            throw new EstadoInvalidoException(e.getMessage());
        }
    }

    private void validarTransicion(EstadoEnvio actual, EstadoEnvio nuevo) throws EstadoInvalidoException {
        if (nuevo == null) {
            throw new EstadoInvalidoException("El estado no puede ser nulo.");
        }
        if (actual == EstadoEnvio.REGISTRADO &&
                nuevo != EstadoEnvio.EN_TRANSITO && nuevo != EstadoEnvio.CANCELADO) {
            throw new EstadoInvalidoException("Desde REGISTRADO solo puede pasar a EN_TRANSITO o CANCELADO.");
        }
        if (actual == EstadoEnvio.EN_TRANSITO &&
                nuevo != EstadoEnvio.EN_CENTRO_DISTRIBUCION && nuevo != EstadoEnvio.CANCELADO) {
            throw new EstadoInvalidoException("Desde EN_TRANSITO solo puede pasar a EN_CENTRO_DISTRIBUCION o CANCELADO.");
        }
        if (actual == EstadoEnvio.EN_CENTRO_DISTRIBUCION &&
                nuevo != EstadoEnvio.EN_REPARTO && nuevo != EstadoEnvio.CANCELADO) {
            throw new EstadoInvalidoException("Desde EN_CENTRO_DISTRIBUCION solo puede pasar a EN_REPARTO o CANCELADO.");
        }
        if (actual == EstadoEnvio.EN_REPARTO && nuevo != EstadoEnvio.ENTREGADO) {
            throw new EstadoInvalidoException("Desde EN_REPARTO solo puede pasar a ENTREGADO.");
        }
        if (actual == EstadoEnvio.ENTREGADO || actual == EstadoEnvio.CANCELADO) {
            throw new EstadoInvalidoException("El envio ya se encuentra cerrado.");
        }
    }

    public Envio despacharSiguiente() throws EnvioNoEncontradoException {
        while (!colaDespacho.isEmpty()) {
            String codigo = colaDespacho.poll();
            Envio envio = enviosPorCodigo.get(codigo);
            if (envio != null && envio.getEstado() == EstadoEnvio.REGISTRADO) {
                try {
                    envio.actualizarEstado(EstadoEnvio.EN_TRANSITO);
                    return envio;
                } catch (IllegalStateException e) {
                    throw new EnvioNoEncontradoException(codigo);
                }
            }
        }
        throw new EnvioNoEncontradoException("COLA VACIA");
    }

    public List<Envio> listarEnvios() {
        return new ArrayList<>(historialEnvios);
    }

    public List<Envio> listarPorEstado(EstadoEnvio estado) {
        List<Envio> resultado = new ArrayList<>();
        for (Envio envio : historialEnvios) {
            if (envio.getEstado() == estado) {
                resultado.add(envio);
            }
        }
        return resultado;
    }

    public Set<String> getCiudadesDestino() {
        return new HashSet<>(ciudadesDestino);
    }

    public int cantidadEnvios() {
        return enviosPorCodigo.size();
    }

    public int cantidadPendientesDespacho() {
        int count = 0;
        for (String codigo : colaDespacho) {
            Envio envio = enviosPorCodigo.get(codigo);
            if (envio != null && envio.getEstado() == EstadoEnvio.REGISTRADO) {
                count++;
            }
        }
        return count;
    }

    public void exportarCSV(Path archivo) throws IOException {
        Path parent = archivo.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try (BufferedWriter writer = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8)) {
            writer.write("codigo;tipo;remitente;destinatario;origen;destino;pesoKg;valorDeclarado;estado;costo;fechaRegistro");
            writer.newLine();
            for (Envio envio : historialEnvios) {
                writer.write(envio.aCSV());
                writer.newLine();
            }
        }
    }

    /**
     * Carga datos de demostracion para la sustentacion.
     */
    public void cargarDatosDemo() throws PaqueteriaException {
        if (!enviosPorCodigo.isEmpty()) {
            return;
        }
        registrarEnvio(false, "Ana Lopez", "Carlos Perez", "Bogota", "Medellin", 2.5, 180000);
        registrarEnvio(true, "Laura Gomez", "Juan Ruiz", "Cali", "Bogota", 1.2, 320000);
        registrarEnvio(false, "Tienda Java", "Maria Torres", "Bogota", "Bucaramanga", 4.8, 150000);
    }
}
