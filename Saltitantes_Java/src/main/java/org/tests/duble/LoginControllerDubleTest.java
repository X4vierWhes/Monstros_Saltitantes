package org.tests.duble;

import org.example.controller.LoginController;
import org.example.model.SQLite;
import org.example.model.User;
import org.example.view.LoginView;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import static org.mockito.Mockito.*;

/**
 * Classe de testes unitários para a classe {@link LoginController}.
 * <p>
 * Testa os fluxos de autenticação e cadastro de usuários, utilizando mocks
 * para simular a interação com a interface gráfica e banco de dados SQLite.
 */
public class LoginControllerDubleTest {

    /** Mock do banco de dados SQLite. */
    @Mock
    private SQLite bd;

    /** Mock da interface de login. */
    @Mock
    private LoginView view;

    /** Instância do controller sendo testada, com injeção dos mocks. */
    @InjectMocks
    private LoginController loginController;

    private JTextField actualMockUsernameField;
    private JPasswordField actualMockPasswordField;
    private JButton actualMockLoginButton;
    private JButton actualMockSignInButton;
    private JLabel actualMockStatusLabel;

    /**
     * Inicializa os mocks e configura o comportamento esperado da view.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        actualMockUsernameField = mock(JTextField.class);
        actualMockPasswordField = mock(JPasswordField.class);
        actualMockStatusLabel = mock(JLabel.class);
        actualMockLoginButton = mock(JButton.class);
        actualMockSignInButton = mock(JButton.class);

        when(view.getUsernameField()).thenReturn(actualMockUsernameField);
        when(view.getPasswordField()).thenReturn(actualMockPasswordField);
        when(view.getStatusLabel()).thenReturn(actualMockStatusLabel);
        when(view.getLoginButton()).thenReturn(actualMockLoginButton);
        when(view.getSignInButton()).thenReturn(actualMockSignInButton);

        loginController.initialize();
    }

    /**
     * Método chamado após cada teste.
     */
    @AfterEach
    void tearDown() {
        // Nenhuma ação necessária no momento
    }

    /**
     * Testa o fluxo de login bem-sucedido.
     */
    @Test
    @DisplayName("Teste de LOGIN bem sucedido")
    void loginSuccess() throws SQLException {
        String username = "teste";
        String password = "1234";
        User user = new User(username, password, "dog");

        when(actualMockUsernameField.getText()).thenReturn(username);
        when(actualMockPasswordField.getPassword()).thenReturn(password.toCharArray());
        when(bd.findUserByUsername(username)).thenReturn(user);

        ArgumentCaptor<ActionListener> loginActionListenerCaptor = ArgumentCaptor.forClass(ActionListener.class);
        verify(actualMockLoginButton).addActionListener(loginActionListenerCaptor.capture());
        loginActionListenerCaptor.getValue().actionPerformed(mock(ActionEvent.class));

        verify(actualMockStatusLabel).setForeground(new Color(0, 128, 0));
        verify(actualMockStatusLabel).setText("Login realizado com sucesso!");
        verify(bd).findUserByUsername(username);
        verify(bd).close();
        verify(view).dispose();
    }

    /**
     * Testa o fluxo de login com falha (usuário não encontrado).
     */
    @Test
    @DisplayName("Teste de LOGIN que nao deu certo")
    void failureLogin() throws SQLException {
        String username = "teste";
        String password = "1234";

        when(actualMockUsernameField.getText()).thenReturn(username);
        when(actualMockPasswordField.getPassword()).thenReturn(password.toCharArray());
        when(bd.findUserByUsername(username)).thenReturn(null);

        ArgumentCaptor<ActionListener> loginActionListenerCaptor = ArgumentCaptor.forClass(ActionListener.class);
        verify(actualMockLoginButton).addActionListener(loginActionListenerCaptor.capture());
        loginActionListenerCaptor.getValue().actionPerformed(mock(ActionEvent.class));

        verify(actualMockStatusLabel).setForeground(Color.RED);
        verify(actualMockStatusLabel).setText("Usuário ou password inválidos.");
        verify(bd).findUserByUsername(username);
        verify(bd, never()).close();
        verify(view, never()).dispose();
    }

