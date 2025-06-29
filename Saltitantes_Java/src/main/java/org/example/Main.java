package org.example;

import org.example.controller.SimulationController;

/**
 * Classe principal que inicializa a aplicação.
 */
public class Main {

    public static SimulationController simulation;

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            simulation = new SimulationController();
        });
    }
}
