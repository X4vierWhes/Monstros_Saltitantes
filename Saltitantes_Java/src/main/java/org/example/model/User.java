package org.example.model;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.Vector;

public class User {
    private String USERNAME;
    private String PASSWORD;
    private ImageIcon AVATAR;
    private String AVATAR_NAME;
    private int SIMULATIONS;
    private int SUCCESS_SIMULATIONS;
    private boolean isAdm = false;

    private static int index = 0;

    public User(String username, String password, String avatarName){
        this.USERNAME = username;
        this.PASSWORD = password;
        this.setAvatar(avatarName);
        this.SIMULATIONS = 0;
        this.SUCCESS_SIMULATIONS = 0;
    }

    public User(String username, String password, String avatarName, int simulations, int success_simulations){
        this.USERNAME = username;
        this.PASSWORD = password;
        this.setAvatar(avatarName);
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

    public int getSIMULATIONS(){return SIMULATIONS;}

    public int getSUCCESS_SIMULATIONS(){return SUCCESS_SIMULATIONS;}

    public ImageIcon getAVATAR(){
        return this.AVATAR;
    }

    public float getSuccesRate(){
        if(SUCCESS_SIMULATIONS == 0 || SIMULATIONS == 0){
            return 0.0f;
        }
        return (float) this.SUCCESS_SIMULATIONS/this.SIMULATIONS;
    }

    public boolean isAdm() {
        return isAdm;
    }

    public void setAdm(boolean adm) {
        isAdm = adm;
    }

    public String getAvatarname() {
        return AVATAR_NAME;
    }

    public void setAvatar(String avatarname){
        this.AVATAR_NAME = avatarname;
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource(
                "/org/example/images/" + avatarname + ".jpeg")));
        Image scaled = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        this.AVATAR = new ImageIcon(scaled);
    }
    public void changeAvatar(Vector<String> imgs){
        this.setAvatar(imgs.get(index));
        index = (index + 1) % imgs.size();
    }
}
