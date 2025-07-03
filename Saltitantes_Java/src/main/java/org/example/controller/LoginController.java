package org.example.controller;

import org.example.model.SQLite;
import org.example.model.User;
import org.example.view.LoginView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controlador responsável por gerenciar as ações da tela de login.
 * Ele lida com a autenticação de usuários e criação de novas contas,
 * utilizando a interface {@link LoginView} e o banco de dados {@link SQLite}.
 * @author ValentinaClash
 */
public class LoginController {
    /** Visão de login associada a este controlador. */
    private LoginView view;

    /** Controlador do usuário que será inicializado após o login. */
    private UserController user;

    /** Instância do banco de dados SQLite para manipulação de dados de usuário. */
    private SQLite bd;

    /**
     * Construtor padrão do LoginController.
     * Inicializa a visualização, conexão com o banco de dados e os listeners dos botões.
     */
    public LoginController() {
        bd = new SQLite();
        view = new LoginView();
        initListeners();
    }

    /**
     * Inicializa os ouvintes de eventos para os botões de login e criação de conta.
     */
    private void initListeners() {
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

    /**
     * Realiza o processo de criação de conta de usuário.
     * Caso o nome de usuário já exista, exibe uma mensagem de erro.
     * Caso contrário, insere o novo usuário no banco e exibe confirmação.
     */
    private boolean signIn() {
        String userName = view.getUsernameField().getText();
        String passWord = new String(view.getPasswordField().getPassword());
        User newUser = new User(userName, passWord, "common");

        if(userName.isEmpty() || passWord.isEmpty()){
            view.getStatusLabel().setForeground(Color.red);
            view.getStatusLabel().setText("Campo de username ou password em branco");
            return false;
        }else if (bd.insertIntoUsers(newUser)) {
            view.getStatusLabel().setForeground(new Color(0, 128, 0));
            view.getStatusLabel().setText("Conta criada com sucesso!");
            return true;
        } else {
            User user = bd.findUserByUsername(userName);
            if (user != null && user.getUserName().equals(userName)) {
                view.getStatusLabel().setForeground(Color.RED);
                view.getStatusLabel().setText("Nome de usuário ocupado!");
                return false;
            }
        }
        return false;
    }

    /**
     * Realiza o processo de autenticação do usuário.
     * Caso as credenciais sejam válidas, exibe mensagem de boas-vindas
     * e inicia o controlador do usuário correspondente.
     * Caso contrário, exibe mensagem de erro.
     */
    private boolean login() {
        String userName = view.getUsernameField().getText();
        String passWord = new String(view.getPasswordField().getPassword());
        User log = bd.findUserByUsername(userName);

        if (log != null && log.getPassWord().equals(passWord)) {
            view.getStatusLabel().setForeground(new Color(0, 128, 0));
            view.getStatusLabel().setText("Login realizado com sucesso!");

            JOptionPane.showMessageDialog(view, "Bem-vindo, " + userName + "!");
            bd.close();
            view.dispose();
            javax.swing.SwingUtilities.invokeLater(() -> {
                user = new UserController(log);
            });
            return true;
        } else {
            view.getStatusLabel().setForeground(Color.RED);
            view.getStatusLabel().setText("Usuário ou password inválidos.");
            return false;
        }
    }
}
