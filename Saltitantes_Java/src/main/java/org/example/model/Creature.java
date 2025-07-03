package org.example.model;

import javax.swing.*;

/**
 * Representa uma criatura (ou bola) participante da simulação.
 *
 * <p>Cada criatura possui atributos físicos (posição, velocidade), status de ação (roubo e movimento),
 * uma quantidade de ouro que pode ser transferida, e um componente gráfico {@link JLabel}
 * usado para exibição visual no painel.</p>
 *
 * <p>Ela pode assumir diferentes papéis na simulação, como:</p>
 * <ul>
 *     <li><b>Guardião</b>: designado a absorver clusters.</li>
 *     <li><b>Cluster</b>: fusão de múltiplas criaturas com soma de ouro.</li>
 * </ul>
 *
 * @author ValentinaClash
 * @version 1.0
 */
public class Creature {

    /** Posição horizontal (X) da criatura na tela. */
    public int x;

    /** Posição vertical (Y) da criatura na tela. */
    int y;

    /** Velocidade vertical da criatura (usada na simulação de pulo/gravidade). */
    int spdY = 0;

    /** Velocidade horizontal da criatura (usada na movimentação para o alvo). */
    int spdX = 0;

    /** Quantidade de ouro que a criatura possui. */
    public double gold = 1000000;

    /** Indica se a criatura está autorizada a se mover. */
    public boolean canMove = false;

    /** Indica se a criatura pode executar um roubo neste ciclo. */
    public boolean canTheft = true;

    /** Indica se esta criatura representa um cluster (fusão de múltiplas criaturas). */
    public boolean isCluster = false;

    /** Indica se esta criatura é o guardião da simulação. */
    public boolean isGuardian = false;

    /** Posição alvo da criatura na direção X (destino lógico). */
    public int target = 0;

    /** Componente visual associado à criatura para exibição na interface Swing. */
    public JLabel label;

    /**
     * Cria uma nova instância de {@code Creature} com posição, velocidade e componente gráfico definidos.
     *
     * @param x     Posição inicial no eixo X.
     * @param y     Posição inicial no eixo Y.
     * @param spdX  Velocidade horizontal inicial.
     * @param spdY  Velocidade vertical inicial.
     * @param label JLabel associado à representação gráfica da criatura.
     */
    public Creature(int x, int y, int spdX, int spdY, JLabel label) {
        this.x = x;
        this.y = y;
        this.spdX = spdX;
        this.spdY = spdY;
        this.label = label;
    }
}
