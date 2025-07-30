package org.example.model;

import org.example.controller.UserController;

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

    public static final int fuseDistance = 75; // tolerância em pixels

    /** Posição Y que representa o chão. */
    private final int groundY;

    /** Lista de todas as bolas presentes no painel. */
    public final List<Creature> Creatures = new ArrayList<>();

    /** Timer que controla a física das bolas (gravidade, movimento). */
    public Timer phisycsTimer;

    /** Timer que dispara atualizações periódicas nas bolas (roubo e movimentação). */
    public Timer updateTimer;

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
    public boolean canUpdate = true;

    public boolean startSimulation = false;

    /** Índice atual da bola que está se movendo. */
    public static int moveIndex = 0;
    public User user;
    public SQLite bd;
    private int maxInteration = 50;

    /**
     * Construtor do painel de bolas.
     *
     * @param width  Largura do painel.
     * @param height Altura do painel.
     */
    public CreaturesPanel(int width, int height, User user, SQLite bd) {
        this.bd = bd;
        this.user = user;
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(width, height));
        groundY = height - CREATURE_SIZE - 40;
    }

    public CreaturesPanel(int width, int height, User user) {
        this.user = user;
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
                    Creatures.removeLast();
                    Creatures.add(newCreature);
                    newCreature.x = calcNextPosition(newCreature);
                    newCreature.target = calcNextPosition(newCreature);
                    Creatures.add(last);
                }
            }
        }
    }

    public boolean createCluster(ArrayList<Creature> creaturesColliding){
        if(creaturesColliding.size() > 1){
            synchronized (Creatures) {
                if(!getLast().isGuardian) return false;
                Creature guardian = getLast();
                removeCreature(guardian); //Remove temporiarmente o guardião da lista

                JLabel label = new JLabel();
                label.setForeground(Color.WHITE);
                label.setBounds(creaturesColliding.get(0).x, groundY - 20, CREATURE_SIZE, 20);
                label.setHorizontalAlignment(SwingConstants.CENTER);

                Creature cluster = new Creature(creaturesColliding.getFirst().x, groundY - 20, 1, 0, label);
                cluster.gold = 0.0;
                cluster.isCluster = true;

                for (Creature aux : creaturesColliding) {
                    cluster.gold += aux.gold;
                    this.remove(aux.label);
                    user.addPoints(20.0);
                }

                // Remove todas as criaturas que participaram da fusão
                Creatures.removeAll(creaturesColliding);

                // Adiciona o novo cluster
                Creatures.add(cluster);
                cluster.target = calcNextPosition(cluster);
                cluster.canMove = true;
                setLabelText(cluster);
                Creatures.add(guardian);
                return true;
            }
        }
        return false;
    }

    public boolean createGuardian(int posX){
        synchronized (Creatures){
            if(this.getLast().isGuardian){
                System.err.println("Só pode ter um guardião");
                return false;
            }
            int spdX = 1;
            int spdY = 1;

            JLabel label = new JLabel();
            label.setForeground(Color.WHITE);
            label.setBounds(posX, groundY - 20, CREATURE_SIZE, 20);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            Guardian guardian = new Guardian(posX, groundY, spdX, spdY, label);
            setLabelText(guardian);
            Creatures.add(guardian);
            guardian.x = calcNextPosition(Creatures.getLast());
            guardian.target = Creatures.getLast().x;
            return true;
        }
    }

    /**
     * Verifica se todas as bolas estão paradas (não podem se mover).
     *
     * @return true se todas as bolas estão paradas, senão false.
     */
    public boolean isCanUpdate() {
        synchronized (Creatures) {
            for (Creature aux : Creatures) {
                if (aux.canMove && !aux.isGuardian) {
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

            if (isCanUpdate() && !thief.isGuardian) {
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
                            user.addPoints(5.0);
                        }
                    }
                    index++;
                }

                thief.gold += Creatures.get(closerIndex).gold / 2;
                Creatures.get(closerIndex).gold /= 2;

                thief.target = calcNextPosition(thief);
                Creatures.get(closerIndex).target = calcNextPosition(Creatures.get(closerIndex));
            }else{
                return false;
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
        return Creature.x + (rand.nextDouble(2) - 1) * Creature.gold;
    }

    /**
     * Atualiza o estado lógico do jogo, removendo bolas sem dinheiro e ativando roubos.
     *
     * @return true se a atualização foi bem-sucedida, false se não há bolas left.
     */
    public boolean update() {
        synchronized (Creatures) {
            interacao++;
            //System.err.println(interacao);
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

            List<Creature> snapshot; //Copia da lista de criaturas para evitar erros
            snapshot = new ArrayList<>(Creatures);

            if (canUpdate) {
                if(creaturesMove >= snapshot.size()){
                    System.err.println(checkGuardian());
                    System.err.println(checkCluster());
                    creaturesMove = 0;
                    moveIndex = 0;
                    snapshot = new ArrayList<>(Creatures);

                    for (Creature aux : snapshot) {
                        if(!aux.isGuardian) { //Guardiao deve continuar se movendo
                            aux.canTheft = true;
                            aux.canMove = false;
                        }
                    }
                }

                for (Creature creature : snapshot) {
                    // Atualização vertical (PULO)

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
            checkEndCondition();
            return true;
        }
    }

    public boolean checkGuardian() {
        synchronized (Creatures) {
            Creature guardian = getLast();

            if(guardian.isGuardian){
                List<Creature> toRemove = new ArrayList<>();

                for(Creature c: Creatures){
                    if(c.isCluster && Math.abs(c.x - guardian.x) <= fuseDistance){
                        guardian.gold += c.gold;
                        toRemove.add(c);
                    }
                }
                if(!toRemove.isEmpty()) {
                    guardian.x = toRemove.getLast().x;
                    for (Creature c : toRemove) {
                        removeCreature(c);
                        user.addPoints(50.0);
                    }
                    this.remove(guardian.label);
                    setLabelText(guardian);
                    guardian.target = calcNextPosition(guardian);
                    guardian.canMove = true;
                    guardian.canTheft = false;
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    /**
     * Checa se precisa criar novos clusters (duas ou mais criaturas na mesma posição
     */
    public boolean checkCluster() {
        synchronized (Creatures) {
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
                }else{
                    return false;
                }
            }

            for (List<Creature> grupo : group) {
                createCluster(new ArrayList<>(grupo));
            }
        }
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
            if(creature.isCluster) {
                g.setColor(new Color(157,0,255));
                g.fillRect(creature.x, creature.y, CREATURE_SIZE, CREATURE_SIZE);
            }else if(creature.isGuardian){
                g.setColor(new Color(0, 255, 0));
                g.fillRect(creature.x, creature.y, CREATURE_SIZE, CREATURE_SIZE);
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
            if (Creatures.size() <= 1 || remove.isGuardian) {
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

    public boolean initSimulation(int randomX) {
        synchronized (Creatures){
            if(!startSimulation) {
                startSimulation = true;
                moveIndex = 0;
                user.addSimulations();
                bd.editUserByUsername(user.getUserName(), user);
                createGuardian(randomX);
                startUpdateTimer();

                if (phisycsTimer == null){
                    startPhisycsTimer();
                }

                return true;
            }
            return false;
        }
    }

    public boolean stopSimulation(){
        if(!startSimulation){
            System.err.println("Simulação ainda nao começou");
            return false;
        } //Se simulação nao começou, nao tem como parar

        startSimulation = false; //Para simulação que começou
        //Para os timers
        if((updateTimer != null && phisycsTimer != null) && (updateTimer.isRunning() && phisycsTimer.isRunning())) {
            updateTimer.stop();
            phisycsTimer.stop();
        }

        //System.err.println(user.getSIMULATIONS() + " / " + user.getPoints() + " / " + user.getSUCCESS_SIMULATIONS());
        boolean ret;
        String msg;
        if(user.getPoints() >= 500){
            user.addSuccesSimulations();
            msg = "Objetivo: 500 pontos | Resultado: " + user.getPoints() + " | == Vitoria";
            ret = true;
        }else{
            msg = "Objetivo: 500 pontos | Resultado: " + user.getPoints() + " | == Derrota";
            ret = false;
        }
        System.err.println(user.getSIMULATIONS() + " / " + user.getPoints() + " / " + user.getSUCCESS_SIMULATIONS());
        user.setPoints(0.0);
        JOptionPane.showMessageDialog(this, msg);
        bd.editUserByUsername(user.getUserName(), user);
        return ret;
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
        aux.label.setText("OURO: " + (aux.gold / 1000));
        this.add(aux.label);
        this.setComponentZOrder(aux.label, 0);
    }


    /**
     * Inicia o timer de atualização lógica (roubos e movimentação).
     */
    public void startUpdateTimer() {
        updateTimer = new Timer(3000, e -> update());
        updateTimer.start();
    }

    /**
     * Inicia o timer de atualização física (gravidade e pulo).
     */
    public void startPhisycsTimer() {
        phisycsTimer = new Timer(10, e -> phisycsUpdate());
        phisycsTimer.start();
    }

    public boolean checkEndCondition() {
        synchronized (Creatures) {
            if (!startSimulation) return false;

            int normalCount = 0;
            Creature normalCreature = null;
            Creature guardian = null;

            for (Creature c : Creatures) {
                if (c.isGuardian) {
                    guardian = c;
                } else {
                    normalCount++;
                    normalCreature = c;
                }
            }

            if ((normalCount == 1 && guardian != null  /*&& guardian.gold > normalCreature.gold*/) || interacao >= maxInteration) {
                JOptionPane.showMessageDialog(this, "FIM DA SIMULAÇÃO!");
                return stopSimulation();
            }
        }
        return false;
    }


}
