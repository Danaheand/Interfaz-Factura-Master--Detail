package factura;

import confi.conexion;
import gestionclientes.ClienteManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FacturaMaster {

    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel sumaLabel;
    private JLabel ivaLabel;
    private JLabel totalLabel;
    private JTextField productoField;
    private JTextField descripcionField;
    private JTextField uniMedField;
    private JTextField cantidadField;
    private JTextField valUniField;
    private JTextField clienteField;
    private JComboBox<String> ivaComboBox;
    private JComboBox<String> metodoPagoComboBox;  // Nueva JComboBox para métodos de pago
    private JLabel facturaNoLabel;
    private JLabel fechaLabel;

    private int facturaCounter = 1;

    private Connection connection;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FacturaMaster app = new FacturaMaster();
        });
    }

    public FacturaMaster() {
        connection = conexion.getConnection();
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    {
        JFrame frame = new JFrame("Factura - Productos (INTERFAZ CRUD)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(173, 216, 230)); // Color celeste

        // Logo de la empresa y título
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(240, 248, 255));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        ImageIcon logoIcon = new ImageIcon("C:/Users/Dana/Desktop/images (1).jpeg");
        JLabel logoLabel = new JLabel(logoIcon);
        headerPanel.add(logoLabel, BorderLayout.WEST);

        JLabel companyName = new JLabel("AndradeSoft", SwingConstants.CENTER);
        companyName.setFont(new Font("Arial", Font.BOLD, 36));
        companyName.setForeground(Color.BLACK);
        headerPanel.add(companyName, BorderLayout.CENTER);

        panel.add(headerPanel, BorderLayout.NORTH);

        // Información de la factura
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(3, 2, 10, 10));
        infoPanel.setBackground(new Color(240, 248, 255));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        facturaNoLabel = new JLabel("Factura No.: FAC-001");
        clienteField = new JTextField(20);
        fechaLabel = new JLabel("Fecha: " + new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        infoPanel.add(new JLabel("Factura No.:"));
        infoPanel.add(facturaNoLabel);
        infoPanel.add(new JLabel("Cliente:"));
        infoPanel.add(clienteField);
        infoPanel.add(new JLabel("Fecha:"));
        infoPanel.add(fechaLabel);

        panel.add(infoPanel, BorderLayout.NORTH);

        // Panel principal para tabla y campos de ingreso
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Tabla
        String[] columnNames = {"Producto", "Descripción", "Uni Med", "Cantidad", "Val Uni", "Subtotal"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel para ingreso de datos
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBackground(new Color(240, 248, 255));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel de campos de texto
        JPanel fieldsPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        fieldsPanel.setBackground(new Color(240, 248, 255));

        productoField = new JTextField(10);
        descripcionField = new JTextField(10);
        uniMedField = new JTextField(10);
        cantidadField = new JTextField(10);
        valUniField = new JTextField(10);

        fieldsPanel.add(new JLabel("Producto:"));
        fieldsPanel.add(productoField);
        fieldsPanel.add(new JLabel("Descripción:"));
        fieldsPanel.add(descripcionField);
        fieldsPanel.add(new JLabel("Uni Med:"));
        fieldsPanel.add(uniMedField);
        fieldsPanel.add(new JLabel("Cantidad:"));
        fieldsPanel.add(cantidadField);
        fieldsPanel.add(new JLabel("Val Uni:"));
        fieldsPanel.add(valUniField);

        inputPanel.add(fieldsPanel);

        // Panel de selección de IVA
        JPanel ivaPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        ivaPanel.setBackground(new Color(240, 248, 255));

        ivaComboBox = new JComboBox<>(new String[]{"0%", "12%", "15%", "18%"});
        ivaPanel.add(new JLabel("IVA:"));
        ivaPanel.add(ivaComboBox);

        inputPanel.add(ivaPanel);

        // Panel de selección de método de pago
        JPanel metodoPagoPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        metodoPagoPanel.setBackground(new Color(240, 248, 255));

        metodoPagoComboBox = new JComboBox<>(new String[]{"EFECT", "TARCR", "TARDB", "TRANS", "CHEQ"});
        metodoPagoPanel.add(new JLabel("Método de Pago:"));
        metodoPagoPanel.add(metodoPagoComboBox);

        inputPanel.add(metodoPagoPanel);

        // Panel de botones
        JPanel buttonsPanel = new JPanel(new GridLayout(7, 1, 10, 10)); // Cambiado de 6 a 7
        buttonsPanel.setBackground(new Color(240, 248, 255));

        JButton searchButton = new JButton("Buscar Producto");
        searchButton.setFont(new Font("Arial", Font.PLAIN, 14));
        searchButton.setBackground(new Color(173, 216, 230));
        searchButton.setForeground(Color.BLACK);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarProducto(productoField);
            }
        });

        JButton addButton = new JButton("Agregar Producto");
        addButton.setFont(new Font("Arial", Font.PLAIN, 14));
        addButton.setBackground(new Color(173, 216, 230));
        addButton.setForeground(Color.BLACK);
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarProducto(productoField, descripcionField, uniMedField, cantidadField, valUniField);
            }
        });

        JButton updateButton = new JButton("Actualizar Producto");
        updateButton.setFont(new Font("Arial", Font.PLAIN, 14));
        updateButton.setBackground(new Color(173, 216, 230));
        updateButton.setForeground(Color.BLACK);
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarProducto(productoField, descripcionField, uniMedField, cantidadField, valUniField);
            }
        });

        JButton deleteButton = new JButton("Eliminar Producto");
        deleteButton.setFont(new Font("Arial", Font.PLAIN, 14));
        deleteButton.setBackground(new Color(173, 216, 230));
        deleteButton.setForeground(Color.BLACK);
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarProducto();
            }
        });

        JButton readButton = new JButton("Leer Producto");
        readButton.setFont(new Font("Arial", Font.PLAIN, 14));
        readButton.setBackground(new Color(173, 216, 230));
        readButton.setForeground(Color.BLACK);
        readButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                leerProducto(productoField, descripcionField, uniMedField, cantidadField, valUniField);
            }
        });

        JButton generateButton = new JButton("Generar Factura Final");
        generateButton.setFont(new Font("Arial", Font.PLAIN, 14));
        generateButton.setBackground(new Color(173, 216, 230));
        generateButton.setForeground(Color.BLACK);
        generateButton.addActionListener((ActionEvent e) -> {
            generarFacturaFinal();
        });

        JButton verFacturasButton = new JButton("Ver Facturas");
        verFacturasButton.setFont(new Font("Arial", Font.PLAIN, 14));
        verFacturasButton.setBackground(new Color(173, 216, 230));
        verFacturasButton.setForeground(Color.BLACK);
        verFacturasButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new VerFacturas(); // Abre la ventana de Ver Facturas
            }
        });

        buttonsPanel.add(searchButton);
        buttonsPanel.add(addButton);
        buttonsPanel.add(updateButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(readButton);
        buttonsPanel.add(generateButton);
        buttonsPanel.add(verFacturasButton); // Añadir el botón al panel de botones

        inputPanel.add(buttonsPanel);

        mainPanel.add(inputPanel, BorderLayout.WEST);
        panel.add(mainPanel, BorderLayout.CENTER);

        // Totales
        JPanel totalPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        totalPanel.setBackground(new Color(240, 248, 255));
        totalPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        totalPanel.add(new JLabel("SUMA"));
        sumaLabel = new JLabel("$0.00");
        totalPanel.add(sumaLabel);
        totalPanel.add(new JLabel("IVA"));
        ivaLabel = new JLabel("$0.00");
        totalPanel.add(ivaLabel);
        totalPanel.add(new JLabel("TOTAL"));
        totalLabel = new JLabel("$0.00");
        totalPanel.add(totalLabel);

        panel.add(totalPanel, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setVisible(true);
    }

    private void buscarProducto(JTextField productoField) {
        String productoCodigo = productoField.getText();
        String query = "SELECT * FROM PRODUCTOS WHERE PROCODIGO = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, productoCodigo);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    descripcionField.setText(resultSet.getString("PRODESCRIPCION"));
                    uniMedField.setText(resultSet.getString("PROUNIDADMEDIDA"));
                    valUniField.setText(resultSet.getString("PROPRECIOUM"));
                } else {
                    JOptionPane.showMessageDialog(null, "Producto no encontrado.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void agregarProducto(JTextField productoField, JTextField descripcionField, JTextField uniMedField, JTextField cantidadField, JTextField valUniField) {
        if (productoField.getText().isEmpty() || descripcionField.getText().isEmpty() || uniMedField.getText().isEmpty() ||
                cantidadField.getText().isEmpty() || valUniField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Todos los campos deben estar llenos para agregar un producto.");
            return;
        }

        String producto = productoField.getText();
        String descripcion = descripcionField.getText();
        String uniMed = uniMedField.getText();
        int cantidad = Integer.parseInt(cantidadField.getText());
        double valUni = Double.parseDouble(valUniField.getText());
        double subtotal = cantidad * valUni;

        tableModel.addRow(new Object[]{producto, descripcion, uniMed, cantidad, "$" + valUni, "$" + subtotal});

        productoField.setText("");
        descripcionField.setText("");
        uniMedField.setText("");
        cantidadField.setText("");
        valUniField.setText("");

        actualizarTotales();
    }

    private void actualizarProducto(JTextField productoField, JTextField descripcionField, JTextField uniMedField, JTextField cantidadField, JTextField valUniField) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            String producto = productoField.getText();
            String descripcion = descripcionField.getText();
            String uniMed = uniMedField.getText();
            int cantidad = Integer.parseInt(cantidadField.getText());
            double valUni = Double.parseDouble(valUniField.getText());
            double subtotal = cantidad * valUni;

            tableModel.setValueAt(producto, selectedRow, 0);
            tableModel.setValueAt(descripcion, selectedRow, 1);
            tableModel.setValueAt(uniMed, selectedRow, 2);
            tableModel.setValueAt(cantidad, selectedRow, 3);
            tableModel.setValueAt("$" + valUni, selectedRow, 4);
            tableModel.setValueAt("$" + subtotal, selectedRow, 5);

            productoField.setText("");
            descripcionField.setText("");
            uniMedField.setText("");
            cantidadField.setText("");
            valUniField.setText("");

            actualizarTotales();
        } else {
            JOptionPane.showMessageDialog(null, "Seleccione un producto para actualizar");
        }
    }

    private void eliminarProducto() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int confirm = JOptionPane.showConfirmDialog(null, "¿Está seguro de que desea eliminar el producto?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                tableModel.removeRow(selectedRow);
                actualizarTotales();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Seleccione un producto para eliminar");
        }
    }

    private void leerProducto(JTextField productoField, JTextField descripcionField, JTextField uniMedField, JTextField cantidadField, JTextField valUniField) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            productoField.setText((String) tableModel.getValueAt(selectedRow, 0));
            descripcionField.setText((String) tableModel.getValueAt(selectedRow, 1));
            uniMedField.setText((String) tableModel.getValueAt(selectedRow, 2));
            cantidadField.setText(String.valueOf(tableModel.getValueAt(selectedRow, 3)));
            valUniField.setText(((String) tableModel.getValueAt(selectedRow, 4)).replace("$", ""));
        } else {
            JOptionPane.showMessageDialog(null, "Seleccione un producto para leer");
        }
    }

    private void actualizarTotales() {
        double suma = 0.0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String subtotalStr = (String) tableModel.getValueAt(i, 5);
            double subtotal = Double.parseDouble(subtotalStr.replace("$", ""));
            suma += subtotal;
        }

        String ivaPercentage = ivaComboBox.getSelectedItem().toString().replace("%", "");
        double ivaRate = Double.parseDouble(ivaPercentage) / 100;
        double iva = suma * ivaRate;
        double total = suma + iva;

        sumaLabel.setText(String.format("$%.2f", suma));
        ivaLabel.setText(String.format("$%.2f", iva));
        totalLabel.setText(String.format("$%.2f", total));
    }

    private void limpiarCamposYTabla() {
        // Limpiar campos de texto
        productoField.setText("");
        descripcionField.setText("");
        uniMedField.setText("");
        cantidadField.setText("");
        valUniField.setText("");
        clienteField.setText("");

        // Limpiar tabla
        tableModel.setRowCount(0);

        // Restablecer totales
        sumaLabel.setText("$0.00");
        ivaLabel.setText("$0.00");
        totalLabel.setText("$0.00");
    }

    private void generarFacturaFinal() {
        System.out.println("Iniciando generación de factura...");

        // Obtener el último número de factura
        String facturaNo = obtenerNuevoNumeroFactura();
        if (facturaNo == null) {
            JOptionPane.showMessageDialog(null, "No se pudo generar el número de factura.");
            return;
        }

        String clienteNombre = clienteField.getText();
        String clienteCodigo = getClienteCodigo(clienteNombre);

        System.out.println("Cliente código obtenido: " + clienteCodigo);

        if (clienteCodigo == null) {
            int opcion = JOptionPane.showConfirmDialog(null, "El cliente no existe. ¿Desea agregarlo?", "Cliente no encontrado", JOptionPane.YES_NO_OPTION);
            if (opcion == JOptionPane.YES_OPTION) {
                // Mostrar un diálogo para ingresar los datos del nuevo cliente
                JTextField nombre1Field = new JTextField();
                JTextField nombre2Field = new JTextField();
                JTextField apellido1Field = new JTextField();
                JTextField apellido2Field = new JTextField();
                JTextField identificacionField = new JTextField();
                JTextField direccionField = new JTextField();
                JTextField telefonoField = new JTextField();
                JTextField celularField = new JTextField();
                JTextField emailField = new JTextField();
                Object[] message = {
                    "Primer Nombre:", nombre1Field,
                    "Segundo Nombre (opcional):", nombre2Field,
                    "Primer Apellido:", apellido1Field,
                    "Segundo Apellido:", apellido2Field,
                    "Identificación:", identificacionField,
                    "Dirección:", direccionField,
                    "Teléfono:", telefonoField,
                    "Celular:", celularField,
                    "Email:", emailField
                };
                int option = JOptionPane.showConfirmDialog(null, message, "Ingrese los datos del nuevo cliente", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    ClienteManager clienteManager = new ClienteManager();
                    clienteCodigo = clienteManager.agregarClienteDirectamente(
                        nombre1Field.getText(),
                        nombre2Field.getText(),
                        apellido1Field.getText(),
                        apellido2Field.getText(),
                        identificacionField.getText(),
                        direccionField.getText(),
                        telefonoField.getText(),
                        celularField.getText(),
                        emailField.getText()
                    );
                    System.out.println("Cliente nuevo agregado, código: " + clienteCodigo);
                } else {
                    System.out.println("Operación cancelada por el usuario.");
                    return;
                }
            } else {
                System.out.println("Operación cancelada por el usuario.");
                return;
            }
        }

        String fecha = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        double suma = Double.parseDouble(sumaLabel.getText().replace("$", "").replace(",", "."));
        double iva = Double.parseDouble(ivaLabel.getText().replace("$", "").replace(",", "."));
        double total = Double.parseDouble(totalLabel.getText().replace("$", "").replace(",", "."));

        String metodoPago = metodoPagoComboBox.getSelectedItem().toString();  // Obtener el método de pago seleccionado

        System.out.println("Valores calculados: Suma=" + suma + ", IVA=" + iva + ", Total=" + total + ", Método de Pago=" + metodoPago);

        try {
            String insertFacturaSQL = "INSERT INTO FACTURAS (FACNUMERO, CLICODIGO, FACFECHA, FACSUBTOTAL, FACIVA, FACSTATUS, FACFORMAPAGO) VALUES (?, ?, ?, ?, ?, 'PAG', ?)";
            try (PreparedStatement insertFacturaStmt = connection.prepareStatement(insertFacturaSQL)) {
                insertFacturaStmt.setString(1, facturaNo);
                insertFacturaStmt.setString(2, clienteCodigo);
                insertFacturaStmt.setString(3, fecha);
                insertFacturaStmt.setDouble(4, suma);
                insertFacturaStmt.setDouble(5, iva);
                insertFacturaStmt.setString(6, metodoPago);  // Asignar el método de pago seleccionado
                insertFacturaStmt.executeUpdate();
                System.out.println("Factura insertada en la base de datos.");
            }

            String insertPXFSQL = "INSERT INTO PXF (FACNUMERO, PROCODIGO, PXFCANTIDAD, PXFVALOR, PXFSUBTOTAL, PXFSTATUS) VALUES (?, ?, ?, ?, ?, 'ACT')";
            try (PreparedStatement insertPXFStmt = connection.prepareStatement(insertPXFSQL)) {
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    String procodigo = (String) tableModel.getValueAt(i, 0);
                    int cantidad = Integer.parseInt(tableModel.getValueAt(i, 3).toString());
                    double valor = Double.parseDouble(((String) tableModel.getValueAt(i, 4)).replace("$", "").replace(",", "."));
                    double subtotal = Double.parseDouble(((String) tableModel.getValueAt(i, 5)).replace("$", "").replace(",", "."));

                    insertPXFStmt.setString(1, facturaNo);
                    insertPXFStmt.setString(2, procodigo);
                    insertPXFStmt.setDouble(3, cantidad);
                    insertPXFStmt.setDouble(4, valor);
                    insertPXFStmt.setDouble(5, subtotal);
                    insertPXFStmt.addBatch();
                    System.out.println("Producto añadido al batch: " + procodigo);
                }
                insertPXFStmt.executeBatch();
                System.out.println("Productos insertados en PXF.");
            }

            connection.commit();
            System.out.println("Transacción confirmada.");

            JFrame facturaFrame = new JFrame("Factura Final");
            facturaFrame.setSize(600, 400);
            facturaFrame.setLayout(new BorderLayout());

            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setBackground(Color.CYAN);
            infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            infoPanel.add(new JLabel("Factura No.: " + facturaNo));
            infoPanel.add(new JLabel("Cliente: " + clienteNombre));
            infoPanel.add(new JLabel("Fecha: " + fecha));
            infoPanel.add(new JLabel("Código SRI: 001-001-009"));
            infoPanel.add(new JLabel("Método de Pago: " + metodoPago));  // Mostrar el método de pago

            facturaFrame.add(infoPanel, BorderLayout.NORTH);

            // Consulta SQL para obtener los detalles de la factura
            String query = "SELECT p.PROCODIGO, p.PRODESCRIPCION, p.PROUNIDADMEDIDA, px.PXFCANTIDAD, p.PROPRECIOUM, px.PXFSUBTOTAL " +
                           "FROM PXF px " +
                           "JOIN PRODUCTOS p ON px.PROCODIGO = p.PROCODIGO " +
                           "WHERE px.FACNUMERO = ?";
            DefaultTableModel facturaTableModel = new DefaultTableModel(new String[]{"Producto", "Descripción", "Uni Med", "Cantidad", "Val Uni", "Subtotal"}, 0);
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, facturaNo);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String producto = rs.getString("PROCODIGO");
                        String descripcion = rs.getString("PRODESCRIPCION");
                        String unidadMedida = rs.getString("PROUNIDADMEDIDA");
                        int cantidad = rs.getInt("PXFCANTIDAD");
                        double valUni = rs.getDouble("PROPRECIOUM");
                        double subtotal = rs.getDouble("PXFSUBTOTAL");
                        facturaTableModel.addRow(new Object[]{producto, descripcion, unidadMedida, cantidad, valUni, subtotal});
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            JTable facturaTable = new JTable(facturaTableModel);
            JScrollPane scrollPane = new JScrollPane(facturaTable);
            facturaFrame.add(scrollPane, BorderLayout.CENTER);

            JPanel totalPanel = new JPanel(new GridLayout(3, 2));
            totalPanel.setBackground(Color.CYAN);
            totalPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            totalPanel.add(new JLabel("SUMA"));
            totalPanel.add(new JLabel("$" + suma));
            totalPanel.add(new JLabel("IVA"));
            totalPanel.add(new JLabel("$" + iva));
            totalPanel.add(new JLabel("TOTAL"));
            totalPanel.add(new JLabel("$" + total));

            facturaFrame.add(totalPanel, BorderLayout.SOUTH);

            facturaFrame.setVisible(true);

            facturaCounter++;
            actualizarFacturaInfo();
            limpiarCamposYTabla(); // Añadir esta línea para limpiar los campos y la tabla
            System.out.println("Factura generada y mostrada.");
        } catch (SQLException e) {
            try {
                connection.rollback();
                System.out.println("Transacción revertida.");
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            System.out.println("Error al generar la factura: " + e.getMessage());
        }
    }

    private String obtenerNuevoNumeroFactura() {
        String ultimoNumeroFactura = null;
        String query = "SELECT MAX(FACNUMERO) AS ultimoNumero FROM FACTURAS";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                ultimoNumeroFactura = resultSet.getString("ultimoNumero");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (ultimoNumeroFactura != null && !ultimoNumeroFactura.isEmpty()) {
            try {
                int ultimoNumero = Integer.parseInt(ultimoNumeroFactura.replace("FAC-", ""));
                return "FAC-" + String.format("%03d", ultimoNumero + 1);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return "FAC-001"; // Valor por defecto en caso de error
            }
        } else {
            return "FAC-001";
        }
    }

    private String getClienteCodigo(String clienteNombre) {
        String query = "SELECT CLICODIGO FROM CLIENTES WHERE CLINOMBRE = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, clienteNombre);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("CLICODIGO");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void actualizarFacturaInfo() {
        facturaNoLabel.setText("Factura No.: FAC-" + String.format("%03d", facturaCounter));
        clienteField.setText("");
        fechaLabel.setText("Fecha: " + new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
    }
}
