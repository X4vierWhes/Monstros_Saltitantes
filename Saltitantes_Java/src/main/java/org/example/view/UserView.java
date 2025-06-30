package org.example.view;

import org.example.controller.LoginController;
import org.example.model.User;

import javax.swing.*;
import java.awt.*;

public class UserView extends JFrame {
    /** Largura da janela. */
    private static final int WIDTH = 720;
    /** Altura da janela. */
    private static final int HEIGHT = 480;

    private static final int btnWIDTH = 150;
    private static final int btnHEIGHT = 70;
    private User user;
    private JPanel avatarPanel;
    private ImageIcon imageIcon;
    private JButton initButton;
    private JButton resultButton;
    private JButton deleteButton;
    private JButton quitButton;
    private final Color color = new Color(255,0,129);

    public UserView(User user){
        this.user = user;
        this.setTitle("Tela de Usuario");
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
        avatarPanel.setBounds(WIDTH/25, HEIGHT/25, 100, 100);
        avatarPanel.setOpaque(false);
        avatarPanel.add(avatarLabel, BorderLayout.CENTER);
        this.add(avatarPanel);

        JLabel userLabel = new JLabel("User: " + user.getUserName());
        userLabel.setBounds(WIDTH/25, HEIGHT/25 + 100, 100, 25 );
        userLabel.setForeground(color);
        this.add(userLabel);

        initButton = new JButton("Iniciar");
        resultButton = new JButton("Resultados");
        quitButton = new JButton("Sair");

        JButton[] buttons = {initButton, resultButton, quitButton};

        int heightIn = 75;
        int fuse = 0;

        for(JButton b: buttons){
            b.setBackground(color);
            b.setForeground(Color.WHITE);
            b.setBounds(WIDTH/2, HEIGHT/heightIn + fuse, btnWIDTH, btnHEIGHT);
            heightIn -= 25;
            fuse = btnHEIGHT + 10;
            this.add(b);
        }
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            UserView view = new UserView(new User("Whesley", "1234", "dog"));
        });
    }

    public JButton getInitButton() {
        return initButton;
    }

    public JButton getResultButton() {
        return resultButton;
    }

    public JButton getDeleteButton() {
        return deleteButton;
    }

    public JButton getQuitButton() {
        return quitButton;
    }
}
