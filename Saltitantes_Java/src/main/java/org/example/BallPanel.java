package org.example;

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
 * @see Ball
 */
public class BallPanel extends JPanel {

    /** Tamanho padrão da bola em pixels. */
    public static final int BALL_SIZE = 50;

    /** Posição Y que representa o chão. */
    private final int groundY;

    /** Lista de todas as bolas presentes no painel. */
    private final ArrayList<Ball> balls = new ArrayList<>();

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
    public BallPanel(int width, int height) {
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(width, height));
        groundY = height - BALL_SIZE - 40;
    }

    /**
     * Adiciona uma nova bola ao painel na posição horizontal especificada.
     *
     * @param posX Posição X inicial da bola.
     */
    public void addBall(int posX) {
        int spdX = 1;
        int spdY = rand.nextInt(6) - 3;

        JLabel label = new JLabel();
        label.setForeground(Color.WHITE);
        label.setBounds(posX, groundY - 20, BALL_SIZE, 20);
        label.setHorizontalAlignment(SwingConstants.CENTER);

        balls.add(new Ball(posX, groundY, spdX, spdY, label));
        label.setText("R$ " + (balls.getLast().money / 100.0));
        balls.getLast().x = calcNextPosition(balls.getLast());
        balls.getLast().target = balls.getLast().x;
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
    private boolean isCanUpdate() {
        for (Ball ball : balls) {
            if (ball.canMove) {
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

        balls.removeIf(ball -> ball.money <= 0.0);

        if (balls.isEmpty()) {
            return false;
        }

        for (Ball ball : balls) {
            if (ball.canTheft) {
                thiefNeighbor(ball);
                ball.canMove = true;
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
    public boolean thiefNeighbor(Ball thief) {
        if (balls.size() <= 1) return false;

        if (isCanUpdate()) {
            int closerIndex = 0;
            int index = 0;
            int closest_distance = getWidth() - BALL_SIZE;
            int aux_distance;

            for (Ball neighbor : balls) {
                if (thief != neighbor) {
                    aux_distance = Math.abs(thief.x - neighbor.x);
                    if (aux_distance <= closest_distance) {
                        closest_distance = aux_distance;
                        closerIndex = index;
                    }
                }
                index++;
            }

            thief.money += balls.get(closerIndex).money / 2;
            balls.get(closerIndex).money /= 2;

            thief.target = calcNextPosition(thief);
            balls.get(closerIndex).target = calcNextPosition(balls.get(closerIndex));
        }
        return true;
    }

    /**
     * Calcula a próxima posição horizontal da bola na tela com base em seu alvo.
     *
     * @param ball Bola cuja posição será calculada.
     * @return Posição X em pixels na tela.
     */
    public int calcNextPosition(Ball ball) {
        double rawTarget = calcTarget(ball);
        double normalized = normalizedTarget(rawTarget);
        int screenTarget = (int) (normalized * (getWidth() - BALL_SIZE));
        return Math.max(0, Math.min(screenTarget, getWidth() - BALL_SIZE));
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
     * @param ball Bola cujo destino será calculado.
     * @return Valor alvo em coordenadas lógicas.
     */
    public double calcTarget(Ball ball) {
        return ball.x + (rand.nextDouble(2) - 1) * ball.money;
    }

    /**
     * Atualiza a física das bolas (gravidade, pulo e movimentação horizontal).
     *
     * @return true se a atualização ocorreu normalmente, false se não há bolas.
     */
    public boolean phisycsUpdate() {
        if (balls.isEmpty()) {
            return false;
        }

        if (canUpdate) {
            for (Ball ball : balls) {
                // Atualização vertical
                ball.spdY += grav;
                ball.y += ball.spdY;

                if (ball.y >= groundY) {
                    ball.y = groundY;
                    ball.spdY = jumpForce;
                }

                // Atualização horizontal (movimento em direção ao alvo)
                if (balls.get(moveIndex).canMove) {
                    Ball moving = balls.get(moveIndex);

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
                    moveIndex = (moveIndex + 1) % balls.size();
                }

                ball.label.setText("R$ " + (ball.money / 100.0));
                ball.label.setBounds(ball.x, ball.y - 20, BALL_SIZE, 20);
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
        g.setColor(Color.BLUE);
        for (Ball ball : balls) {
            g.fillOval(ball.x, ball.y, BALL_SIZE, BALL_SIZE);
        }
    }

    /**
     * Retorna a largura atual do painel.
     *
     * @return Largura em pixels.
     */
    public int _getWidht() {
        return getWidth();
    }

    /**
     * Remove uma bola específica do painel.
     *
     * @param remove Bola a ser removida.
     */
    public void removeBall(Ball remove) {
        balls.remove(remove);
    }

    /**
     * Retorna a última bola adicionada à lista.
     *
     * @return Última bola da lista.
     */
    public Ball getLast() {
        return balls.getLast();
    }
}
