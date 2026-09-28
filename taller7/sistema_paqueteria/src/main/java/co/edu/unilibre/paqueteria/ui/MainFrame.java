package co.edu.unilibre.paqueteria.ui;

import co.edu.unilibre.paqueteria.exception.PaqueteriaException;
import co.edu.unilibre.paqueteria.model.EstadoEnvio;
import co.edu.unilibre.paqueteria.model.Envio;
import co.edu.unilibre.paqueteria.service.SistemaPaqueteriaService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.IOException;
import java.nio.file.Path;

public class MainFrame extends JFrame {
    private final SistemaPaqueteriaService service = new SistemaPaqueteriaService();

    private final JTextField txtRemitente = new JTextField();
    private final JTextField txtDestinatario = new JTextField();
    private final JTextField txtOrigen = new JTextField();
    private final JTextField txtDestino = new JTextField();
    private final JSpinner spPeso = new JSpinner(new SpinnerNumberModel(1.0, 0.1, 500.0, 0.1));
    private final JSpinner spValor = new JSpinner(new SpinnerNumberModel(50000.0, 1.0, 100000000.0, 1000.0));
    private final JComboBox<String> cbTipo = new JComboBox<>(new String[]{"Estandar", "Express"});

    private final JTextField txtBuscar = new JTextField();
    private final JTextArea areaRastreo = new JTextArea();
    private final JTextField txtEstadoCodigo = new JTextField();
    private final JComboBox<EstadoEnvio> cbEstado = new JComboBox<>(EstadoEnvio.values());

    private final JComboBox<EstadoEnvio> cbFiltro = new JComboBox<>();
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"Codigo", "Tipo", "Remitente", "Destinatario", "Origen", "Destino", "Peso", "Estado", "Costo"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(tableModel);
    private final JLabel lblResumen = new JLabel("Envios: 0 | Pendientes de despacho: 0");

    public MainFrame() {
        setTitle("Sistema de Paqueteria - Programacion I");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel header = new JLabel("SISTEMA DE PAQUETERIA", SwingConstants.CENTER);
        header.setFont(new Font("SansSerif", Font.BOLD, 24));
        header.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
        add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Registrar envio", construirRegistro());
        tabs.addTab("Rastrear", construirRastreo());
        tabs.addTab("Operaciones", construirOperaciones());
        tabs.addTab("Listado y CSV", construirListado());
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel construirRegistro() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        agregarCampo(form, c, 0, "Remitente", txtRemitente);
        agregarCampo(form, c, 1, "Destinatario", txtDestinatario);
        agregarCampo(form, c, 2, "Ciudad origen", txtOrigen);
        agregarCampo(form, c, 3, "Ciudad destino", txtDestino);
        agregarCampo(form, c, 4, "Peso (kg)", spPeso);
        agregarCampo(form, c, 5, "Valor declarado (COP)", spValor);
        agregarCampo(form, c, 6, "Tipo de envio", cbTipo);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnRegistrar = new JButton("Registrar envio");
        JButton btnLimpiar = new JButton("Limpiar");

        btnRegistrar.addActionListener(e -> registrar());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        acciones.add(btnRegistrar);
        acciones.add(btnLimpiar);
        panel.add(form, BorderLayout.NORTH);
        panel.add(acciones, BorderLayout.CENTER);

        return panel;
    }

    private JPanel construirRastreo() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.add(new JLabel("Codigo de rastreo:"), BorderLayout.WEST);
        top.add(txtBuscar, BorderLayout.CENTER);
        JButton btnBuscar = new JButton("Consultar");
        top.add(btnBuscar, BorderLayout.EAST);
        btnBuscar.addActionListener(e -> rastrear());

