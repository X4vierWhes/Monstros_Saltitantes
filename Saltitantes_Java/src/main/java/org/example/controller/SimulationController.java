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
    private static final int WIDTH = 720;

    /** Altura da janela. */
    private static final int HEIGHT = 480;

    private static final int buttonWIDTH = 100;
    private static final int buttonHEIGHT = 25;

    /** Painel que gerencia as bolas. */
    private CreaturesPanel CreaturesPanel;

    private static boolean simulation = false;

    /** Gerador de números aleatórios para posições. */
    private Random rand = new Random();

    /**
     * Inicializa a tela principal, cria o painel de bolas, adiciona botões e inicia os timers.
     */
    public void initScreen() {
        try {
            /** Janela principal do aplicativo. */
            JFrame frame = new JFrame("Saltitantes");
            frame.setSize(WIDTH, HEIGHT);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            CreaturesPanel = new CreaturesPanel(WIDTH, HEIGHT);
            CreaturesPanel.setLayout(null);
            frame.add(CreaturesPanel);

            addBallButton();
            addInitButton();
            addBallInRandomPos();

            frame.setVisible(true);
            CreaturesPanel.startPhisycsTimer();
            //CreaturesPanel.startUpdateTimer();
        } catch (Exception e) {
        }
    }

    private void addInitButton() {
        try{
            JButton button = new JButton("Init");
            button.setBounds(WIDTH - buttonWIDTH - 20, 10, buttonWIDTH, buttonHEIGHT);
            button.setBackground(Color.BLUE);
            button.setForeground(Color.WHITE);
            CreaturesPanel.add(button);
            CreaturesPanel.setComponentZOrder(button, 0);
            button.addActionListener(e -> initSimulation());
        }catch (Exception e){
            System.err.println("Não foi possivel adicionar botão de iniciar simulação");
        }
    }

    /**
     * Adiciona o botão "ADD BALL" à interface gráfica para permitir ao usuário adicionar novas bolas.
     */
    private void addBallButton() {
        try {
            JButton button = new JButton("ADD BALL");
            button.setBounds(WIDTH - 2 * buttonWIDTH - 40, 10, buttonWIDTH, buttonHEIGHT);
            button.setBackground(Color.BLUE);
            button.setForeground(Color.WHITE);
            CreaturesPanel.add(button);
            CreaturesPanel.setComponentZOrder(button, 0);
            button.addActionListener(e -> addBallInRandomPos());
        } catch (Exception e) {
            System.err.println("Não foi possivel adicionar o botão");
        }
    }

    /**
     * Adiciona uma nova bola em uma posição horizontal aleatória dentro do painel.
     */
    public void addBallInRandomPos() {
        try {
            int randomX = rand.nextInt(WIDTH - org.example.model.CreaturesPanel.CREATURE_SIZE);
            CreaturesPanel.addCreature(randomX);
        } catch (Exception e) {
            System.err.println("Não foi possivel adicionar bola");
        }
    }

    public  void initSimulation(){
        System.out.println("Simulação iniciada");
        int randomX = rand.nextInt(WIDTH - org.example.model.CreaturesPanel.CREATURE_SIZE);
        CreaturesPanel.initSimulation(randomX);
    }
}
