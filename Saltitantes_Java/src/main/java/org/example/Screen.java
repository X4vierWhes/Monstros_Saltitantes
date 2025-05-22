package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

/**
 * Classe responsável por inicializar a interface gráfica do jogo "Saltitantes".
 *
 * <p>Ela cria a janela principal com um painel de bolas {@link BallPanel},
 * adiciona botões de interação e gerencia a inserção de bolas em posições aleatórias.</p>
 *
 * @author ValentinaClash
 * @version 1.0
 * @see BallPanel
 */
public class Screen {

    /** Largura da janela. */
    private static int width = 720;

    /** Altura da janela. */
    private static int height = 480;

    /** Janela principal do aplicativo. */
    private JFrame frame;

    /** Painel que gerencia as bolas. */
    private BallPanel ballPanel;

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
            frame.setSize(width, height);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            ballPanel = new BallPanel(width, height);
            ballPanel.setLayout(null);
            frame.add(ballPanel);

            addButton();
            addBallInRandomPos();

            frame.setVisible(true);
            ballPanel.startPhisycsTimer();
            ballPanel.startUpdateTimer();
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
            int buttonWidth = 100;
            int buttonHeight = 25;
            button.setBounds(width - buttonWidth - 20, 10, buttonWidth, buttonHeight);
            button.setBackground(Color.BLUE);
            button.setForeground(Color.WHITE);
            ballPanel.add(button);
            ballPanel.setComponentZOrder(button, 0);
            button.addActionListener(e -> addBallInRandomPos());
        } catch (Exception e) {
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
            int randomX = rand.nextInt(width - BallPanel.BALL_SIZE);
            ballPanel.addBall(randomX);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
