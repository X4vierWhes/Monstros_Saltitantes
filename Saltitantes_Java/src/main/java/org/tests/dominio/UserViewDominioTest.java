package org.tests;

import org.example.model.User;
import org.example.view.UserView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.image.BufferedImage;


import static org.junit.jupiter.api.Assertions.*;
/**
 * TESTES FEITOS POR IA*/
/**
 * Classe de testes de domínio para a interface {@link UserView},
 * validando a exibição e manipulação de informações do usuário na interface gráfica.
 */
class UserViewDominioTest {

    private UserView userView;
    private User user;

    /**
     * Configura um usuário com avatar fictício para testes de domínio.
     */
    @BeforeEach
    void setUp() {
        user = new User("Teste", "123", "common") {
            @Override
            public ImageIcon getAVATAR() {
                return new ImageIcon(new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB));
            }
        };

        userView = new UserView(user);
    }

    /**
     * Verifica se a janela foi criada com título e dimensões esperadas.
     */
    @Test
    void testCriacaoJanelaUsuario() {
        assertEquals("Tela de Usuario", userView.getTitle());
        assertEquals(720, userView.getWidth());
        assertEquals(480, userView.getHeight());
        assertTrue(userView.isVisible());
    }

    /**
     * Verifica se o botão de iniciar simulação está presente e com texto correto.
     */
    @Test
    void testBotaoIniciarExiste() {
        JButton init = userView.getInitButton();
        assertNotNull(init);
        assertEquals("Iniciar", init.getText());
    }

    /**
     * Verifica se o botão de exibir resultados está presente.
     */
    @Test
    void testBotaoResultadosExiste() {
        JButton result = userView.getResultButton();
        assertNotNull(result);
        assertEquals("Resultados", result.getText());
    }

    /**
     * Verifica se o botão de deletar usuário está presente.
     */
    @Test
    void testBotaoDeletarExiste() {
        JButton delete = userView.getDeleteButton();
        assertNotNull(delete);
        assertEquals("Deletar", delete.getText());
    }

    /**
     * Verifica se o botão de sair da interface está presente.
     */
    @Test
    void testBotaoSairExiste() {
        JButton quit = userView.getQuitButton();
        assertNotNull(quit);
        assertEquals("Sair", quit.getText());
    }

    /**
     * Verifica se a troca de avatar ocorre corretamente com usuário válido.
     */
    @Test
    void testTrocaAvatarUsuarioValido() {
        boolean resultado = userView.changeAvatar(user);
        assertTrue(resultado);
    }

    /**
     * Verifica se a troca de avatar falha corretamente com usuário nulo.
     */
    @Test
    void testTrocaAvatarUsuarioNulo() {
        boolean resultado = userView.changeAvatar(null);
        assertFalse(resultado);
    }
}
