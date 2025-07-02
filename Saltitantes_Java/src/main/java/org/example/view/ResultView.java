package org.example.view;

import org.example.model.User;

import javax.swing.*;
import java.awt.*;

public class ResultView extends JFrame {
    /** Largura da janela. */
    private static final int WIDTH = 720;

    /** Altura da janela. */
    private static final int HEIGHT = 480;

    private static final Color color = new Color(255,0, 129);
    private User user;
    private ImageIcon imageIcon;
    private JPanel avatarPanel;
    private JButton quitButton;

    public ResultView(User user){
        this.user = user;
        this.setTitle("Tela de Resultados");
        this.setSize(WIDTH, HEIGHT);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.getContentPane().setBackground(new Color(0,0,0)); //cor background da tela

        initComponents();

        setVisible(true);
    }

    private void initComponents() {
        imageIcon = user.getAVATAR();
        JLabel avatarLabel = new JLabel(imageIcon);
        avatarLabel.setBorder(BorderFactory.createLineBorder(color));

        avatarPanel = new JPanel();
        avatarPanel.setLayout(new BorderLayout());
        avatarPanel.setBounds(WIDTH / 25, HEIGHT / 25, 100, 100);
        avatarPanel.setOpaque(false);
        avatarPanel.add(avatarLabel, BorderLayout.CENTER);
        this.add(avatarPanel);

        quitButton = new JButton("Voltar");
        quitButton.setBackground(color);
        quitButton.setForeground(Color.WHITE);
        quitButton.setBounds(WIDTH - 100, HEIGHT / 25, 75, 50);
        this.add(quitButton);

        int labelX = WIDTH / 25;
        int startY = HEIGHT / 25 + 110;
        int spacing = 30;

        JLabel userLabel = new JLabel("User: " + user.getUserName());
        userLabel.setBounds(labelX, startY, 300, 25);
        userLabel.setForeground(color);
        this.add(userLabel);

        JLabel simulationLabel = new JLabel("Simulações: " + user.getSIMULATIONS());
        simulationLabel.setBounds(labelX, startY + spacing, 300, 25);
        simulationLabel.setForeground(color);
        this.add(simulationLabel);

        JLabel successLabel = new JLabel("Simulações com vitória: " + user.getSUCCESS_SIMULATIONS());
        successLabel.setBounds(labelX, startY + spacing * 2, 300, 25);
        successLabel.setForeground(color);
        this.add(successLabel);

        JLabel successrateLabel = new JLabel("Rating de vitórias/sucesso: " + user.getSuccesRate());
        successrateLabel.setBounds(labelX, startY + spacing * 3, 300, 25);
        successrateLabel.setForeground(color);
        this.add(successrateLabel);
    }


    public JButton getQuitButton() {
        return quitButton;
    }
}
