package org.example.model;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class User {
    private String USERNAME;
    private String PASSWORD;
    private ImageIcon AVATAR;
    private int SIMULATIONS;
    private int SUCCESS_SIMULATIONS;

    public User(String username, String password, String avatarName){
        this.USERNAME = username;
        this.PASSWORD = password;
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource(
                "/org/example/images/" + avatarName + ".jpeg")));
        Image scaled = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        this.AVATAR = new ImageIcon(scaled);

    }

    public User(String username, String password, String avatarName, int simulations, int success_simulations){
        this.USERNAME = username;
        this.PASSWORD = password;
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource(
                "/org/example/images/" + avatarName + ".jpeg")));
        Image scaled = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        this.AVATAR = new ImageIcon(scaled);
        this.SIMULATIONS = simulations;
        this.SUCCESS_SIMULATIONS = success_simulations;
    }

    public void addSimulations(){SIMULATIONS++;}
    public void addSuccesSimulations(){SUCCESS_SIMULATIONS++;}

    public String getUserName(){
        return this.USERNAME;
    }

    public String getPassWord(){
        return this.PASSWORD;
    }

    public ImageIcon getAVATAR(){
        return this.AVATAR;
    }

    public float getSuccesRate(){
        if(SUCCESS_SIMULATIONS == 0 || SIMULATIONS == 0){
            return 0.0f;
        }
        return (float) this.SUCCESS_SIMULATIONS/this.SIMULATIONS;
    }

}
