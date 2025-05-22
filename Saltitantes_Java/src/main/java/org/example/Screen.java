package main.java.org.example;

import javax.swing.*;
import java.awt.*;
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
        int buttonHeight = 25;
        button.setBounds(width - buttonWidth - 20, 10, buttonWidth, buttonHeight);
        button.setBackground(Color.BLUE);
        button.setForeground(Color.WHITE);
        ballPanel.add(button);
        ballPanel.setComponentZOrder(button, 0);
        button.addActionListener(e -> addBallInRandomPos());
    }

    private void addBallInRandomPos() {
        int randomX = rand.nextInt(width - BallPanel.BALL_SIZE);
        ballPanel.addBall(randomX);
    }
}
