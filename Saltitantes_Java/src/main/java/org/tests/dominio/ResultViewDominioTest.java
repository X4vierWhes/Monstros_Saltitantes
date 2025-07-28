package org.tests.dominio;

import org.example.model.SQLite;
import org.example.model.User;
import org.example.view.ResultView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.*;
import java.awt.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TESTES FEITOS POR IA*/

/**
 * Classe de testes de domínio para a interface {@link ResultView},
 * responsável por validar a exibição correta de informações do usuário
 * e do ranking, além da integridade de seus componentes gráficos.
 */
class ResultViewDominioTest {

    private User mockUser;
    private SQLite mockDB;

    /**
     * Configura os objetos mockados do {@link User} e {@link SQLite} antes de cada teste.
     * Define valores simulados para o usuário atual e para a lista de usuários do ranking.
     */
    @BeforeEach
    void setUp() {
        mockUser = Mockito.mock(User.class);
        mockDB = Mockito.mock(SQLite.class);

        Mockito.when(mockUser.getUserName()).thenReturn("TestUser");
        Mockito.when(mockUser.getSIMULATIONS()).thenReturn(5);
        Mockito.when(mockUser.getSUCCESS_SIMULATIONS()).thenReturn(3);
        Mockito.when(mockUser.getSuccessRate()).thenReturn((float) 3 / 5);
        Mockito.when(mockUser.getUserName()).thenReturn("common");

        ArrayList<User> users = new ArrayList<>();
        User u1 = Mockito.mock(User.class);
        Mockito.when(u1.getUserName()).thenReturn("User1");
        Mockito.when(u1.getSIMULATIONS()).thenReturn(10);
        Mockito.when(u1.getSUCCESS_SIMULATIONS()).thenReturn(5);
        Mockito.when(u1.getSuccessRate()).thenReturn((float) 5 / 10);
        Mockito.when(u1.getTotalPoints()).thenReturn(100.0);
        users.add(u1);

        Mockito.when(mockDB.getAllUsers()).thenReturn(users);
    }

    /**
     * Verifica se a {@link ResultView} é criada corretamente com título,
     * dimensões esperadas e visibilidade ativa.
     */
    @Test
    void testCriacaoJanelaComUsuarioValido() {
        ResultView view = new ResultView(mockUser, mockDB);
        assertEquals("Tela de Resultados", view.getTitle());
        assertEquals(720, view.getWidth());
        assertEquals(480, view.getHeight());
        assertTrue(view.isVisible());
    }

    /**
     * Verifica se o botão de sair (quit) está presente e possui o texto "Voltar"
     * com largura de 75 pixels.
     */
    @Test
    void testBotaoQuitExiste() {
        ResultView view = new ResultView(mockUser, mockDB);
        JButton quitButton = view.getQuitButton();
        assertNotNull(quitButton);
        assertEquals("Voltar", quitButton.getText());
        assertEquals(75, quitButton.getWidth());
    }

    /**
     * Verifica se o ranking de usuários é renderizado corretamente
     * quando há dados retornados do banco.
     */
    @Test
    void testRankingRenderizadoComUsuarios() {
        ResultView view = new ResultView(mockUser, mockDB);
        Component[] components = view.getContentPane().getComponents();
        boolean rankingEncontrado = false;
        for (Component comp : components) {
            if (comp instanceof JScrollPane) {
                rankingEncontrado = true;
                break;
            }
        }
        assertTrue(rankingEncontrado);
    }

    /**
     * Verifica se a interface trata corretamente o caso em que
     * a lista de usuários retornada é nula.
     */
    @Test
    void testNaoCriaRankingComListaNula() {
        Mockito.when(mockDB.getAllUsers()).thenReturn(null);
        ResultView view = new ResultView(mockUser, mockDB);
        Component[] components = view.getContentPane().getComponents();
        boolean rankingEncontrado = false;
        for (Component comp : components) {
            if (comp instanceof JScrollPane) {
                rankingEncontrado = true;
                break;
            }
        }
        assertFalse(rankingEncontrado);
    }

    /**
     * Verifica se a interface trata corretamente o caso em que
     * a lista de usuários está vazia.
     */
    @Test
    void testNaoCriaRankingComListaVazia() {
        Mockito.when(mockDB.getAllUsers()).thenReturn(new ArrayList<>());
        ResultView view = new ResultView(mockUser, mockDB);
        Component[] components = view.getContentPane().getComponents();
        boolean rankingEncontrado = false;
        for (Component comp : components) {
            if (comp instanceof JScrollPane) {
                rankingEncontrado = true;
                break;
            }
        }
        assertFalse(rankingEncontrado);
    }
}
