package org.example.model;

import org.example.model.Creature;
import org.w3c.dom.css.RGBColor;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Painel gráfico responsável por gerenciar e exibir múltiplas bolas em movimento.
 *
 * <p>Este painel simula física básica (gravidade e pulo), movimentação horizontal,
 * lógica de roubo entre bolas e atualizações visuais em tempo real com timers.</p>
 *
 * @author ValentinaClash
 * @version 1.0
 * @see Creature
 */
public class CreaturesPanel extends JPanel {

    /** Tamanho padrão da bola em pixels. */
    public static final int CREATURE_SIZE = 50;

    /** Posição Y que representa o chão. */
    private final int groundY;

    /** Lista de todas as bolas presentes no painel. */
    private final ArrayList<Creature> Creatures = new ArrayList<>();

    /** Timer que controla a física das bolas (gravidade, movimento). */
    private Timer phisycsTimer;

    /** Timer que dispara atualizações periódicas nas bolas (roubo e movimentação). */
    private Timer updateTimer;

    /** Valor da gravidade aplicada às bolas. */
    private final float grav = 1.0f;

    /** Força vertical aplicada no "pulo" das bolas. */
    private final int jumpForce = -15;

    /** Gerador de números aleatórios para movimentação e cálculo de alvo. */
    private Random rand = new Random();

    /** Contador de interações (ticks de atualização). */
    private int interacao = 0;

    /** Flag que alterna o momento de atualizar ou não. */
    private boolean canUpdate = true;

    /** Índice atual da bola que está se movendo. */
    private static int moveIndex = 0;

