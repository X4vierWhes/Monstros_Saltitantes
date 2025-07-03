package org.example.model;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.Vector;

/**
 * Representa um usuário do sistema de simulação.
 *
 * <p>Contém dados como nome de usuário, senha, avatar, estatísticas de simulações
 * (realizadas e bem-sucedidas), pontuação acumulada e status administrativo.
 * Também fornece funcionalidades para trocar o avatar e atualizar pontuações.</p>
 *
 * <p>Os avatares são carregados a partir do diretório de recursos:
 * <code>/org/example/images/</code> e redimensionados para 100x100 pixels.</p>
 *
 * @author ValentinaClash
 * @version 1.0
 */
public class User {

    /** Nome de usuário. */
    private String USERNAME;

    /** Senha do usuário. */
    private String PASSWORD;

    /** Imagem do avatar. */
    private ImageIcon AVATAR;

    /** Nome do arquivo do avatar (sem extensão). */
    private String AVATAR_NAME;

    /** Total de simulações realizadas pelo usuário. */
    private int SIMULATIONS;

    /** Total de simulações bem-sucedidas pelo usuário. */
    private int SUCCESS_SIMULATIONS;

    /** Flag que indica se o usuário é administrador. */
    private boolean isAdm = false;

    /** Índice do avatar atual (usado para troca sequencial). */
    private static int index = 0;

    /** Pontuação atual da simulação em execução. */
    private double points = 0;

    /** Pontuação total acumulada pelo usuário (não persistido). */
    private double totalPoints = 0;

    /**
     * Construtor padrão do usuário.
     *
     * @param username   Nome de usuário.
     * @param password   Senha do usuário.
     * @param avatarName Nome do avatar (sem extensão).
     */
    public User(String username, String password, String avatarName) {
        this.USERNAME = username;
        this.PASSWORD = password;
        this.setAvatar(avatarName);
        this.SIMULATIONS = 0;
        this.SUCCESS_SIMULATIONS = 0;
    }

    /**
     * Construtor completo para carregar usuários existentes do banco de dados.
     *
     * @param username            Nome de usuário.
     * @param password            Senha do usuário.
     * @param avatarName          Nome do avatar (sem extensão).
     * @param simulations         Número de simulações realizadas.
     * @param success_simulations Número de simulações bem-sucedidas.
     */
    public User(String username, String password, String avatarName, int simulations, int success_simulations, double totalPoints) {
        this.USERNAME = username;
        this.PASSWORD = password;
        this.setAvatar(avatarName);
        this.SIMULATIONS = simulations;
        this.SUCCESS_SIMULATIONS = success_simulations;
        this.totalPoints = totalPoints;
    }

    /** Incrementa o número de simulações realizadas. */
    public void addSimulations() {
        SIMULATIONS++;
    }

    /** Incrementa o número de simulações bem-sucedidas. */
    public void addSuccesSimulations() {
        SUCCESS_SIMULATIONS++;
    }

    /** @return Nome de usuário. */
    public String getUserName() {
        return this.USERNAME;
    }

    /** @return Senha do usuário. */
    public String getPassWord() {
        return this.PASSWORD;
    }

    /** @return Quantidade de simulações realizadas. */
    public int getSIMULATIONS() {
        return SIMULATIONS;
    }

    /** @return Quantidade de simulações bem-sucedidas. */
    public int getSUCCESS_SIMULATIONS() {
        return SUCCESS_SIMULATIONS;
    }

    /** @return Imagem do avatar atual do usuário. */
    public ImageIcon getAVATAR() {
        return this.AVATAR;
    }

    /**
     * Calcula a taxa de sucesso nas simulações.
     *
     * @return Taxa de sucesso como valor entre 0.0 e 1.0.
     */
    public float getSuccesRate() {
        if (SUCCESS_SIMULATIONS == 0 || SIMULATIONS == 0) {
            return 0.0f;
        }
        return (float) this.SUCCESS_SIMULATIONS / this.SIMULATIONS;
    }

    /** @return {@code true} se o usuário for administrador. */
    public boolean isAdm() {
        return isAdm;
    }

    /**
     * Define se o usuário é administrador.
     *
     * @param adm {@code true} para tornar administrador.
     */
    public void setAdm(boolean adm) {
        isAdm = adm;
    }

    /** @return Nome do avatar associado ao usuário. */
    public String getAvatarname() {
        return AVATAR_NAME;
    }

    /**
     * Define o avatar do usuário com base no nome do arquivo.
     *
     * @param avatarname Nome do arquivo (sem extensão .jpeg).
     */
    public void setAvatar(String avatarname) {
        this.AVATAR_NAME = avatarname;
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(
                getClass().getResource("/org/example/images/" + avatarname + ".jpeg")));
        Image scaled = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        this.AVATAR = new ImageIcon(scaled);
    }

    /**
     * Alterna para o próximo avatar na lista fornecida.
     *
     * @param imgs Vetor com os nomes de avatar disponíveis.
     */
    public void changeAvatar(Vector<String> imgs) {
        this.setAvatar(imgs.get(index));
        index = (index + 1) % imgs.size();
    }

    /**
     * Adiciona pontos à pontuação atual da simulação.
     *
     * @param points Valor a ser somado.
     */
    public void addPoints(double points) {
        this.points += points;
    }

    /** @return Pontuação atual da simulação. */
    public double getPoints() {
        return points;
    }

    /**
     * Define a pontuação atual da simulação.
     *
     * @param points Novo valor da pontuação.
     */
    public void setPoints(double points) {
        this.totalPoints += this.points;
        this.points = points;
    }

    public double getTotalPoints(){return this.totalPoints;}

    public void setTotalPoints(double t){
        this.totalPoints = t;
    }
}
