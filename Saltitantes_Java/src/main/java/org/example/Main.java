package org.example;

import org.example.controller.LoginController;
import org.example.controller.SimulationController;

import java.sql.SQLException;

/**
 * Classe principal que inicializa a aplicação.
 */
public class Main {

    public static LoginController login;

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                login = new LoginController();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
