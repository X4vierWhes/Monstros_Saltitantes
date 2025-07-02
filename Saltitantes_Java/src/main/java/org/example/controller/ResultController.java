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
    public ResultController(User user, SQLite bd){
        this.user = user;
        this.bd = bd;
        view = new ResultView(this.user);
        initListeners();
    }

    private void initListeners() {
        view.getQuitButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.dispose();
                javax.swing.SwingUtilities.invokeLater(() -> {
                    UserController suser = new UserController(user, bd);
                });
            }
        });
    }
}
