package org.example.controller;

import org.example.model.SQLite;
import org.example.model.User;
import org.example.view.LoginView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginController {
    private LoginView view;
    private UserController user;
    private SQLite bd;

    public LoginController(){
        bd = new SQLite();
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

        view.getSignInButtonButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                signIn();
            }
        });
    }

    private void signIn(){
        String userName = view.getUsernameField().getText();
        String passWord = new String(view.getPasswordField().getPassword());
        User newUser = new User(userName, passWord, "common");

        if(bd.insertIntoUsers(newUser)){
            view.getStatusLabel().setForeground(new Color(0, 128, 0));
            view.getStatusLabel().setText("Conta criada com sucesso!");
        }else{
            User user = bd.findUserByUsername(userName);
            if(user.getUserName().equals(userName)){
                view.getStatusLabel().setForeground(Color.RED);
                view.getStatusLabel().setText("Nome de usuario ocupado!");
            }
        }
    }

    private void login(){
        String userName = view.getUsernameField().getText();
        String passWord = new String(view.getPasswordField().getPassword());
        User log = bd.findUserByUsername(userName);

        if(log != null && log.getPassWord().equals(passWord)){
            view.getStatusLabel().setForeground(new Color(0, 128, 0));
            view.getStatusLabel().setText("Login realizado com sucesso!");

            JOptionPane.showMessageDialog(view, "Bem-vindo, " + userName + "!");

            view.dispose();
            javax.swing.SwingUtilities.invokeLater( () -> {
                user = new UserController(log, bd);
            });
        }else{
            view.getStatusLabel().setForeground(Color.RED);
            view.getStatusLabel().setText("Usuário ou password inválidos.");
        }
        
    }
}
