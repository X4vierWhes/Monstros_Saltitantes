package org.example.controller;

import org.example.model.SQLite;
import org.example.model.User;
import org.example.view.ResultView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controlador responsável por gerenciar as ações da tela de resultados da simulação.
 *
 * <p>Este controlador conecta a {@link ResultView} com o modelo {@link SQLite} e o {@link User},
 * permitindo que o usuário visualize os resultados da simulação e possa retornar à tela do usuário.
 * </p>
 *
 * <p>Ao encerrar a visualização de resultados, a conexão com o banco de dados é fechada
 * e a tela principal do usuário é reaberta.</p>
 *
 * @author ValentinaClash
 */
public class ResultController {
    /** Referência para a visualização dos resultados. */
    private ResultView view;

    /** Conexão com o banco de dados SQLite. */
    private SQLite bd;

    /** Usuário atualmente autenticado. */
    private User user;

    /**
     * Construtor do controlador da tela de resultados.
     *
     * @param user Usuário logado que visualizou a simulação.
     */
    public ResultController(User user) {
        this.user = user;
        this.bd = new SQLite();
        view = new ResultView(this.user, this.bd);
        initListeners();
    }

    /**
     * Inicializa os listeners dos componentes da view, como o botão de saída.
     *
     * <p>Quando o botão "Sair" é clicado, o banco de dados é fechado,
     * a janela atual é encerrada e a tela principal do usuário é exibida.</p>
     */
    private void initListeners() {
        view.getQuitButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bd.close();
                view.dispose();
                javax.swing.SwingUtilities.invokeLater(() -> {
                    UserController suser = new UserController(user);
                });
            }
        });
    }
}
