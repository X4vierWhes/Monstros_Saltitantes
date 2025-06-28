package org.example.controller;

import org.example.model.CreaturesPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

/**
 * Classe responsável por inicializar a interface gráfica do jogo "Saltitantes".
 *
 * <p>Ela cria a janela principal com um painel de bolas {@link org.example.model.CreaturesPanel},
 * adiciona botões de interação e gerencia a inserção de bolas em posições aleatórias.</p>
 *
 * @author ValentinaClash
 * @version 1.0
 * @see CreaturesPanel
 */
public class SimulationController {

    /** Largura da janela. */
    private static int WIDTH = 720;

    /** Altura da janela. */
    private static int HEIGHT = 480;

    /** Janela principal do aplicativo. */
    private JFrame frame;

    /** Painel que gerencia as bolas. */
    private CreaturesPanel CreaturesPanel;

    /** Gerador de números aleatórios para posições. */
    private Random rand = new Random();

    /**
     * Inicializa a tela principal, cria o painel de bolas, adiciona botões e inicia os timers.
     *
     * @return {@code true} se a tela foi inicializada com sucesso, senão {@code false}.
     */
    public boolean initScreen() {
        try {
            frame = new JFrame("Saltitantes");
            frame.setSize(WIDTH, HEIGHT);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            CreaturesPanel = new CreaturesPanel(WIDTH, HEIGHT);
            CreaturesPanel.setLayout(null);
            frame.add(CreaturesPanel);

            addButton();
            addBallInRandomPos();

            frame.setVisible(true);
            CreaturesPanel.startPhisycsTimer();
            CreaturesPanel.startUpdateTimer();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Adiciona o botão "ADD BALL" à interface gráfica para permitir ao usuário adicionar novas bolas.
     *
     * @return {@code true} se o botão foi adicionado com sucesso, senão {@code false}.
     */
    private boolean addButton() {
        try {
            JButton button = new JButton("ADD BALL");
            int buttonWIDTH = 100;
            int buttonHEIGHT = 25;
            button.setBounds(WIDTH - buttonWIDTH - 20, 10, buttonWIDTH, buttonHEIGHT);
            button.setBackground(Color.BLUE);
            button.setForeground(Color.WHITE);
            CreaturesPanel.add(button);
            CreaturesPanel.setComponentZOrder(button, 0);
            button.addActionListener(e -> addBallInRandomPos());
        } catch (Exception e) {
            System.err.println("Não foi possivel adicionar o botão");
            return false;
        }
        return true;
    }

    /**
     * Adiciona uma nova bola em uma posição horizontal aleatória dentro do painel.
     *
     * @return {@code true} se a bola foi adicionada com sucesso, senão {@code false}.
     */
    public boolean addBallInRandomPos() {
        try {
            int randomX = rand.nextInt(WIDTH - org.example.model.CreaturesPanel.CREATURE_SIZE);
            CreaturesPanel.addCreature(randomX);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
