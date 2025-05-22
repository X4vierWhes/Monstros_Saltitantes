package main.java.org.example;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class BallPanel extends JPanel {
    static final int BALL_SIZE = 50;
    private final int groundY;

    private final ArrayList<Ball> balls = new ArrayList<>();
    private Timer phisycsTimer;
    private Timer updateTimer;

    private final float grav = 1.0f;
    private final int jumpForce = -15;

    private Random rand = new Random();
    private int interacao = 0;
    private boolean canUpdate = true;
    private static int moveIndex = 0;
    BallPanel(int width, int height) {
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(width, height));
        groundY = height - BALL_SIZE - 40;
    }

    public void addBall(int posX) {
        int spdX = 1; //rand.nextInt(6) - 3;
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

    public void startUpdateTimer(){
        updateTimer = new Timer(2500, e -> update());
        updateTimer.start();
    }

    public void startPhisycsTimer() {
        phisycsTimer = new Timer(1, e -> phisycsUpdate());
        phisycsTimer.start();
    }

    private boolean isCanUpdate(){
        for(Ball ball: balls){
            if(ball.canMove ){
                return false;
            }
        }
        return true;
    }

    private void update(){
        interacao++;
        //System.out.println(interacao);
        canUpdate = !canUpdate;

        balls.removeIf(ball -> ball.money <= 0.0);

        for(Ball ball: balls){
            if (ball.canTheft) {
                thiefNeighbor(ball);
                ball.canMove = true;
            }
        }

        canUpdate = !canUpdate;

    }
    private void thiefNeighbor(Ball thief){
        if (balls.size() <= 1) return;

        if(isCanUpdate()) {
            int closerIndex = 0;
            int index = 0;
            int closest_distance = getWidth() - BALL_SIZE;
            int aux_distance = 0;

            for (Ball neighbor : balls) {
                if (thief != neighbor) {
                    if (thief.x >= neighbor.x) {
                        aux_distance = thief.x - neighbor.x;
                    } else {
                        aux_distance = neighbor.x - thief.x;
                    }

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
            System.out.println(
                    "roubou"
            );
        }
    }
    private int calcNextPosition(Ball ball){
        int minX = -1000000;
        int maxX =  1000000;
        int range = maxX - minX;

        double rawTarget = calcTarget(ball);
        double normalized = (rawTarget - minX) / range;

        int screenTarget = (int)(normalized * (getWidth() - BALL_SIZE));

        screenTarget = Math.max(0, Math.min(screenTarget, getWidth() - BALL_SIZE));

        //System.out.println("Target: " + screenTarget);
        return screenTarget;
    }

    private double calcTarget(Ball ball){
        return ball.x + (rand.nextDouble(2) - 1) * ball.money;
    }

    private void phisycsUpdate() {
        if(canUpdate) {
            for (Ball ball : balls) {
                //Atualizar Y para sempre pular
                ball.spdY += grav;
                ball.y += ball.spdY;

                if (ball.y >= groundY) {
                    ball.y = groundY;
                    ball.spdY = jumpForce;
                }
                //Atualizar o x quando a bola pode se mover
                if (balls.get(moveIndex).canMove) {

                    if (balls.get(moveIndex).x == balls.get(moveIndex).target) {
                        balls.get(moveIndex).canMove = false;
                        balls.get(moveIndex).startTimer();
                    }

                    if(balls.get(moveIndex).x != balls.get(moveIndex).target){
                        balls.get(moveIndex).canTheft = false;
                    }

                    if(balls.get(moveIndex).target > balls.get(moveIndex).x) {
                        balls.get(moveIndex).x += balls.get(moveIndex).spdX;
                    }else if(balls.get(moveIndex).target < balls.get(moveIndex).x) {
                        balls.get(moveIndex).x -= balls.get(moveIndex).spdX;
                    }
                    System.out.println( balls.get(moveIndex).target + " / " + balls.get(moveIndex).x);
                }else{
                    moveIndex = (moveIndex + 1) % balls.size();
                    //System.out.println("Index move: " + moveIndex);
                }
                ball.label.setText("R$ " + (ball.money / 100.0));
                ball.label.setBounds(ball.x, ball.y - 20, BALL_SIZE, 20);
            }
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLUE);
        for (Ball ball : balls) {
            g.fillOval(ball.x, ball.y, BALL_SIZE, BALL_SIZE);
        }
    }
}