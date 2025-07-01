package org.example.controller;

import org.example.model.SQLite;
import org.example.model.User;
import org.example.view.UserView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserController {
    private final UserView view;
    private final User user;
    private SimulationController simulation;
    private SQLite bd;

    public UserController(User user, SQLite bd){
        this.bd = bd;
        this.view = new UserView(user);
        this.user = user;
        initListeners();
    }

    private void initListeners() {
        view.getInitButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.dispose();
                javax.swing.SwingUtilities.invokeLater(() -> {
                    simulation = new SimulationController(user, bd);
                });
            }
        });

        view.getQuitButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.dispose();
                javax.swing.SwingUtilities.invokeLater(() -> {
                    LoginController login = new LoginController();
                });
            }
        });
    }
}
