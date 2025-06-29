package org.example.controller;

import org.example.view.LoginView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginController {
    private LoginView view;
    private SimulationController simulation;

    public LoginController(){
        view = new LoginView();
        initListeners();
    }

    private void initListeners(){
        view.getLoginButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
    }

    private void login(){
        String userName = view.getUsernameField().getText();
        String passWord = new String(view.getPasswordField().getPassword());

        if (userName.equals("admin") && passWord.equals("1234")) {
            view.getStatusLabel().setForeground(new Color(0, 128, 0));
            view.getStatusLabel().setText("Login realizado com sucesso!");

            JOptionPane.showMessageDialog(view, "Bem-vindo, " + userName + "!");

            view.dispose();

            javax.swing.SwingUtilities.invokeLater(() -> {
                simulation = new SimulationController();
            });
        } else {
            view.getStatusLabel().setForeground(Color.RED);
            view.getStatusLabel().setText("Usuário ou passWord inválidos.");
        }
        
    }
}
