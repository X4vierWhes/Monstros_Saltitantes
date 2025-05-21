import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Screen {
    private static int width = 720;
    private static int height = 480;
    private JFrame frame;
    private BallPanel ballPanel;
    private Random rand = new Random();

    public void initScreen() {
        frame = new JFrame("Saltitantes");
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ballPanel = new BallPanel(width, height);
        ballPanel.setLayout(null);
        frame.add(ballPanel);

        addButton();

        frame.setVisible(true);
        ballPanel.startPhisycsTimer();
        ballPanel.startUpdateTimer();
    }

    private void addButton() {
        JButton button = new JButton("ADD BALL");
        int buttonWidth = 100;
        int buttonHeight = 50;
        button.setBounds(width - buttonWidth - 20, 10, buttonWidth, buttonHeight);
        ballPanel.add(button);
        ballPanel.setComponentZOrder(button, 0);
        button.addActionListener(e -> addBallInRandomPos());
    }

    private void addBallInRandomPos() {
        int randomX = rand.nextInt(width - BallPanel.BALL_SIZE);
        ballPanel.addBall(randomX);
    }
}

class BallPanel extends JPanel {
    static final int BALL_SIZE = 50;
    private final int groundY;

    private final ArrayList<Ball> balls = new ArrayList<>();
    private Timer phisycsTimer;
    private Timer updateTimer;

    private final float grav = 1.0f;
    private final int jumpForce = -25;

    private Random rand = new Random();
    private int interacao = 0;
    private boolean canUpdate = true;

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
        this.add(label);
        this.setComponentZOrder(label, 0);
    }

    public void startUpdateTimer(){
        updateTimer = new Timer(2500, e -> update());
        updateTimer.start();
    }

    public void stopUpdateTimer(){
        if (updateTimer.isRunning()) {
            updateTimer.stop();
        }
    }

    public void startPhisycsTimer() {
        phisycsTimer = new Timer(10, e -> phisycsUpdate());
        phisycsTimer.start();
    }

    public void stopPhisycsTimer() {
        if (phisycsTimer.isRunning()) {
            phisycsTimer.stop();
        }
    }

    private void update(){
        interacao++;
        System.out.println(interacao);
        canUpdate = !canUpdate;

        balls.removeIf(ball -> ball.money < 0.0);

        for(Ball ball: balls){
            if (ball.canTheft) {
                thiefNeighbor(ball);
                ball.canMove = true;
            }
        }

        canUpdate = !canUpdate;

    }
    private boolean thiefNeighbor(Ball thief){
        if (balls.size() <= 1) return false;

        int closerIndex = 0;
        int index = 0;
        int closest_distance = getWidth() - BALL_SIZE;
        int aux_distance = 0;

        for(Ball neighbor: balls){
            if(thief != neighbor){
                if(thief.x >= neighbor.x){
                    aux_distance = thief.x - neighbor.x;
                }else{
                    aux_distance = neighbor.x - thief.x;
                }

                if(aux_distance <= closest_distance){
                    closest_distance = aux_distance;
                    closerIndex = index;
                }
            }
            index++;
        }

        thief.money += balls.get(closerIndex).money/2;
        balls.get(closerIndex).money /= 2;
        thief.target = calcNextPosition(thief);
        balls.get(closerIndex).target = calcNextPosition(balls.get(closerIndex));

        return true;
    }
    private int calcNextPosition(Ball ball){
        int minX = -1000000;
        int maxX =  1000000;
        int range = maxX - minX;

        int rawTarget = calcTarget(ball);
        double normalized = (double)(rawTarget - minX) / range;

        int screenTarget = (int)(normalized * (getWidth() - BallPanel.BALL_SIZE));

        screenTarget = Math.max(0, Math.min(screenTarget, getWidth() - BallPanel.BALL_SIZE));

        System.out.println("Target: " + screenTarget);
        return screenTarget;
    }

    private int calcTarget(Ball ball){
        return ball.x + (rand.nextInt(2) - 1) * ball.money;
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
                if (ball.canMove && canUpdate) {

                    if (ball.x == ball.target) { //Nao ta funcionando direito
                        ball.canMove = false;
                        ball.startTimer();

                    }

                    if(ball.x != ball.target){
                        ball.canTheft = false;
                    }

                    if(ball.target > ball.x) {
                        ball.x += ball.spdX;
                    }else if(ball.target < ball.x) {
                        ball.x -= ball.spdX;
                    }
                    //System.out.println(ball.target + " / " + ball.x);
                    //System.out.println(getWidth() + " " + getHeight());
                    if (ball.x < 0 || ball.x >= getWidth() - BALL_SIZE) { //Bola ta batendo pois getWidht ta dando um valor abaixo dos 720
                        ball.spdX *= -1;
                    }
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

class Ball {
    int x, y;
    int spdY = 0;
    int spdX = 0;
    int money = 1000000;
    boolean canMove = false;
    boolean canTheft = true;
    int target = 0;
    JLabel label;
    Timer cooldown;

    Ball(int x, int y, int spdX, int spdY, JLabel label) {
        this.x = x;
        this.y = y;
        this.spdX = spdX;
        this.spdY = spdY;
        this.label = label;
        cooldown = new Timer(6000, e ->
                thiefTimer()
        );
    }

    public void startTimer(){
        if (!cooldown.isRunning()) {
            canTheft = false;
            cooldown.setRepeats(false);
            cooldown.start();
        }
    }

    public void thiefTimer(){
        canTheft = true;
        if(cooldown.isRunning()){
            cooldown.stop();
        }
    }
}


