package org.example.view;

import javax.swing.*;
import java.awt.*;

/**
 * Representa a interface gráfica da tela de login do sistema.
 *
 * <p>Permite que o usuário insira nome de usuário e senha, além de acessar as opções
 * de login e criação de nova conta (sign-in). A classe estende {@link JFrame} e monta
 * todos os componentes de forma manual com layout absoluto.</p>
 *
 * <p>Os elementos gráficos incluem campos de texto, botões e mensagens de status,
 * todos organizados visualmente com cores e fontes personalizadas.</p>
 *
 * @author ValentinaClash
 * @version 1.0
 */
public class LoginView extends JFrame {

    /** Largura padrão da janela. */
    private static final int WIDTH = 720;

    /** Altura padrão da janela. */
    private static final int HEIGHT = 480;

    /** Largura do campo de entrada de texto. */
    private static final int fieldWidth = 210;

    /** Altura do campo de entrada de texto. */
    private static final int fieldHeight = 25;

    /** Largura padrão dos botões. */
    private static final int buttonWIDTH = 100;

    /** Altura padrão dos botões. */
    private static final int buttonHEIGHT = 25;

    /** Campo de entrada para o nome de usuário. */
    private JTextField usernameField;

    /** Campo de entrada para a senha (oculta). */
    private JPasswordField passwordField;

    /** Botão de login. */
    private JButton loginButton;

    /** Botão de cadastro (sign-in). */
    private JButton signInButton;

    /** Rótulo para exibir mensagens de erro ou status. */
    private JLabel statusLabel;

    /** Cor de destaque usada em botões e textos. */
    private static final Color textColor = new Color(255, 0, 129);

    /**
     * Construtor da tela de login. Inicializa os componentes e exibe a janela.
     */
    public LoginView() {
        this.setTitle("Tela de Login");
        this.setSize(WIDTH, HEIGHT);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.getContentPane().setBackground(Color.BLACK); // cor de fundo
        initComponents();
        setVisible(true);
    }

    /**
     * Inicializa e posiciona todos os componentes da interface gráfica.
     */
    private void initComponents() {
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
        passwordField.setBounds(WIDTH / 2 - 120, HEIGHT / 3, fieldWidth, fieldHeight);
        add(passwordField);

        loginButton = new JButton("Entrar");
        loginButton.setBounds(WIDTH / 2 - 120, 200, buttonWIDTH, buttonHEIGHT);
        loginButton.setBackground(textColor);
        loginButton.setForeground(Color.WHITE);
        add(loginButton);

        signInButton = new JButton("Sign-in");
        signInButton.setBounds(WIDTH / 2 - 10, 200, buttonWIDTH, buttonHEIGHT);
        signInButton.setBackground(textColor);
        signInButton.setForeground(Color.WHITE);
        add(signInButton);

        statusLabel = new JLabel("");
        statusLabel.setBounds(WIDTH / 2 - 100, 225, 300, 25);
        statusLabel.setForeground(Color.RED);
        add(statusLabel);
    }

    /**
     * @return Campo de entrada do nome de usuário.
     */
    public JTextField getUsernameField() {
        return usernameField;
    }

    /**
     * @return Campo de entrada da senha.
     */
    public JPasswordField getPasswordField() {
        return passwordField;
    }

    /**
     * @return Botão para realizar o login.
     */
    public JButton getLoginButton() {
        return loginButton;
    }

    /**
     * @return Botão para realizar cadastro (sign-in).
     */
    public JButton getSignInButtonButton() {
        return signInButton;
    }

    /**
     * @return Rótulo de status para mensagens ao usuário.
     */
    public JLabel getStatusLabel() {
        return statusLabel;
    }
}
