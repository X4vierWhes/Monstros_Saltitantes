package org.example.controller;

import org.example.model.SQLite;
import org.example.model.User;
import org.example.view.UserView;

import javax.swing.*;
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

        view.getChangeAvatarButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.changeAvatar(user);
                bd.editUserByUsername(user.getUserName(), user);
            }
        });
        view.getInitButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.dispose();
                javax.swing.SwingUtilities.invokeLater(() -> {
                    simulation = new SimulationController(user, bd);
                });
            }
        });

        view.getResultButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        view.getDeleteButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                        view,
                        "Tem certeza que deseja deletar sua conta?",
                        "Confirmação",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    bd.deleteUserByUsername(user.getUserName());
                    JOptionPane.showMessageDialog(view, "Conta deletada com sucesso.");
                    view.dispose();
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        LoginController login = new LoginController();
                    });
                }
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