    /**
     * Testa o fluxo de cadastro de usuário com sucesso.
     */
    @Test
    @DisplayName("Sucesso ao registrar novo usuario")
    void succedSign() {
        String username = "teste";
        String password = "1234";

        when(actualMockUsernameField.getText()).thenReturn(username);
        when(actualMockPasswordField.getPassword()).thenReturn(password.toCharArray());
        when(bd.insertIntoUsers(any(User.class))).thenReturn(true);

        ArgumentCaptor<ActionListener> signActionListenerCaptor = ArgumentCaptor.forClass(ActionListener.class);
        verify(actualMockSignInButton).addActionListener(signActionListenerCaptor.capture());
        signActionListenerCaptor.getValue().actionPerformed(mock(ActionEvent.class));

        verify(bd).insertIntoUsers(argThat(userArg ->
                userArg.getUserName().equals(username) &&
                        userArg.getPassWord().equals(password) &&
                        userArg.getAvatarname().equals("common")
        ));

        verify(actualMockStatusLabel).setForeground(new Color(0, 255, 0));
        verify(actualMockStatusLabel).setText("Conta criada com sucesso!");

        verify(bd, never()).close();
        verify(view, never()).dispose();
    }

    /**
     * Testa o fluxo de cadastro de usuário com falha por nome de usuário já existente.
     */
    @Test
    @DisplayName("Falha ao registrar novo usuario - Nome de usuário já existe")
    void failureSign_UsernameExists() throws SQLException {
        String username = "usuarioExistente";
        String password = "senhaQualquer";
        User existingUser = new User(username, "outraSenha", "common");

        when(actualMockUsernameField.getText()).thenReturn(username);
        when(actualMockPasswordField.getPassword()).thenReturn(password.toCharArray());
        when(bd.insertIntoUsers(any(User.class))).thenReturn(false);
        when(bd.findUserByUsername(username)).thenReturn(existingUser);

        ArgumentCaptor<ActionListener> signActionListenerCaptor = ArgumentCaptor.forClass(ActionListener.class);
        verify(actualMockSignInButton).addActionListener(signActionListenerCaptor.capture());
        signActionListenerCaptor.getValue().actionPerformed(mock(ActionEvent.class));

        verify(actualMockStatusLabel).setForeground(Color.RED);
        verify(actualMockStatusLabel).setText("Nome de usuário ocupado!");

        verify(bd).insertIntoUsers(argThat(userArg ->
                userArg.getUserName().equals(username) &&
                        userArg.getPassWord().equals(password)
        ));

        verify(bd).findUserByUsername(username);
        verify(bd, never()).close();
        verify(view, never()).dispose();
    }

    /**
     * Testa o fluxo de cadastro de usuário com falha por campos vazios.
     */
    @Test
    @DisplayName("Falha ao registrar novo usuario - Campos vazios")
    void failureSign_EmptyFields() throws SQLException {
        String username = "";
        String password = "";

        when(actualMockUsernameField.getText()).thenReturn(username);
        when(actualMockPasswordField.getPassword()).thenReturn(password.toCharArray());
        when(bd.findUserByUsername(anyString())).thenReturn(null);

        ArgumentCaptor<ActionListener> signActionListenerCaptor = ArgumentCaptor.forClass(ActionListener.class);
        verify(actualMockSignInButton).addActionListener(signActionListenerCaptor.capture());
        signActionListenerCaptor.getValue().actionPerformed(mock(ActionEvent.class));

        verify(actualMockStatusLabel).setForeground(Color.RED);
        verify(actualMockStatusLabel).setText("Campo de username ou password em branco");
        verify(bd, never()).insertIntoUsers(any(User.class));
        verify(bd, never()).findUserByUsername(anyString());
        verify(bd, never()).close();
        verify(view, never()).dispose();
    }
}
