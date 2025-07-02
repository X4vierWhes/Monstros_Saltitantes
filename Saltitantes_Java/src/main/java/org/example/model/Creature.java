package org.example.model;

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
public class Creature {

    /** Coordenada X da bola. */
    public int x;

    /** Coordenada Y da bola. */
    int y;

    /** Velocidade vertical da bola. */
    int spdY = 0;

    /** Velocidade horizontal da bola. */
    int spdX = 0;

    /** Dinheiro associado à bola. */
    public double gold = 1000000;

    /** Indica se a bola pode se mover. */
    public boolean canMove = false;

    /** Indica se a bola pode realizar roubo. */
    boolean canTheft = true;

    /** Indica se é um cluster */
    boolean isCluster = false;

    /** Indica se é um guardião */
    boolean isGuardian = false;

    /** Índice do alvo atual da bola (possivelmente outra bola ou jogador). */
    int target = 0;

    /** Rótulo visual associado à bola (usado para exibição gráfica). */
    JLabel label;

    /**
     * Construtor da classe Creature.
     *
     * @param x     Posição inicial no eixo X.
     * @param y     Posição inicial no eixo Y.
     * @param spdX  Velocidade horizontal inicial.
     * @param spdY  Velocidade vertical inicial.
     * @param label JLabel associado à representação visual da bola.
     */
    public Creature(int x, int y, int spdX, int spdY, JLabel label) {
        this.x = x;
        this.y = y;
        this.spdX = spdX;
        this.spdY = spdY;
        this.label = label;
    }
}
