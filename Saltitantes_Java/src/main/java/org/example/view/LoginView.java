package org.example.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginView extends JFrame{
    /** Largura da janela. */
    private static final int WIDTH = 720;

    /** Altura da janela. */
    private static final int HEIGHT = 480;

    private static final int fieldWidth = 200;

    private static final int fieldHeight = 25;

    private static final int buttonWIDTH = 100;
    private static final int buttonHEIGHT = 25;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;

    private static final Color textColor = new Color(255,0, 129);
    public LoginView(){
        this.setTitle("Tela de Login");
        this.setSize(WIDTH, HEIGHT);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.getContentPane().setBackground(new Color(0,0,0)); //cor background da tela

        initComponents();

        setVisible(true);
    }

    private void initComponents() {
        ///////////////////////////////////////////////////////////////////////////////////
        //Adicionando Label principal
        JLabel titleLabel = new JLabel("SIMULATION");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBounds(WIDTH / 2 - 100, 30, 200, 30);
        titleLabel.setForeground(textColor);
        add(titleLabel);

        JLabel userLabel = new JLabel("Usuário:");
        userLabel.setBounds(WIDTH / 2 - 190, HEIGHT / 4, 100, 25);
        userLabel.setForeground(textColor);
        add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(WIDTH / 2 - 120, HEIGHT / 4, fieldWidth, fieldHeight);
        add(usernameField);

        JLabel passLabel = new JLabel("Senha:");
        passLabel.setBounds(WIDTH / 2 - 180, HEIGHT / 3, 100, 25);
        passLabel.setForeground(textColor);
        add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(WIDTH / 2 - 120, HEIGHT / 3, 200, 25);
        add(passwordField);

        loginButton = new JButton("Entrar");
        loginButton.setBounds(WIDTH/2 - 75 , 200, buttonWIDTH, buttonHEIGHT);
        loginButton.setBackground(textColor);
        loginButton.setForeground(Color.WHITE);
        add(loginButton);

        statusLabel = new JLabel("");
        statusLabel.setBounds(WIDTH/2 - 100 , 225, 300, 25);
        statusLabel.setForeground(Color.RED);
        add(statusLabel);
    }

    public JTextField getUsernameField() {
        return usernameField;
    }

    public JPasswordField getPasswordField() {
        return passwordField;
    }

    public JButton getLoginButton() {
        return loginButton;
    }

    public JLabel getStatusLabel() {
        return statusLabel;
    }
}
