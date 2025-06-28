package org.example.model;

import javax.swing.*;

public class Guardian extends Creature{
    /**
     * Construtor da classe Creature.
     *
     * @param x     Posição inicial no eixo X.
     * @param y     Posição inicial no eixo Y.
     * @param spdX  Velocidade horizontal inicial.
     * @param spdY  Velocidade vertical inicial.
     * @param label JLabel associado à representação visual da bola.
     */
    public Guardian(int x, int y, int spdX, int spdY, JLabel label) {
        super(x, y, spdX, spdY, label);
        this.money = 0.0;
    }
}
