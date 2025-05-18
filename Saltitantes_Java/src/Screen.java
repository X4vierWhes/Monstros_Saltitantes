import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Screen {
    private static int width = 720;
    private static int height = 480;

    private static JFrame frame;
    private static JPanel panel;

    private static ArrayList<PanelBall> list;
    public void initScreen() {
        list = new ArrayList<>();
        frame = new JFrame("Saltitantes");
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addPanel();
        addButton();

        frame.setVisible(true);
    }

    private void addPanel(){
        panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.BLACK);
        frame.add(panel);
    }
    private void addButton() {
        JButton button = new JButton("ADD BALL");
        int buttonWidth = 100;
        int buttonHeight = 50;
        button.setBounds(width - buttonWidth - 20, 10, buttonWidth, buttonHeight);
        panel.add(button);
        panel.setComponentZOrder(button, 0);
        button.addActionListener( e -> addBallInRandomPos());
    }

    private void addBallInRandomPos(){
        Random rand = new Random();
        int randomNumber = rand.nextInt(width);
        addBall(randomNumber);
    }
    private void addBall(int posX){
        PanelBall ball = new PanelBall(width, height, posX);
        ball.setBounds(0, 0, width, height);
        ball.setOpaque(false);
        ball.startTimer();
        panel.add(ball);
        list.add(ball);
    }

}

class PanelBall extends JPanel{
    private int x;
    private int y;
    private int spd = 0;
    private final int grav = 1;
    private final int jumpForce = -15;
    private final int groundY;
    private final int ballSize = 50;
    private Timer timer;
    PanelBall(int Widht, int Height, int posX) {

        this.groundY = Height - ballSize - 40;
        this.y = this.groundY;
        if(posX >= 0 && posX <= Widht - ballSize)
            this.x = posX;
    }

    public void startTimer(){
        timer = new Timer(16, e -> update());
        timer.start();
    }

    public void update(){
        this.spd += this.grav;
        this.y += this.spd;

        if (y >= groundY){
            y = groundY;
            spd = jumpForce;
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.BLUE);
        g.fillOval(x, y, ballSize, ballSize);
    }
}