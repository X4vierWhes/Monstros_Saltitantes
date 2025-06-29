package org.example.model;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.util.*;
import java.util.List;

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

    public static final int fuseDistance = 50; // tolerância em pixels

    /** Posição Y que representa o chão. */
    private final int groundY;

    /** Lista de todas as bolas presentes no painel. */
    private final List<Creature> Creatures = new ArrayList<>();

    /** Timer que controla a física das bolas (gravidade, movimento). */
    private Timer phisycsTimer;

    /** Timer que dispara atualizações periódicas nas bolas (roubo e movimentação). */
    private Timer updateTimer;

    /** Valor da gravidade aplicada às bolas. */
    private final float grav = 1.0f;

    /** Força vertical aplicada no "pulo" das bolas. */
    private final int jumpForce = -15;

    private static int creaturesMove = 0;

    /** Gerador de números aleatórios para movimentação e cálculo de alvo. */
    private Random rand = new Random();

    /** Contador de interações (ticks de atualização). */
    private int interacao = 0;

    /** Flag que alterna o momento de atualizar ou não. */
    private boolean canUpdate = true;

    private boolean startSimulation = false;

    /** Índice atual da bola que está se movendo. */
    private static int moveIndex = 0;

    private static int ticksToCompleteCycle;
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
        synchronized (Creatures) {
            int spdX = 1;
            int spdY = 1;

            JLabel label = new JLabel();
            label.setForeground(Color.WHITE);
            label.setBounds(posX, groundY - 20, CREATURE_SIZE, 20);
            label.setHorizontalAlignment(SwingConstants.CENTER);

            Creature newCreature = new Creature(posX, groundY, spdX, spdY, label);
            setLabelText(newCreature);
            if (Creatures.isEmpty()) {
                Creatures.add(newCreature);
                newCreature.x = calcNextPosition(newCreature);
                newCreature.target = calcNextPosition(newCreature);
            } else {
                Creature last = Creatures.getLast();
                if (!last.isGuardian) {
                    Creatures.add(newCreature);
                    newCreature.x = calcNextPosition(newCreature);
                    newCreature.target = calcNextPosition(newCreature);
                } else {
                    Creatures.removeLast(); // remove o guardião temporariamente
                    Creatures.add(newCreature);
                    newCreature.x = calcNextPosition(newCreature);
                    newCreature.target = calcNextPosition(newCreature);
                    Creatures.add(last); // reinserir o guardião
                }
            }
        }
    }

    private void createCluster(ArrayList<Creature> creaturesColliding){
        if(creaturesColliding.size() > 1){
            synchronized (Creatures) {

                if(!getLast().isGuardian) return;
                Creature guardian = getLast();
                removeCreature(guardian); //Remove temporiarmente o guardião da lista

                JLabel label = new JLabel();
                label.setForeground(Color.WHITE);
                label.setBounds(creaturesColliding.get(0).x, groundY - 20, CREATURE_SIZE, 20);
                label.setHorizontalAlignment(SwingConstants.CENTER);

                Creature cluster = new Creature(creaturesColliding.getFirst().x, groundY - 20, 1, 0, label);
                cluster.isCluster = true;

                for (Creature aux : creaturesColliding) {
                    cluster.money += aux.money;
                    this.remove(aux.label);
                }

                // Remove todas as criaturas que participaram da fusão
                Creatures.removeAll(creaturesColliding);

                // Adiciona o novo cluster
                Creatures.add(cluster);
                cluster.target = calcNextPosition(cluster);
                cluster.canMove = true;
                setLabelText(cluster);
                Creatures.add(guardian);
            }
        }
    }

    private void createGuardian(int posX){
        synchronized (Creatures){
            int spdX = 1;
            int spdY = rand.nextInt(6) - 3;

            JLabel label = new JLabel();
            label.setForeground(Color.WHITE);
            label.setBounds(posX, groundY - 20, CREATURE_SIZE, 20);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            Guardian guardian = new Guardian(posX, groundY, spdX, spdY, label);
            setLabelText(guardian);
            Creatures.add(guardian);
            guardian.x = calcNextPosition(Creatures.getLast());
            guardian.target = Creatures.getLast().x;
        }
    }

    /**
     * Inicia o timer de atualização lógica (roubos e movimentação).
     */
    public void startUpdateTimer() {
        updateTimer = new Timer(5000, e -> update());
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
        synchronized (Creatures) {
            for (Creature Creature : Creatures) {
                if (Creature.canMove) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Realiza o roubo entre a bola informada e sua vizinha mais próxima.
     *
     * @param thief A bola que irá roubar.
     * @return true se o roubo foi realizado com sucesso.
     */
    public boolean thiefNeighbor(Creature thief) {
        synchronized (Creatures){

            if (Creatures.size() <= 1) return false;

            if (isCanUpdate()) {
                int closerIndex = 0;
                int index = 0;
                int closest_distance = getWidth() - CREATURE_SIZE;
                int aux_distance;

                for (Creature neighbor : Creatures) {
                    if (thief != neighbor && !neighbor.isGuardian) {
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
     * Atualiza o estado lógico do jogo, removendo bolas sem dinheiro e ativando roubos.
     *
     * @return true se a atualização foi bem-sucedida, false se não há bolas left.
     */
    public boolean update() {
        synchronized (Creatures) {
            interacao++;
            canUpdate = !canUpdate;

            if (Creatures.isEmpty()) {
                return false;
            }

            for (Creature creature : Creatures) {
                if (creature.canTheft && !creature.isGuardian) {
                    thiefNeighbor(creature);
                    creature.canMove = true;
                }
            }
            canUpdate = !canUpdate;
            return true;
        }
    }

    /**
     * Atualiza a física das bolas (gravidade, pulo e movimentação horizontal).
     *
     * @return true se a atualização ocorreu normalmente, false se não há bolas.
     */
    public boolean phisycsUpdate() {

        synchronized (Creatures) {
            if (Creatures.isEmpty()) {
                return false;
            }

            if(Creatures.size() <= 2 && startSimulation){
                stopSimulation();
            }

            List<Creature> snapshot; //Copia da lista de criaturas para evitar erros
            snapshot = new ArrayList<>(Creatures);

            if (canUpdate) {
                if(creaturesMove >= snapshot.size()){
                    System.err.println(creaturesMove + " / " + snapshot.size() + " / " + moveIndex);
                    checkCluster();
                    checkGuardian();
                    creaturesMove = 0;
                    snapshot = new ArrayList<>(Creatures);
                    System.err.println(creaturesMove + " / " + snapshot.size() + " / " + moveIndex);
                }

                for (Creature creature : snapshot) {
                    // Atualização vertical (PULO) se nao for cluster

                    creature.spdY += grav;
                    creature.y += creature.spdY;

                    if (creature.y >= groundY) {
                        creature.y = groundY;
                        creature.spdY = jumpForce;
                    }

                    if (moveIndex >= snapshot.size()) {
                        moveIndex = 0;
                    }

                    // Atualização horizontal (movimento em direção ao alvo)
                    Creature moving = snapshot.get(moveIndex); //Criatura que vai se mover

                    if (moving.canMove && startSimulation) {
                        moving.label.setForeground(new Color(255, 0, 0));
                        if (moving.x == moving.target) {
                            moving.canMove = false;
                            creaturesMove++;
                        }

                        if (moving.x != moving.target) {
                            moving.canTheft = false;
                        }

                        if (moving.target > moving.x) {
                            moving.x += moving.spdX;
                        } else if (moving.target < moving.x) {
                            moving.x -= moving.spdX;
                        }

                    } else if(startSimulation) {
                        moving.label.setForeground(new Color(255, 255, 255));
                        moveIndex = (moveIndex + 1) % snapshot.size();

                        if (moveIndex >= snapshot.size() - 1 && startSimulation) {
                            for (Creature aux : snapshot) {
                                aux.canTheft = true; //Autoriza criaturas a roubar novamente
                            }
                            moveIndex = 0;
                        }

                    }

                    setLabelText(creature);
                    creature.label.setBounds(creature.x, creature.y - 20, CREATURE_SIZE, 20);
                }
            }

            repaint();
            return true;
        }
    }

    private void checkGuardian() {
        synchronized (Creatures) {
            Creature guardian = getLast();

            if(guardian.isGuardian){
                List<Creature> toRemove = new ArrayList<>();

                for(Creature c: Creatures){
                    if(c.isCluster && Math.abs(c.x - guardian.x) <= fuseDistance){
                        guardian.money += c.money;
                        toRemove.add(c);
                    }
                }
                if(!toRemove.isEmpty()) {
                    for (Creature c : toRemove) {
                        removeCreature(c);
                    }
                    this.remove(guardian.label);
                   setLabelText(guardian);
                   guardian.target = calcNextPosition(guardian);
                   guardian.canMove = true;
                }
            }
        }
    }

    /**
     * Checa se precisa criar novos clusters (duas ou mais criaturas na mesma posição
     */
    private void checkCluster() {
        List<Creature> left = new ArrayList<>(Creatures); // cópia para controle
        List<List<Creature>> group = new ArrayList<>();

        while (!left.isEmpty()) {
            Creature base = left.removeFirst();

            if (base.isGuardian) continue;

            List<Creature> grupo = new ArrayList<>();
            grupo.add(base);

            Iterator<Creature> it = left.iterator();
            while (it.hasNext()) {
                Creature c = it.next();
                if (!c.isGuardian && Math.abs(c.x - base.x) <= fuseDistance) {
                    grupo.add(c);
                    it.remove();
                }
            }

            if (grupo.size() > 1) {
                    group.add(grupo);
            }
        }

        for (List<Creature> grupo : group) {
            createCluster(new ArrayList<>(grupo));
        }
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
            if(creature.isCluster) {
                g.setColor(new Color(157,0,255));
                g.fillRect(creature.x, creature.y, CREATURE_SIZE, CREATURE_SIZE);
            }else if(creature.isGuardian){
                g.setColor(new Color(0, 255, 0));
                int halfSize = CREATURE_SIZE / 2;
                int[] xPoints = {
                        creature.x + halfSize,
                        creature.x,
                        creature.x + CREATURE_SIZE
                };
                int[] yPoints = {
                        creature.y,
                        creature.y + CREATURE_SIZE,
                        creature.y + CREATURE_SIZE
                };
                g.fillPolygon(xPoints, yPoints, 3);
            }else{
                g.setColor(new Color(0,0,255));
                g.fillOval(creature.x, creature.y, CREATURE_SIZE, CREATURE_SIZE);
            }
        }
    }

    /**
     * Remove uma bola específica do painel.
     *
     * @param remove Bola a ser removida.
     */
    public boolean removeCreature(Creature remove) {
        synchronized (Creatures) {
            if (Creatures.size() <= 1) {
                return false;
            }
            this.remove(remove.label);
            Creatures.remove(remove);
            return true;
        }
    }

    /**
     * Retorna a última bola adicionada à lista.
     *
     * @return Última bola da lista.
     */
    public Creature getLast() {
        synchronized (Creatures) {
            if (Creatures.isEmpty()) {
                return null;
            }
            return Creatures.getLast();
        }
    }

    public void initSimulation(int randomX) {
        synchronized (Creatures){
            if(!startSimulation) {
                startSimulation = true;
                moveIndex = 0;
                createGuardian(randomX);
                startUpdateTimer();
            }
        }
    }

    public void stopSimulation(){
        startSimulation = false;
        updateTimer.stop();
        phisycsTimer.stop();
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

    public void setLabelText(Creature aux){
        aux.label.setText("R$ " + (aux.money / 1000));
        this.add(aux.label);
        this.setComponentZOrder(aux.label, 0);
    }
}
