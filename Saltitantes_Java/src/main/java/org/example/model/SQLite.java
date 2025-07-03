package org.example.model;

import java.sql.*;
import java.util.ArrayList;

/**
 * Classe responsável por gerenciar o acesso e a manipulação de dados no banco de dados SQLite.
 *
 * <p>Ela encapsula todas as operações de persistência dos usuários, incluindo criação de tabela,
 * inserção, remoção, busca, edição e listagem. Toda a comunicação é realizada via JDBC.</p>
 *
 * <p>O banco de dados utilizado é um arquivo local chamado <code>base.db</code>.</p>
 *
 * @author ValentinaClash
 * @version 1.0
 * @see User
 */
public class SQLite {

    /** URL de conexão com o banco de dados SQLite. */
    private final String url = "jdbc:sqlite:base.db";

    /** Comando SQL para criar a tabela de usuários, se ainda não existir. */
    private final String sqlTable = "CREATE TABLE IF NOT EXISTS users (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "username TEXT NOT NULL UNIQUE, " +
            "password TEXT NOT NULL, " +
            "avatar TEXT, " +
            "simulations INTEGER, " +
            "success INTEGER," +
            "totalPoints DOUBLE)";

    /** Conexão ativa com o banco de dados. */
    private Connection connection;

    /**
     * Construtor padrão. Inicializa a conexão com o banco de dados e cria a tabela de usuários, se necessário.
     */
    public SQLite() {
        try {
            connection = DriverManager.getConnection(url);
            Statement stmt = connection.createStatement();
            stmt.execute(sqlTable);
        } catch (SQLException e) {
            System.err.println("Não foi possível conectar ao SQL: " + e.getMessage());
        }
    }

    /**
     * Insere um novo usuário na tabela.
     *
     * @param user Instância de {@link User} contendo os dados a serem inseridos.
     * @return {@code true} se a inserção for bem-sucedida, {@code false} em caso de erro.
     */
    public boolean insertIntoUsers(User user) {
        try {
            PreparedStatement insert = connection.prepareStatement(
                    "INSERT INTO users(username, password, avatar, simulations, success, totalpoints) VALUES(?,?,?,?,?, ?)");

            insert.setString(1, user.getUserName());
            insert.setString(2, user.getPassWord());
            insert.setString(3, user.getAvatarname());
            insert.setInt(4, user.getSIMULATIONS());
            insert.setInt(5, user.getSUCCESS_SIMULATIONS());
            insert.setDouble(6, user.getTotalPoints());
            insert.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Não foi possível adicionar: " + e.getMessage());
        }
        return false;
    }

    /**
     * Remove um usuário do banco de dados com base no nome de usuário.
     *
     * @param username Nome de usuário a ser deletado.
     * @return {@code true} se o usuário for removido com sucesso, {@code false} caso contrário.
     */
    public boolean deleteUserByUsername(String username) {
        try {
            PreparedStatement delete = connection.prepareStatement(
                    "DELETE FROM users WHERE username = ?"
            );
            delete.setString(1, username);
            delete.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Não foi possível deletar: " + e.getMessage());
        }
        return false;
    }

    /**
     * Busca um usuário no banco de dados pelo nome de usuário.
     *
     * @param username Nome de usuário a ser buscado.
     * @return Instância de {@link User} se encontrado, {@code null} caso contrário.
     */
    public User findUserByUsername(String username) {
        try {
            PreparedStatement find = connection.prepareStatement(
                    "SELECT * FROM users WHERE username = ?");

            find.setString(1, username);
            ResultSet resultSet = find.executeQuery();

            if (resultSet.next()) {
                return new User(
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("avatar"),
                        resultSet.getInt("simulations"),
                        resultSet.getInt("success"),
                        resultSet.getDouble("totalpoints"));
            }

        } catch (SQLException e) {
            System.err.println("Não foi possível encontrar user: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retorna todos os usuários cadastrados no banco de dados.
     *
     * @return Lista de usuários ou {@code null} se ocorrer erro ou não houver usuários.
     */
    public ArrayList<User> getAllUsers() {
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
                        resultSet.getInt("success"),
                        resultSet.getDouble("totalpoints")
                );
                aux.add(user);
            }

            return aux.isEmpty() ? null : aux;

        } catch (SQLException e) {
            System.err.println("Não foi possível retornar nenhum usuário: " + e.getMessage());
        }
        return null;
    }

    /**
     * Edita os dados de um usuário existente com base no nome de usuário.
     *
     * @param username    Nome do usuário a ser editado.
     * @param editedUser  Objeto {@link User} com os novos dados.
     * @return {@code true} se a atualização for bem-sucedida, {@code false} em caso de erro.
     */
    public boolean editUserByUsername(String username, User editedUser) {
        try {
            PreparedStatement edit = connection.prepareStatement(
                    "UPDATE users SET password = ?, avatar = ?, simulations = ?, success = ?, totalpoints = ? WHERE username = ?");
            edit.setString(1, editedUser.getPassWord());
            edit.setString(2, editedUser.getAvatarname());
            edit.setInt(3, editedUser.getSIMULATIONS());
            edit.setInt(4, editedUser.getSUCCESS_SIMULATIONS());
            edit.setString(5, username);
            edit.setDouble(6, editedUser.getTotalPoints());
            edit.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Não foi possível editar: " + e.getMessage());
        }
        return false;
    }

    /**
     * Fecha a conexão com o banco de dados se ela estiver aberta.
     */
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
