package org.example.view;

import org.example.model.CreaturesPanel;
import org.example.model.SQLite;
import org.example.model.User;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

/**
 * Janela principal da aplicação responsável pela simulação das criaturas saltitantes.
 *
 * <p>Esta classe exibe o painel de simulação {@link CreaturesPanel}, mostra o avatar e
 * informações do usuário, e inclui botões para controle da simulação: adicionar bola,
 * iniciar e sair.</p>
 *
 * <p>Os componentes gráficos são posicionados manualmente e a janela tem tamanho fixo.</p>
 *
 * @author ValentinaClash
 * @version 1.0
 * @see CreaturesPanel
 * @see User
 * @see SQLite
 */
public class SimulationView extends JFrame {

    /** Largura fixa da janela. */
    private static final int WIDTH = 720;

    /** Altura fixa da janela. */
    private static final int HEIGHT = 480;

    /** Largura padrão dos botões. */
    private static final int buttonWIDTH = 100;

    /** Altura padrão dos botões. */
    private static final int buttonHEIGHT = 25;

    /** Painel responsável pela simulação e exibição das criaturas. */
    private CreaturesPanel creaturesPanel;

    /** Botão para adicionar uma nova bola/criatura. */
    private JButton btnAddBall;

    /** Botão para iniciar a simulação. */
    private JButton btnInit;

    /** Botão para sair ou voltar da tela de simulação. */
    private JButton btnQuit;

    /** Gerador de números aleatórios utilizado para posicionar as criaturas. */
    private Random rand = new Random();

    /** Usuário autenticado no sistema. */
    private User user;

    /** Conexão com o banco de dados SQLite. */
    private SQLite bd;

    /**
     * Construtor que inicializa a interface da simulação com o usuário e banco de dados fornecidos.
     * Configura a janela, cria o painel de criaturas, adiciona botões e componentes gráficos.
     *
     * @param user Usuário autenticado para a simulação.
     * @param bd Instância do banco de dados SQLite.
     */
    public SimulationView(User user, SQLite bd) {
            this.bd = bd;
            this.user = user;

            this.setTitle("Saltitantes");
            this.setSize(WIDTH, HEIGHT);
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setLayout(null);
            this.setLocationRelativeTo(null);
            this.setResizable(false);

            // Inicializa o painel de simulação
            this.creaturesPanel = new CreaturesPanel(WIDTH, HEIGHT, user, bd);
            this.creaturesPanel.setLayout(null);
            this.creaturesPanel.setBounds(0, 0, WIDTH, HEIGHT);
            this.add(creaturesPanel);

            // Adiciona avatar do usuário
            ImageIcon imageIcon = user.getAVATAR();
            JLabel avatarLabel = new JLabel(imageIcon);
            avatarLabel.setBorder(BorderFactory.createLineBorder(new Color(255, 0, 129)));

            JPanel avatarPanel = new JPanel(new BorderLayout());
            avatarPanel.setBounds(WIDTH / 25, HEIGHT / 25, 100, 100);
            avatarPanel.setOpaque(false);
            avatarPanel.add(avatarLabel, BorderLayout.CENTER);
            creaturesPanel.add(avatarPanel);
            creaturesPanel.setComponentZOrder(avatarPanel, 0);

            // Exibe nome do usuário
            JLabel userLabel = new JLabel("User: " + user.getUserName());
            userLabel.setBounds(WIDTH / 25, HEIGHT / 25 + 110, 150, 25);
            userLabel.setForeground(new Color(255, 0, 129));
            creaturesPanel.add(userLabel);
            creaturesPanel.setComponentZOrder(userLabel, 0);

            // Adiciona os botões da interface
            addBallButton();
            addInitButton();
            addQuitButton();

            this.setVisible(true);
    }

    /**
     * Cria e adiciona o botão "Voltar" que permite sair da tela de simulação.
     */
    private void addQuitButton() {
            btnQuit = new JButton("Voltar");
            btnQuit.setBounds(WIDTH - 3 * buttonWIDTH - 60, 10, buttonWIDTH, buttonHEIGHT);
            btnQuit.setBackground(Color.BLUE);
            btnQuit.setForeground(Color.WHITE);
            creaturesPanel.add(btnQuit);
            creaturesPanel.setComponentZOrder(btnQuit, 0);
    }

    /**
     * Cria e adiciona o botão "Iniciar" para iniciar a simulação.
     */
    private void addInitButton() {
            btnInit = new JButton("Iniciar");
            btnInit.setBounds(WIDTH - buttonWIDTH - 20, 10, buttonWIDTH, buttonHEIGHT);
            btnInit.setBackground(Color.BLUE);
            btnInit.setForeground(Color.WHITE);
            creaturesPanel.add(btnInit);
            creaturesPanel.setComponentZOrder(btnInit, 0);
    }

    /**
     * Cria e adiciona o botão "Adicionar" para permitir que o usuário adicione novas criaturas.
     */
    private void addBallButton() {
            btnAddBall = new JButton("Adicionar");
            btnAddBall.setBounds(WIDTH - 2 * buttonWIDTH - 40, 10, buttonWIDTH, buttonHEIGHT);
            btnAddBall.setBackground(Color.BLUE);
            btnAddBall.setForeground(Color.WHITE);
            creaturesPanel.add(btnAddBall);
            creaturesPanel.setComponentZOrder(btnAddBall, 0);
    }

    /**
     * Retorna o botão para adicionar criaturas.
     * @return JButton para adicionar criaturas.
     */
    public JButton getBtnAddBall() {
        return btnAddBall;
    }

    /**
     * Retorna o botão para iniciar a simulação.
     * @return JButton para iniciar.
     */
    public JButton getBtnInit() {
        return btnInit;
    }

    /**
     * Retorna o painel que gerencia e exibe as criaturas.
     * @return painel de criaturas {@link CreaturesPanel}.
     */
    public CreaturesPanel getCreaturesPanel() {
        return creaturesPanel;
    }

    /**
     * Gera um valor aleatório válido para coordenada X dentro do painel.
     * @return posição X aleatória.
     */
    public int getRandomX() {
        return rand.nextInt(WIDTH - CreaturesPanel.CREATURE_SIZE);
    }

    /**
     * Retorna o botão para sair da tela de simulação.
     * @return botão "Voltar".
     */
    public JButton getBtnQuit() {
        return btnQuit;
    }
}
