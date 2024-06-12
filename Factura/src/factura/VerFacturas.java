package factura;

import confi.conexion;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VerFacturas {
    private JFrame frame;
    private JTable table;
    private DefaultTableModel tableModel;
    private Connection connection;

    public VerFacturas() {
        connection = conexion.getConnection();
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        frame = new JFrame("Ver Facturas");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columnNames = {"Factura No.", "Cliente", "Fecha", "Subtotal", "IVA", "Total"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        cargarFacturas();

        panel.add(scrollPane, BorderLayout.CENTER);
        frame.add(panel);
        frame.setVisible(true);
    }

    private void cargarFacturas() {
        String query = "SELECT f.FACNUMERO, c.CLINOMBRE, f.FACFECHA, f.FACSUBTOTAL, f.FACIVA, (f.FACSUBTOTAL + f.FACIVA) AS TOTAL " +
                       "FROM FACTURAS f " +
                       "JOIN CLIENTES c ON f.CLICODIGO = c.CLICODIGO";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String facNumero = resultSet.getString("FACNUMERO");
                String cliNombre = resultSet.getString("CLINOMBRE");
                String facFecha = resultSet.getString("FACFECHA");
                double facSubtotal = resultSet.getDouble("FACSUBTOTAL");
                double facIva = resultSet.getDouble("FACIVA");
                double total = resultSet.getDouble("TOTAL");
                tableModel.addRow(new Object[]{facNumero, cliNombre, facFecha, facSubtotal, facIva, total});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al cargar las facturas: " + e.getMessage());
        }
    }
}
