package org.example.controller;

import org.example.model.SQLite;
import org.example.model.User;
import org.example.view.UserView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controlador responsável por gerenciar a tela principal do usuário após o login.
 *
 * <p>Este controlador conecta a {@link UserView} com o modelo {@link SQLite} e com os demais controladores
 * que permitem executar simulações, visualizar resultados, alterar o avatar ou excluir a conta.</p>
 *
 * <p>Ele também lida com o encerramento da sessão e navegação entre as telas da aplicação.</p>
 *
 * @author ValentinaClash
 */
public class UserController {

    /** Referência à interface gráfica principal do usuário. */
    private final UserView view;

    /** Usuário atualmente autenticado. */
    private final User user;

    /** Controlador da simulação, instanciado sob demanda. */
    private SimulationController simulation;

    /** Controlador dos resultados, instanciado sob demanda. */
    private ResultController result;

    /** Conexão com o banco de dados SQLite. */
    private SQLite bd;

    /**
     * Construtor do controlador do usuário.
     *
     * <p>Inicializa a view e os listeners, conectando as ações da interface com o modelo de dados.</p>
     *
     * @param user Usuário autenticado no sistema.
     */
    public UserController(User user){
        this.bd = new SQLite();
        this.view = new UserView(user);
        this.user = user;
        initListeners();
    }

    /**
     * Inicializa os listeners dos botões da tela do usuário.
     *
     * <ul>
     *     <li><b>Alterar Avatar:</b> Permite ao usuário alterar sua imagem de perfil.</li>
     *     <li><b>Iniciar Simulação:</b> Fecha a tela atual e inicia uma nova simulação.</li>
     *     <li><b>Ver Resultados:</b> Fecha a tela atual e abre a visualização de resultados.</li>
     *     <li><b>Excluir Conta:</b> Solicita confirmação, exclui o usuário do banco e retorna à tela de login.</li>
     *     <li><b>Sair:</b> Fecha a tela atual e retorna à tela de login.</li>
     * </ul>
     */
    private void initListeners() {

        // Listener do botão "Alterar Avatar"
        view.getChangeAvatarButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.changeAvatar(user);
                bd.editUserByUsername(user.getUserName(), user);
            }
        });

        // Listener do botão "Iniciar Simulação"
        view.getInitButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bd.close();
                view.dispose();
                javax.swing.SwingUtilities.invokeLater(() -> {
                    simulation = new SimulationController(user);
                });
            }
        });

        // Listener do botão "Resultados"
        view.getResultButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bd.close();
                view.dispose();
                javax.swing.SwingUtilities.invokeLater(() -> {
                    result = new ResultController(user);
                });
            }
        });

        // Listener do botão "Excluir Conta"
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
                    bd.close();
                    view.dispose();
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        LoginController login = new LoginController();
                    });
                }
            }
        });

        // Listener do botão "Sair"
        view.getQuitButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bd.close();
                view.dispose();
                javax.swing.SwingUtilities.invokeLater(() -> {
                    LoginController login = new LoginController();
                });
            }
        });
    }
}