    /**
     * Construtor do painel de bolas.
     *
     * @param width  Largura do painel.
     * @param height Altura do painel.
     */
    public CreaturesPanel(int width, int height) {
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(width, height));
        groundY = height - CREATURE_SIZE - 40;
    }

    /**
     * Adiciona uma nova bola ao painel na posição horizontal especificada.
     *
     * @param posX Posição X inicial da bola.
     */
    public void addCreature(int posX) {
        int spdX = 1;
        int spdY = rand.nextInt(6) - 3;

        JLabel label = new JLabel();
        label.setForeground(Color.WHITE);
        label.setBounds(posX, groundY - 20, CREATURE_SIZE, 20);
        label.setHorizontalAlignment(SwingConstants.CENTER);

        Creatures.add(new Creature(posX, groundY, spdX, spdY, label));
        label.setText("R$ " + (Creatures.getLast().money / 100.0));
        Creatures.getLast().x = calcNextPosition(Creatures.getLast());
        Creatures.getLast().target = Creatures.getLast().x;
        this.add(label);
        this.setComponentZOrder(label, 0);
    }

    /**
     * Inicia o timer de atualização lógica (roubos e movimentação).
     */
    public void startUpdateTimer() {
        updateTimer = new Timer(2500, e -> update());
        updateTimer.start();
    }

    /**
     * Inicia o timer de atualização física (gravidade e pulo).
     */
    public void startPhisycsTimer() {
        phisycsTimer = new Timer(1, e -> phisycsUpdate());
        phisycsTimer.start();
    }

    /**
     * Verifica se todas as bolas estão paradas (não podem se mover).
     *
     * @return true se todas as bolas estão paradas, senão false.
     */
    public boolean isCanUpdate() {
        for (Creature Creature : Creatures) {
            if (Creature.canMove) {
                return false;
            }
        }
        return true;
    }

    /**
     * Atualiza o estado lógico do jogo, removendo bolas sem dinheiro e ativando roubos.
     *
     * @return true se a atualização foi bem-sucedida, false se não há bolas restantes.
     */
    public boolean update() {
        interacao++;
        canUpdate = !canUpdate;

        Creatures.removeIf(Creature -> Creature.money <= 0.0);

        if (Creatures.isEmpty()) {
            return false;
        }

        for (Creature Creature : Creatures) {
            if (Creature.canTheft) {
                thiefNeighbor(Creature);
                Creature.canMove = true;
            }
        }

        canUpdate = !canUpdate;
        return true;
    }

    /**
     * Realiza o roubo entre a bola informada e sua vizinha mais próxima.
     *
     * @param thief A bola que irá roubar.
     * @return true se o roubo foi realizado com sucesso.
     */
    public boolean thiefNeighbor(Creature thief) {
        if (Creatures.size() <= 1) return false;

        if (isCanUpdate()) {
            int closerIndex = 0;
            int index = 0;
            int closest_distance = getWidth() - CREATURE_SIZE;
            int aux_distance;

            for (Creature neighbor : Creatures) {
                if (thief != neighbor) {
                    aux_distance = Math.abs(thief.x - neighbor.x);
                    if (aux_distance <= closest_distance) {
                        closest_distance = aux_distance;
                        closerIndex = index;
                    }
                }
                index++;
            }

            thief.money += Creatures.get(closerIndex).money / 2;
            Creatures.get(closerIndex).money /= 2;

            thief.target = calcNextPosition(thief);
            Creatures.get(closerIndex).target = calcNextPosition(Creatures.get(closerIndex));
        }
        return true;
    }

    /**
     * Calcula a próxima posição horizontal da bola na tela com base em seu alvo.
     *
     * @param Creature Bola cuja posição será calculada.
     * @return Posição X em pixels na tela.
     */
    public int calcNextPosition(Creature Creature) {
        double rawTarget = calcTarget(Creature);
        double normalized = normalizedTarget(rawTarget);
        int screenTarget = (int) (normalized * (getWidth() - CREATURE_SIZE));
        return Math.max(0, Math.min(screenTarget, getWidth() - CREATURE_SIZE));
    }

    /**
     * Normaliza o valor alvo entre 0 e 1.
     *
     * @param noNormalizedTarget Valor alvo não normalizado.
     * @return Valor normalizado entre 0 e 1.
     */
    public double normalizedTarget(double noNormalizedTarget) {
        int minX = -1000000;
        int maxX = 1000000;
        int range = maxX - minX;
        return (noNormalizedTarget - minX) / range;
    }

    /**
     * Calcula o valor bruto de destino horizontal da bola com base na sua posição e dinheiro.
     *
     * @param Creature Bola cujo destino será calculado.
     * @return Valor alvo em coordenadas lógicas.
     */
    public double calcTarget(Creature Creature) {
        return Creature.x + (rand.nextDouble(2) - 1) * Creature.money;
    }

    /**
     * Atualiza a física das bolas (gravidade, pulo e movimentação horizontal).
     *
     * @return true se a atualização ocorreu normalmente, false se não há bolas.
     */
    public boolean phisycsUpdate() {
        if (Creatures.isEmpty()) {
            return false;
        }
        //System.out.println(getWidth());
        if (canUpdate) {
            for (Creature Creature : Creatures) {
                // Atualização vertical
                Creature.spdY += grav;
                Creature.y += Creature.spdY;

                if (Creature.y >= groundY) {
                    Creature.y = groundY;
                    Creature.spdY = jumpForce;
                }

                // Atualização horizontal (movimento em direção ao alvo)
                Creature moving = Creatures.get(moveIndex);
                if (moving.canMove) {

                    if (moving.x == moving.target) {
                        moving.canMove = false;
                        moving.startTimer();
                    }

                    if (moving.x != moving.target) {
                        moving.canTheft = false;
                    }

                    if (moving.target > moving.x) {
                        moving.x += moving.spdX;
                    } else if (moving.target < moving.x) {
                        moving.x -= moving.spdX;
                    }
                } else {
                    moveIndex = (moveIndex + 1) % Creatures.size();
                }

                Creature.label.setText("R$ " + (Creature.money / 100.0));
                Creature.label.setBounds(Creature.x, Creature.y - 20, CREATURE_SIZE, 20);
            }
        }

        repaint();
        return true;
    }

    /**
     * Renderiza as bolas no painel.
     *
     * @param g Objeto gráfico fornecido pelo sistema.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (Creature creature : Creatures) {
            if(!creature.isCluster) {
                g.setColor(new Color(0,0,255));
                g.fillOval(creature.x, creature.y, CREATURE_SIZE, CREATURE_SIZE);
            }else{
                g.setColor(new Color(157,0,255));
                g.fillRect(creature.x, creature.y, CREATURE_SIZE, CREATURE_SIZE);
            }
        }
    }

    /**
     * Remove uma bola específica do painel.
     *
     * @param remove Bola a ser removida.
     */
    public boolean removeCreature(Creature remove) {
        if (Creatures.size() <= 1){
            return false;
        }
        Creatures.remove(remove);
        return true;
    }

    /**
     * Retorna a última bola adicionada à lista.
     *
     * @return Última bola da lista.
     */
    public Creature getLast() {
        if(Creatures.isEmpty()){
            return null;
        }
        return Creatures.getLast();
    }
}
