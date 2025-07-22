package org.example.controller;

import org.example.model.SQLite;
import org.example.model.User;
import org.example.view.SimulationView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controlador responsável por gerenciar a lógica da simulação de criaturas.
 *
 * <p>Este controlador conecta a {@link SimulationView} com o modelo {@link SQLite} e os dados do {@link User},
 * lidando com os eventos da interface gráfica e controlando o painel de criaturas durante a simulação.</p>
 *
 * <p>Ele também cuida do encerramento seguro da simulação e do retorno à tela principal do usuário.</p>
 *
 * @author ValentinaClash
 */
public class SimulationController {

    /** Referência para a interface gráfica da simulação. */
    private final SimulationView view;

    /** Usuário atualmente autenticado. */
    private User user;

    /** Conexão com o banco de dados SQLite. */
    private SQLite bd;

    /**
     * Construtor do controlador da simulação.
     *
     * <p>Inicializa a interface gráfica, adiciona a primeira criatura no painel,
     * e inicia o temporizador de física da simulação.</p>
     *
     * @param user Usuário que está executando a simulação.
     */
    public SimulationController(User user) {
        this.user = user;
        this.bd = new SQLite();
        this.view = new SimulationView(user, this.bd);
        initListeners();
        view.getCreaturesPanel().addCreature(view.getRandomX());
        view.getCreaturesPanel().startPhisycsTimer();
    }

    /**
     * Inicializa os listeners para os botões da interface gráfica:
     * <ul>
     *     <li><b>Adicionar:</b> adiciona uma nova criatura em uma posição aleatória.</li>
     *     <li><b>Iniciar:</b> inicia a simulação a partir de uma posição aleatória.</li>
     *     <li><b>Sair:</b> encerra a simulação, fecha o banco de dados e retorna à tela do usuário.</li>
     * </ul>
     */
    private void initListeners() {
        // Botão para adicionar criatura
        view.getBtnAddBall().addActionListener(e -> {
            int x = view.getRandomX();
            view.getCreaturesPanel().addCreature(x);
        });

        // Botão para iniciar a simulação
        view.getBtnInit().addActionListener(e -> {
            int x = view.getRandomX();
            view.getCreaturesPanel().initSimulation(x);
        });

        // Botão para sair da simulação
        view.getBtnQuit().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bd.editUserByUsername(user.getUserName(), user);
                bd.close();

                view.getCreaturesPanel().stopSimulation();

                view.dispose();
                javax.swing.SwingUtilities.invokeLater(() -> {
                    UserController userController = new UserController(user);
                });
            }
        });
    }
}
