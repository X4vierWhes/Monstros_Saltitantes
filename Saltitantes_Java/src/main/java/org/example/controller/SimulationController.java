package org.example.controller;

import org.example.model.User;
import org.example.view.SimulationView;

public class SimulationController {

    private final SimulationView view;
    private User user;

    public SimulationController(User user) {
        this.view = new SimulationView(user);
        this.user = user;
        initListeners();
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
            user.addSimulations();
        });
    }
}
