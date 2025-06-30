package org.example.view;

import org.example.controller.SimulationController;
import org.example.model.CreaturesPanel;
import org.example.model.User;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class SimulationView extends JFrame {

    /** Largura da janela. */
    private static final int WIDTH = 720;

    /** Altura da janela. */
    private static final int HEIGHT = 480;

    private static final int buttonWIDTH = 100;
    private static final int buttonHEIGHT = 25;

    /** Painel que gerencia as bolas. */
    private CreaturesPanel creaturesPanel;

    private JButton btnAddBall;

    private  JButton btnInit;

    /** Gerador de números aleatórios para posições. */
    private Random rand = new Random();

    private User user;

    /**
     * Inicializa a tela principal, cria o painel de bolas, adiciona botões e inicia os timers.
     */
    public  SimulationView(User user) {
        try {
            this.user = user;
            /** Janela principal do aplicativo. */
            this.setTitle("Saltitantes");
            this.setSize(WIDTH, HEIGHT);
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setLayout(null);
            this.setLocationRelativeTo(null);
            this.setResizable(false);


            // Painel de simulação
            this.creaturesPanel = new CreaturesPanel(WIDTH, HEIGHT, user);
            this.creaturesPanel.setLayout(null);
            this.creaturesPanel.setBounds(0, 0, WIDTH, HEIGHT);
            this.add(creaturesPanel);

            ImageIcon imageIcon = user.getAVATAR();
            JLabel avatarLabel = new JLabel(imageIcon);
            avatarLabel.setBorder(BorderFactory.createLineBorder(new Color(255,0,129)));

            JPanel avatarPanel = new JPanel();
            avatarPanel.setLayout(new BorderLayout());
            avatarPanel.setBounds(WIDTH / 25, HEIGHT / 25, 100, 100);
            avatarPanel.setOpaque(false);
            avatarPanel.add(avatarLabel, BorderLayout.CENTER);
            creaturesPanel.add(avatarPanel);
            creaturesPanel.setComponentZOrder(avatarPanel, 0);


            JLabel userLabel = new JLabel("User: " + user.getUserName());
            userLabel.setBounds(WIDTH / 25, HEIGHT / 25 + 110, 150, 25);
            userLabel.setForeground(new Color(255,0,129));
            creaturesPanel.add(userLabel);
            creaturesPanel.setComponentZOrder(userLabel, 0);

            addBallButton();
            addInitButton();

            this.setVisible(true);

        } catch (Exception e) {
        }
    }

    private void addInitButton() {
        try{
            btnInit = new JButton("Init");
            btnInit.setBounds(WIDTH - buttonWIDTH - 20, 10, buttonWIDTH, buttonHEIGHT);
            btnInit.setBackground(Color.BLUE);
            btnInit.setForeground(Color.WHITE);
            creaturesPanel.add(btnInit);
            creaturesPanel.setComponentZOrder(btnInit, 0);
        }catch (Exception e){
            System.err.println("Não foi possivel adicionar botão de iniciar simulação");
        }
    }

    /**
     * Adiciona o botão "ADD BALL" à interface gráfica para permitir ao usuário adicionar novas bolas.
     */
    private void addBallButton() {
        try {
            btnAddBall = new JButton("ADD BALL");
            btnAddBall.setBounds(WIDTH - 2 * buttonWIDTH - 40, 10, buttonWIDTH, buttonHEIGHT);
            btnAddBall.setBackground(Color.BLUE);
            btnAddBall.setForeground(Color.WHITE);
            creaturesPanel.add(btnAddBall);
            creaturesPanel.setComponentZOrder(btnAddBall, 0);
        } catch (Exception e) {
            System.err.println("Não foi possivel adicionar o botão");
        }
    }

    // ==== Métodos públicos (acesso pelo Controller) ====

    public JButton getBtnAddBall() {
        return btnAddBall;
    }

    public JButton getBtnInit() {
        return btnInit;
    }

    public CreaturesPanel getCreaturesPanel() {
        return creaturesPanel;
    }

    public int getRandomX() {
        return rand.nextInt(WIDTH - CreaturesPanel.CREATURE_SIZE);
    }
}
