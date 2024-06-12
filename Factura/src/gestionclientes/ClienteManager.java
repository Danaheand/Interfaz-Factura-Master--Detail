package gestionclientes;

import confi.conexion;
import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.UUID;

public class ClienteManager {
    private Connection connection;

    public ClienteManager() {
        connection = conexion.getConnection();
    }

    public String agregarClienteDirectamente(String nombre1, String nombre2, String apellido1, String apellido2, String identificacion, String direccion, String telefono, String celular, String email) {
        if (!validarCampos(nombre1, apellido1, identificacion, direccion, telefono, celular, email)) {
            return null;
        }

        String codigo = generarCodigoCliente();

        String insertSQL = "INSERT INTO CLIENTES (CLICODIGO, CLINOMBRE1, CLINOMBRE2, CLIAPELLIDO1, CLIAPELLIDO2, CLINOMBRE, CLIIDENTIFICACION, CLIDIRECCION, CLITELEFONO, CLICELULAR, CLIEMAIL, CLITIPO, CLISTATUS) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'NOR', 'ACT')";

        try (PreparedStatement statement = connection.prepareStatement(insertSQL)) {
            statement.setString(1, codigo);
            statement.setString(2, nombre1);
            statement.setString(3, nombre2.isEmpty() ? null : nombre2);
            statement.setString(4, apellido1);
            statement.setString(5, apellido2.isEmpty() ? null : apellido2);
            statement.setString(6, nombre1 + " " + (nombre2.isEmpty() ? "" : nombre2 + " ") + apellido1 + " " + apellido2);
            statement.setString(7, identificacion);
            statement.setString(8, direccion);
            statement.setString(9, telefono);
            statement.setString(10, celular);
            statement.setString(11, email);

            statement.executeUpdate();
            return codigo;
        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(null, "Error: La identificación, el teléfono, el celular o el email ya están registrados.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al agregar el cliente: " + e.getMessage());
        }
        return null;
    }

    private boolean validarCampos(String nombre1, String apellido1, String identificacion, String direccion, String telefono, String celular, String email) {
        if (!nombre1.matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ. ]+$") || !apellido1.matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ. ]+$")) {
            JOptionPane.showMessageDialog(null, "El primer nombre y el primer apellido deben contener solo letras.");
            return false;
        }
        if (!identificacion.matches("^[0-9]{10}$")) {
            JOptionPane.showMessageDialog(null, "La identificación debe tener 10 dígitos.");
            return false;
        }
        if (!telefono.isEmpty() && !telefono.matches("^[0-9]{10}$")) {
            JOptionPane.showMessageDialog(null, "El teléfono debe tener 10 dígitos.");
            return false;
        }
        if (!celular.isEmpty() && !celular.matches("^[0-9]{10}$")) {
            JOptionPane.showMessageDialog(null, "El celular debe tener 10 dígitos.");
            return false;
        }
        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            JOptionPane.showMessageDialog(null, "El email debe tener un formato válido (ejemplo@dominio.com).");
            return false;
        }
        if (existeDuplicado("CLIDIRECCION", direccion)) {
            JOptionPane.showMessageDialog(null, "La dirección ya está registrada a otro cliente.");
            return false;
        }
        if (existeDuplicado("CLITELEFONO", telefono)) {
            JOptionPane.showMessageDialog(null, "El teléfono ya está registrado a otro cliente.");
            return false;
        }
        if (existeDuplicado("CLICELULAR", celular)) {
            JOptionPane.showMessageDialog(null, "El celular ya está registrado a otro cliente.");
            return false;
        }
        if (existeDuplicado("CLIEMAIL", email)) {
            JOptionPane.showMessageDialog(null, "El email ya está registrado a otro cliente.");
            return false;
        }
        return true;
    }

    private boolean existeDuplicado(String campo, String valor) {
        String query = "SELECT COUNT(*) FROM CLIENTES WHERE " + campo + " = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, valor);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String generarCodigoCliente() {
        return "CLI" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
}
