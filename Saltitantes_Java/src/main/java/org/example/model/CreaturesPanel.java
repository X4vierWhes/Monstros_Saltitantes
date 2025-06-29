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

    /** Gerador de números aleatórios para movimentação e cálculo de alvo. */
    private Random rand = new Random();

    /** Contador de interações (ticks de atualização). */
    private int interacao = 0;

    /** Flag que alterna o momento de atualizar ou não. */
    private boolean canUpdate = true;

    private boolean startSimulation = false;

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
        synchronized (Creatures) {
            int spdX = 1;
            int spdY = 1;

            JLabel label = new JLabel();
            label.setForeground(Color.WHITE);
            label.setBounds(posX, groundY - 20, CREATURE_SIZE, 20);
            label.setHorizontalAlignment(SwingConstants.CENTER);

            Creature newCreature = new Creature(posX, groundY, spdX, spdY, label);

            if (Creatures.isEmpty()) {
                Creatures.add(newCreature);
                label.setText("R$ " + (newCreature.money / 100.0));
                newCreature.x = calcNextPosition(newCreature);
                newCreature.x = Math.max(0, Math.min(newCreature.target + rand.nextInt(40) - 20, getWidth() - CREATURE_SIZE));
                this.add(label);
                this.setComponentZOrder(label, 0);
            } else {
                Creature last = Creatures.getLast();
                if (!last.isGuardian) {
                    Creatures.add(newCreature);
                    label.setText("R$ " + (newCreature.money / 100.0));
                    newCreature.x = calcNextPosition(newCreature);
                    newCreature.target = newCreature.x;
                    this.add(label);
                    this.setComponentZOrder(label, 0);
                } else {
                    Creatures.removeLast(); // remove o guardião temporariamente
                    Creatures.add(newCreature);
                    label.setText("R$ " + (newCreature.money / 100.0));
                    newCreature.x = calcNextPosition(newCreature);
                    newCreature.target = newCreature.x;
                    this.add(label);
                    this.setComponentZOrder(label, 0);
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
                cluster.target = cluster.x;


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
                System.err.println("Criou novo cluster");
                Creatures.add(guardian);

                guardian.label.setText("R$ " + (guardian.money / 100));
                this.add(guardian.label);
                label.setText("R$ " + (cluster.money / 100.0));
                this.add(label);
                this.setComponentZOrder(label, 0);
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

            Creatures.add(new Guardian(posX, groundY, spdX, spdY, label));
            label.setText("R$ " + (Creatures.getLast().money / 100.0));
            //Creatures.getLast().x = calcNextPosition(Creatures.getLast());
            Creatures.getLast().target = Creatures.getLast().x;
            this.add(label);
            this.setComponentZOrder(label, 0);
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
        if(Creatures.size() <= 2 && startSimulation){
            stopSimulation();
        }
        synchronized (Creatures) {

            interacao++;
            canUpdate = !canUpdate;

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
            List<Creature> snapshot; //Copia da lista de criaturas para evitar erros
            snapshot = new ArrayList<>(Creatures);


            if (canUpdate) {

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
                        System.out.println("Size: " + snapshot.size() + " / Index: " + moveIndex);
                        moveIndex = (moveIndex + 1) % snapshot.size();
                        //System.out.println("Index: " + moveIndex);
                        //System.out.println("X: " + getLast().x + " / Target: " + getLast().target);
                    }

                    creature.label.setText("R$ " + (creature.money / 100.0));
                    creature.label.setBounds(creature.x, creature.y - 20, CREATURE_SIZE, 20);
                }


                if (moveIndex == 0 && startSimulation) {
                    System.err.println("ENTREI");
                    checkCluster(); //Checa se precisa criar clusters
                    checkGuardian();

                    for (Creature aux : snapshot) {
                        aux.canTheft = true; //Autoriza criaturas a roubar novamente
                    }
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
                    guardian.label.setText("R$: " + (guardian.money / 100));
                    this.add(guardian.label);

                    System.err.println("Guardião 'matou' cluster");
                    guardian.target = calcNextPosition(guardian);
                    guardian.canMove = true;
                }
            }else{
                System.err.println("Não possui guardião");
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

        System.out.println(Creatures.size());
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
        if(!startSimulation) {
            startSimulation = true;
            moveIndex = 0;
            createGuardian(randomX);
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
}
