package org.example.controller;

import org.example.model.SQLite;
import org.example.model.User;
import org.example.view.SimulationView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SimulationController {

    private final SimulationView view;
    private User user;
    private SQLite bd;

    public SimulationController(User user, SQLite bd) {
        this.view = new SimulationView(user, bd);
        this.user = user;
        this.bd = bd;
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
        });

        view.getBtnQuit().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.dispose();
                javax.swing.SwingUtilities.invokeLater(() -> {
                    UserController userController = new UserController(user, bd);
                });
            }
        });
    }
}
