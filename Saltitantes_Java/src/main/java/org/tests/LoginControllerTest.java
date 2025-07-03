package org.tests;

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

import static org.mockito.Mockito.*;

public class LoginControllerTest {
    @Mock
    private SQLite bd;
    @Mock
    private LoginView view;
    @InjectMocks
    private LoginController loginController;
    private JTextField actualMockUsernameField;
    private JPasswordField actualMockPasswordField;
    private JButton actualMockLoginButton;
    private JButton actualMockSignInButton;
    private JLabel actualMockStatusLabel;

    @BeforeEach
    void setUp(){
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

    @AfterEach
    void tearDown() {

    }

    @Test
    @DisplayName("Teste de LOGIN bem sucedido")
    void loginSuccess(){
        String username = "teste";
        String password = "1234";
        User user = new User(username, password, "dog");

        when(actualMockUsernameField.getText()).thenReturn(username);
        when(actualMockPasswordField.getPassword()).thenReturn(password.toCharArray());
        when(bd.findUserByUsername(username)).thenReturn(user);

        ArgumentCaptor<ActionListener> loginActionListenerCaptor = ArgumentCaptor.forClass(ActionListener.class);
        verify(actualMockLoginButton).addActionListener(loginActionListenerCaptor.capture());
        loginActionListenerCaptor.getValue().actionPerformed(mock(ActionEvent.class));

        verify(actualMockStatusLabel).setForeground(new Color(0, 128, 0)); // Cor verde para sucesso
        verify(actualMockStatusLabel).setText("Login realizado com sucesso!");
        verify(bd).findUserByUsername(username);
        verify(bd).close();
        verify(view).dispose();
    }

    @Test
    @DisplayName("Teste de LOGIN que nao deu certo")
    void failureLogin(){
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

    @Test
    @DisplayName("Sucesso ao registrar novo usuario")
    void succedSign(){
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

    @Test
    @DisplayName("Falha ao registrar novo usuario - Nome de usuário já existe")
    void failureSign_UsernameExists(){
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

    @Test
    @DisplayName("Falha ao registrar novo usuario - Campos vazios")
    void failureSign_EmptyFields(){
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