package org.example.view;

import org.example.model.User;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

/**
 * Janela gráfica que exibe informações do usuário e permite interações básicas,
 * como iniciar simulação, ver resultados, deletar usuário, sair e trocar o avatar.
 *
 * <p>Mostra o avatar atual do usuário, seu nome, e possui botões organizados em coluna
 * para as ações mencionadas. Também permite trocar o avatar clicando em um botão
 * especial que percorre uma lista predefinida de imagens.</p>
 *
 * @author ValentinaClash
 * @version 1.0
 * @see User
 */
public class UserView extends JFrame {

    /** Largura fixa da janela. */
    private static final int WIDTH = 720;

    /** Altura fixa da janela. */
    private static final int HEIGHT = 480;

    /** Largura padrão dos botões. */
    private static final int btnWIDTH = 150;

    /** Altura padrão dos botões. */
    private static final int btnHEIGHT = 70;

    /** Usuário atualmente exibido na tela. */
    private User user;

    /** Lista de nomes das imagens de avatar disponíveis para troca. */
    private final Vector<String> imgs = new Vector<>(java.util.Arrays.asList(
            "common", "diamond", "dog", "dogs", "lego", "snopanime",
            "cat", "cat2", "cience", "humor", "jr", "mine", "neymar", "pray", "west"));

    /** Painel que contém o avatar do usuário. */
    private JPanel avatarPanel;

    /** Imagem do avatar atual. */
    private ImageIcon imageIcon;

    /** Botão para iniciar a simulação. */
    private JButton initButton;

    /** Botão para exibir resultados. */
    private JButton resultButton;

    /** Botão para deletar o usuário. */
    private JButton deleteButton;

    /** Botão para sair da tela de usuário. */
    private JButton quitButton;

    /** Botão para trocar o avatar. */
    private JButton changeAvatarButton;

    /** Cor padrão usada para textos e botões. */
    private final Color color = new Color(255, 0, 129);

    /**
     * Constrói a janela da tela de usuário, configurando layout, componentes
     * e exibindo o avatar e nome do usuário fornecido.
     *
     * @param user Usuário cujas informações serão exibidas.
     */
    public UserView(User user){
        this.user = user;
        this.setTitle("Tela de Usuario");
        this.setSize(WIDTH, HEIGHT);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.getContentPane().setBackground(new Color(0, 0, 0));

        initComponents();
        setVisible(true);
    }

    /**
     * Inicializa e posiciona os componentes gráficos da janela, incluindo avatar,
     * botões e labels.
     */
    private void initComponents() {
        imageIcon = user.getAVATAR();
        JLabel avatarLabel = new JLabel(imageIcon);
        avatarLabel.setBorder(BorderFactory.createLineBorder(color));

        avatarPanel = new JPanel(new BorderLayout());
        avatarPanel.setBounds(WIDTH / 25, HEIGHT / 25, 100, 100);
        avatarPanel.setOpaque(false);
        avatarPanel.add(avatarLabel, BorderLayout.CENTER);
        this.add(avatarPanel);

        changeAvatarButton = new JButton("*");
        changeAvatarButton.setForeground(Color.WHITE);
        changeAvatarButton.setBounds(WIDTH / 25, HEIGHT / 25, 25, 25);
        changeAvatarButton.setBackground(color);
        this.add(changeAvatarButton);

        JLabel userLabel = new JLabel("User: " + user.getUserName());
        userLabel.setBounds(WIDTH / 25, HEIGHT / 25 + 110, 150, 25);
        userLabel.setForeground(color);
        this.add(userLabel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);
        buttonPanel.setBounds(WIDTH / 2, HEIGHT / 25, btnWIDTH, 3 * (btnHEIGHT + 10));

        initButton = new JButton("Iniciar");
        resultButton = new JButton("Resultados");
        deleteButton = new JButton("Deletar");
        quitButton = new JButton("Sair");

        JButton[] buttons = {initButton, resultButton, deleteButton, quitButton};

        for (JButton b : buttons) {
            b.setMaximumSize(new Dimension(btnWIDTH, btnHEIGHT));
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            b.setBackground(color);
            b.setForeground(Color.WHITE);
            b.setFocusable(false);
            buttonPanel.add(b);
            buttonPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Espaçamento vertical
        }

        this.add(buttonPanel);
    }

    /**
     * Atualiza o avatar do usuário exibido na tela, percorrendo a lista de avatares disponíveis.
     * Atualiza o painel gráfico com o novo avatar.
     *
     * @param user Usuário cujo avatar será atualizado.
     * @return {@code true} se o avatar foi alterado com sucesso; {@code false} se o usuário for nulo.
     */
    public boolean changeAvatar(User user){
        if(user == null){
            return false;
        }
        this.user = user;
        this.remove(avatarPanel);
        this.user.changeAvatar(imgs);

        imageIcon = this.user.getAVATAR();
        JLabel avatarLabel = new JLabel(imageIcon);
        avatarLabel.setBorder(BorderFactory.createLineBorder(color));

        avatarPanel = new JPanel(new BorderLayout());
        avatarPanel.setBounds(WIDTH / 25, HEIGHT / 25, 100, 100);
        avatarPanel.setOpaque(false);
        avatarPanel.add(avatarLabel, BorderLayout.CENTER);
        this.add(avatarPanel);
        this.revalidate();
        this.repaint();
        return true;
    }

    /** @return Botão para trocar o avatar do usuário. */
    public JButton getChangeAvatarButton(){ return changeAvatarButton; }

    /** @return Botão para iniciar a simulação. */
    public JButton getInitButton() {
        return initButton;
    }

    /** @return Botão para exibir os resultados. */
    public JButton getResultButton() {
        return resultButton;
    }

    /** @return Botão para deletar o usuário. */
    public JButton getDeleteButton() {
        return deleteButton;
    }

    /** @return Botão para sair da tela de usuário. */
    public JButton getQuitButton() {
        return quitButton;
    }
}
