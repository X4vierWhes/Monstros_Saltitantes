package org.example;

import org.example.controller.LoginController;
import org.example.controller.SimulationController;

/**
 * Classe principal que inicializa a aplicação.
 */
public class Main {

    public static SimulationController simulation;
    public static LoginController login;

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            login = new LoginController();
        });
    }
}
