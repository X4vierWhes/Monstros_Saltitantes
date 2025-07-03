package org.example.view;

import org.example.model.SQLite;
import org.example.model.User;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

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
    private SQLite bd;

    public ResultView(User user, SQLite bd){
        this.user = user;
        this.bd = bd;
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

        JLabel successrateLabel = new JLabel("Rating: " + user.getSuccesRate());
        successrateLabel.setBounds(labelX, startY + spacing * 3, 300, 25);
        successrateLabel.setForeground(color);
        this.add(successrateLabel);

        ArrayList<User> allUsers = bd.getAllUsers();

        if (allUsers != null && !allUsers.isEmpty()) {
            JPanel listPanel = new JPanel();
            listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
            listPanel.setOpaque(false);

            for (User u : allUsers) {
                JLabel label = new JLabel(
                        u.getUserName() +
                                " | Sims: " + u.getSIMULATIONS() +
                                " | Sucesso: " + u.getSUCCESS_SIMULATIONS() +
                                " | Rating: " + u.getSuccesRate()
                );
                label.setForeground(Color.WHITE);
                label.setAlignmentX(Component.CENTER_ALIGNMENT);
                listPanel.add(label);
                listPanel.add(Box.createRigidArea(new Dimension(0, 5))); // Espaçamento entre labels
            }

            JScrollPane scrollPane = new JScrollPane(listPanel);
            scrollPane.setBounds(WIDTH/3, HEIGHT / 5, WIDTH / 2, 200);
            scrollPane.setOpaque(false);
            scrollPane.getViewport().setOpaque(false);
            scrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(color),
                    "Ranking de Usuários", 0, 0, null, color));
            scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Scroll mais suave

            this.add(scrollPane);
        }
    }


    public JButton getQuitButton() {
        return quitButton;
    }
}
