package org.example;

import org.example.controller.SimulationController;
import org.example.view.SimulationView;

/**
 * Classe principal que inicializa a aplicação.
 *
 * <p>Este é o ponto de entrada do programa. Ele cria uma instância da classe
 * {@link SimulationController} e chama o método {@code initScreen()} para iniciar a interface.</p>
 *
 * @author ValentinaClash
 * @version 1.0
 * @see SimulationController
 */
public class Main {

    public static SimulationView simulationView;
    public static void main(String[] args) {
        simulationView = new SimulationView();
        simulationView.initSimulationView();

    }
}
