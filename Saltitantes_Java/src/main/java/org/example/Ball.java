package org.example;

import javax.swing.*;

/**
 * Representa uma bola no jogo, que possui posição, velocidade, dinheiro e estado de roubo.
 *
 * <p>A classe controla a movimentação da bola e o tempo de espera (cooldown) após um roubo.
 * Ela também associa a bola a um componente gráfico {@link JLabel}.</p>
 *
 * @author ValentinaClash
 * @version 1.0
 */
public class Ball {

    /** Coordenada X da bola. */
    int x;

    /** Coordenada Y da bola. */
    int y;

    /** Velocidade vertical da bola. */
    int spdY = 0;

    /** Velocidade horizontal da bola. */
    int spdX = 0;

    /** Dinheiro associado à bola. */
    public double money = 1000000;

    /** Indica se a bola pode se mover. */
    boolean canMove = false;

    /** Indica se a bola pode realizar roubo. */
    boolean canTheft = true;

    /** Índice do alvo atual da bola (possivelmente outra bola ou jogador). */
    int target = 0;

    /** Rótulo visual associado à bola (usado para exibição gráfica). */
    JLabel label;

    /** Timer responsável por controlar o tempo de recarga após um roubo. */
    Timer cooldown;

    /**
     * Construtor da classe Ball.
     *
     * @param x     Posição inicial no eixo X.
     * @param y     Posição inicial no eixo Y.
     * @param spdX  Velocidade horizontal inicial.
     * @param spdY  Velocidade vertical inicial.
     * @param label JLabel associado à representação visual da bola.
     */
    public Ball(int x, int y, int spdX, int spdY, JLabel label) {
        this.x = x;
        this.y = y;
        this.spdX = spdX;
        this.spdY = spdY;
        this.label = label;
        cooldown = new Timer(3000, e -> thiefTimer());
    }

    /**
     * Inicia o temporizador de recarga de roubo se ainda não estiver ativo.
     * Após 3 segundos, o roubo será permitido novamente.
     */
    public void startTimer() {
        if (!cooldown.isRunning()) {
            canTheft = false;
            cooldown.setRepeats(false);
            cooldown.start();
        }
    }

    /**
     * Habilita o roubo novamente e interrompe o temporizador se estiver em execução.
     */
    public void thiefTimer() {
        canTheft = true;
        if (cooldown.isRunning()) {
            cooldown.stop();
        }
    }
}
