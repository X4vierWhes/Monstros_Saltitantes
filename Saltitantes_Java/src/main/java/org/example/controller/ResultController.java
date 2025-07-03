package org.example.controller;

import org.example.model.SQLite;
import org.example.model.User;
import org.example.view.ResultView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ResultController {
    private ResultView view;
    private SQLite bd;
    private User user;
    public ResultController(User user){
        this.user = user;
        this.bd = new SQLite();
        view = new ResultView(this.user, this.bd);
        initListeners();
    }

    private void initListeners() {
        view.getQuitButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bd.close();
                view.dispose();
                javax.swing.SwingUtilities.invokeLater(() -> {
                    UserController suser = new UserController(user);
                });
            }
        });
    }
}
