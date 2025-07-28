package org.example.view;

import org.example.model.SQLite;
import org.example.model.User;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Janela gráfica responsável por exibir os resultados do usuário após uma simulação.
 *
 * <p>A `ResultView` apresenta o avatar do usuário, informações estatísticas como número
 * de simulações, vitórias e taxa de sucesso, além de um ranking de todos os usuários do sistema.</p>
 *
 * <p>A interface é personalizada com cores e layout manual, utilizando {@link JFrame}
 * como container principal.</p>
 *
 * @author ValentinaClash
 * @version 1.0
 * @see User
 * @see SQLite
 */
public class ResultView extends JFrame {

    /** Largura padrão da janela. */
    private static final int WIDTH = 720;

    /** Altura padrão da janela. */
    private static final int HEIGHT = 480;

    /** Cor de destaque usada na interface. */
    private static final Color color = new Color(255, 0, 129);

    /** Usuário atualmente logado. */
    private User user;

    /** Avatar do usuário em forma de imagem. */
    private ImageIcon imageIcon;

    /** Painel onde o avatar será exibido. */
    private JPanel avatarPanel;

    /** Botão utilizado para sair da tela de resultados. */
    private JButton quitButton;

    /** Conexão com o banco de dados SQLite. */
    private SQLite bd;

    /**
     * Construtor da tela de resultados.
     *
     * @param user Usuário autenticado.
     * @param bd   Instância de conexão com banco de dados SQLite.
     */
    public ResultView(User user, SQLite bd) {
        this.user = user;
        this.bd = bd;

        this.setTitle("Tela de Resultados");
        this.setSize(WIDTH, HEIGHT);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.getContentPane().setBackground(Color.BLACK);

        initComponents();

        setVisible(true);
    }

    /**
     * Inicializa e posiciona os componentes gráficos da tela de resultados.
     * Exibe avatar, informações do usuário e ranking geral de usuários.
     */
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

        JLabel successrateLabel = new JLabel("Rating: " + user.getSuccessRate());
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
                                " | Rating: " + u.getSuccessRate() +
                                " | Pontos: " + u.getTotalPoints()
                );
                label.setForeground(Color.WHITE);
                label.setAlignmentX(Component.CENTER_ALIGNMENT);
                listPanel.add(label);
                listPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }

            JScrollPane scrollPane = new JScrollPane(listPanel);
            scrollPane.setBounds(WIDTH / 3, HEIGHT / 5, WIDTH / 2, 200);
            scrollPane.setOpaque(false);
            scrollPane.getViewport().setOpaque(false);
            scrollPane.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(color),
                    "Ranking de Usuários", 0, 0, null, color));
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);

            this.add(scrollPane);
        }
    }

    /**
     * Retorna o botão de sair (voltar).
     *
     * @return botão quitButton.
     */
    public JButton getQuitButton() {
        return quitButton;
    }
}
