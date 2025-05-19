import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Screen {
    private static int width = 720;
    private static int height = 480;
    private JFrame frame;
    private BallPanel ballPanel;

    public void initScreen() {
        frame = new JFrame("Saltitantes");
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ballPanel = new BallPanel(width, height);
        ballPanel.setLayout(null);
        frame.add(ballPanel);

        addButton();

        frame.setVisible(true);
        ballPanel.startTimer();
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
        Random rand = new Random();
        int randomX = rand.nextInt(width - BallPanel.BALL_SIZE);
        ballPanel.addBall(randomX);
    }
}

class BallPanel extends JPanel {
    static final int BALL_SIZE = 50;
    private final int groundY;

    private final ArrayList<Ball> balls = new ArrayList<>();
    private Timer timer;

    private final int grav = 1;
    private final int jumpForce = -15;

    BallPanel(int width, int height) {
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(width, height));
        groundY = height - BALL_SIZE - 40;
    }

    public void addBall(int posX) {
        Random rand = new Random();
        int spdX = rand.nextBoolean() ? 3 : -3;
        balls.add(new Ball(posX, groundY, spdX));
    }

    public void startTimer() {
        timer = new Timer(16, e -> update());
        timer.start();
    }

    private void update() {
        for (Ball ball : balls) {
            ball.spdY += grav;
            ball.y += ball.spdY;
            ball.x += ball.spdX;

            if(ball.x < 0 || ball.x >= getWidth() - BALL_SIZE){
                ball.spdX *= -1;
            }

            if (ball.y >= groundY) {
                ball.y = groundY;
                ball.spdY = jumpForce;
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

    Ball(int x, int y, int spdX) {
        this.x = x;
        this.y = y;
        this.spdX = spdX;
    }
}