        areaRastreo.setEditable(false);
        areaRastreo.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        areaRastreo.setLineWrap(true);
        areaRastreo.setWrapStyleWord(true);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(areaRastreo), BorderLayout.CENTER);
        return panel;
    }

    private JPanel construirOperaciones() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel actualizar = new JPanel(new GridLayout(3, 2, 8, 8));
        actualizar.setBorder(BorderFactory.createTitledBorder("Actualizar estado"));
        actualizar.add(new JLabel("Codigo:"));
        actualizar.add(txtEstadoCodigo);
        actualizar.add(new JLabel("Nuevo estado:"));
        actualizar.add(cbEstado);
        JButton btnActualizar = new JButton("Aplicar estado");
        actualizar.add(new JLabel());
        actualizar.add(btnActualizar);
        btnActualizar.addActionListener(e -> actualizarEstado());

        JPanel despacho = new JPanel(new FlowLayout(FlowLayout.LEFT));
        despacho.setBorder(BorderFactory.createTitledBorder("Cola de despacho (FIFO)"));
        JButton btnDespachar = new JButton("Despachar siguiente");
        despacho.add(btnDespachar);
        despacho.add(lblResumen);
        btnDespachar.addActionListener(e -> despacharSiguiente());

        panel.add(actualizar, BorderLayout.NORTH);
        panel.add(despacho, BorderLayout.CENTER);
        return panel;
    }

    private JPanel construirListado() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cbFiltro.addItem(null);
        for (EstadoEnvio estado : EstadoEnvio.values()) cbFiltro.addItem(estado);
        JButton btnFiltrar = new JButton("Filtrar");
        JButton btnTodos = new JButton("Ver todos");
        JButton btnExportar = new JButton("Exportar CSV");
        barra.add(new JLabel("Estado:"));
        barra.add(cbFiltro);
        barra.add(btnFiltrar);
        barra.add(btnTodos);
        barra.add(btnExportar);
        btnFiltrar.addActionListener(e -> refrescarTabla((EstadoEnvio) cbFiltro.getSelectedItem()));
        btnTodos.addActionListener(e -> refrescarTabla(null));
        btnExportar.addActionListener(e -> exportarCSV());
        table.setAutoCreateRowSorter(true);
        panel.add(barra, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void registrar() {
        try {
            boolean express = "Express".equals(cbTipo.getSelectedItem());
            double peso = ((Number) spPeso.getValue()).doubleValue();
            double valor = ((Number) spValor.getValue()).doubleValue();
            Envio envio = service.registrarEnvio(express, txtRemitente.getText(), txtDestinatario.getText(),
                    txtOrigen.getText(), txtDestino.getText(), peso, valor);
            mostrarMensaje("Envio registrado correctamente.\nCodigo: " + envio.getCodigo()
                    + "\nTipo: " + envio.getTipo()
                    + "\nCosto: $" + String.format(java.util.Locale.US, "%.2f", envio.calcularCosto()));
            txtBuscar.setText(envio.getCodigo());
            txtEstadoCodigo.setText(envio.getCodigo());
            actualizarResumen();
            refrescarTabla(null);
        } catch (PaqueteriaException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void rastrear() {
        try {
            Envio envio = service.buscarPorCodigo(txtBuscar.getText());
            areaRastreo.setText(envio.generarReporteRastreo());
        } catch (PaqueteriaException ex) {
            areaRastreo.setText("");
            mostrarError(ex.getMessage());
        }
    }

    private void actualizarEstado() {
        try {
            service.actualizarEstado(txtEstadoCodigo.getText(), (EstadoEnvio) cbEstado.getSelectedItem());
            mostrarMensaje("Estado actualizado correctamente.");
            rastrearCodigo(txtEstadoCodigo.getText());
            refrescarTabla(null);
            actualizarResumen();
        } catch (PaqueteriaException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void rastrearCodigo(String codigo) {
        try {
            Envio envio = service.buscarPorCodigo(codigo);
            txtBuscar.setText(codigo);
            areaRastreo.setText(envio.generarReporteRastreo());
        } catch (PaqueteriaException ignored) {
            // No es necesario mostrar un segundo mensaje aqui.
        }
    }

    private void despacharSiguiente() {
        try {
            Envio envio = service.despacharSiguiente();
            mostrarMensaje("Despacho iniciado para " + envio.getCodigo() + " (" + envio.getTipo() + ").");
            txtEstadoCodigo.setText(envio.getCodigo());
            actualizarResumen();
            refrescarTabla(null);
        } catch (PaqueteriaException ex) {
            mostrarError("No hay envios listos para despacho.");
        }
    }

    private void exportarCSV() {
        Path ruta = Path.of("data", "envios.csv");
        try {
            service.exportarCSV(ruta);
            mostrarMensaje("CSV exportado en:\n" + ruta.toAbsolutePath());
        } catch (IOException ex) {
            mostrarError("No fue posible exportar el CSV: " + ex.getMessage());
        }
    }

    private void refrescarTabla(EstadoEnvio filtro) {
        tableModel.setRowCount(0);
        for (Envio envio : (filtro == null ? service.listarEnvios() : service.listarPorEstado(filtro))) {
            tableModel.addRow(new Object[]{
                    envio.getCodigo(), envio.getTipo(), envio.getRemitente(), envio.getDestinatario(),
                    envio.getCiudadOrigen(), envio.getCiudadDestino(), envio.getPesoKg(), envio.getEstado(),
                    String.format(java.util.Locale.US, "%.2f", envio.calcularCosto())
            });
        }
    }

    private void actualizarResumen() {
        lblResumen.setText("Envios: " + service.cantidadEnvios()
                + " | Pendientes de despacho: " + service.cantidadPendientesDespacho());
    }

    private void limpiarFormulario() {
        txtRemitente.setText("");
        txtDestinatario.setText("");
        txtOrigen.setText("");
        txtDestino.setText("");
        spPeso.setValue(1.0);
        spValor.setValue(50000.0);
        cbTipo.setSelectedIndex(0);
    }

    private void agregarCampo(JPanel panel, GridBagConstraints c, int fila, String etiqueta, java.awt.Component componente) {
        c.gridx = 0;
        c.gridy = fila;
        c.weightx = 0;
        panel.add(new JLabel(etiqueta + ":"), c);
        c.gridx = 1;
        c.weightx = 1;
        panel.add(componente, c);
    }

    private void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Sistema de Paqueteria", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void mostrar() {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
