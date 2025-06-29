package org.example.controller;

import org.example.view.SimulationView;

public class SimulationController {

    private final SimulationView view;

    public SimulationController() {
        this.view = new SimulationView(); // primeiro cria a view
        initListeners(); // depois conecta os eventos
        view.getCreaturesPanel().addCreature(view.getRandomX());
        view.getCreaturesPanel().startPhisycsTimer();
    }

    private void initListeners() {
        view.getBtnAddBall().addActionListener(e -> {
            int x = view.getRandomX();
            view.getCreaturesPanel().addCreature(x);
        });

        view.getBtnInit().addActionListener(e -> {
            int x = view.getRandomX();
            view.getCreaturesPanel().initSimulation(x);
        });
    }
}
