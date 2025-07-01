package org.example.model;

import java.sql.*;
import java.util.ArrayList;

public class SQLite {
    private final String url  = "jdbc:sqlite:base.db";

    private final String sqlTable = "CREATE TABLE IF NOT EXISTS users (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "username TEXT NOT NULL UNIQUE, " +
            "password TEXT NOT NULL, " +
            "avatar TEXT, " +
            "simulations INTEGER, " +
            "success INTEGER)";
    private Connection connection;

    public SQLite(){
        try {
            connection = DriverManager.getConnection(url);

            Statement stmt = connection.createStatement();

            stmt.execute(sqlTable);

        } catch (SQLException e) {
            System.err.println("Não foi possivel conectar ao SQL: " + e.getMessage());
        }
    }

    public boolean insertIntoUsers(User user){
        try {
            PreparedStatement insert = connection.prepareStatement(
                    "INSERT INTO users(username, password, avatar, simulations, success) VALUES(?,?,?,?,?)");

            insert.setString(1, user.getUserName());
            insert.setString(2, user.getPassWord());
            insert.setString(3, user.getAvatarname());
            insert.setInt(4, user.getSIMULATIONS());
            insert.setInt(5, user.getSUCCESS_SIMULATIONS());
            insert.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Não foi possivel adicionar: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteUserByUsername(String username){
        try {
            PreparedStatement delete = connection.prepareStatement(
                    "DELETE FROM users WHERE username = ?"
            );
            delete.setString(1, username);
            delete.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Não foi possivel deletar: " + e.getMessage());
        }
        return false;
    }

    public User findUserByUsername(String username){
        try {
            PreparedStatement find = connection.prepareStatement(
                    "SELECT * FROM users WHERE username = ?");

            find.setString(1, username);
            ResultSet resultSet = find.executeQuery();

            if (resultSet.next()){
                User user = new User(
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("avatar"),
                        resultSet.getInt("simulations"),
                        resultSet.getInt("success"));
                return user;
            }

        } catch (SQLException e) {
            System.err.println("Não foi possivel encontrar user: " + e.getMessage());
        }
        return null;
    }

    public ArrayList<User> getAllUsers(){
        try {
            PreparedStatement find = connection.prepareStatement(
                    "SELECT * FROM users");
            ResultSet resultSet = find.executeQuery();
            ArrayList<User> aux = new ArrayList<>();

            while (resultSet.next()) {
                User user = new User(
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("avatar"),
                        resultSet.getInt("simulations"),
                        resultSet.getInt("success")
                );

                aux.add(user);
            }

            if(!aux.isEmpty()){
                return aux;
            }

        } catch (SQLException e) {
            System.err.println("Não foi possivel retornar nenhum usuario: " +  e.getMessage());
        }
        return null;
    }

    public boolean editUserByUsername(String username, User editedUser){
        try {
            PreparedStatement edit = connection.prepareStatement(
                    "UPDATE users SET password = ?, avatar = ?, simulations = ?, success = ? WHERE username = ?");
            edit.setString(1, editedUser.getPassWord());
            edit.setString(2, editedUser.getAvatarname()); // ou o caminho do avatar
            edit.setInt(3, editedUser.getSIMULATIONS());
            edit.setInt(4, editedUser.getSUCCESS_SIMULATIONS());
            edit.setString(5, username);
            edit.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Não foi possivel editar: " + e.getMessage());
        }
        return false;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
        }
    }

}
