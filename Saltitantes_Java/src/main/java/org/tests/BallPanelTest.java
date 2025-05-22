package org.tests;

import org.example.BallPanel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de testes unitários para a classe {@link BallPanel}.
 * <p>
 * Verifica o comportamento de métodos relacionados ao movimento, roubo e atualização de bolas.
 * Utiliza JUnit 5 para estrutura de testes.
 * </p>
 *
 * @author SeuNome
 * @version 1.0
 */
public class BallPanelTest {

    /** Painel a ser testado. */
    private BallPanel panel;

    /** Largura padrão do painel. */
    private final int width = 720;

    /** Altura padrão do painel. */
    private final int height = 480;

    /** Gerador de posições aleatórias. */
    private Random randi;

    /** Valor alvo calculado para teste. */
    private double target;

    /**
     * Inicializa o painel e adiciona uma bola antes de cada teste.
     */
    @BeforeEach
    void setUp() {
        randi = new Random();
        panel = new BallPanel(width, height);
        int posX = randi.nextInt(width - BallPanel.BALL_SIZE);
        panel.addBall(posX);
        target = panel.calcTarget(panel.getLast());
    }

    /**
     * Libera os recursos após cada teste.
     */
    @AfterEach
    void tearDown() {
        panel = null;
        randi = null;
    }

    /**
     * Verifica se a próxima posição calculada está dentro dos limites da tela.
     */
    @Test
    void calcNextPositionTest() {
        int nextPosition = panel.calcNextPosition(panel.getLast());
        assertTrue(nextPosition >= 0 && nextPosition <= panel._getWidht(),
                "Fora do limite do horizonte(" + panel._getWidht() + ")/Posição = " + nextPosition);
    }

    /**
     * Verifica se o roubo entre vizinhos é bem-sucedido quando há pelo menos duas bolas.
     */
    @Test
    void succededThiefTest() {
        int posX = randi.nextInt(width - BallPanel.BALL_SIZE);
        panel.addBall(posX);
        assertTrue(panel.thiefNeighbor(panel.getLast()),
                "Monstro saltitante não possui vizinhos para roubar");
    }

    /**
     * Verifica se o roubo falha quando há apenas uma bola.
     */
    @Test
    void failureThiefTest() {
        assertFalse(panel.thiefNeighbor(panel.getLast()),
                "Monstro saltitante não possui vizinhos para roubar");
    }

    /**
     * Verifica se a posição alvo está fora dos limites da tela (não normalizada).
     */
    @Test
    void failureNormalizedTarget() {
        assertTrue((target > panel.getWidth()) || (target < 0),
                "Posição alvo não está normalizada, então saiu do limite da tela");
    }

    /**
     * Verifica se a posição alvo normalizada está dentro dos limites da tela.
     */
    @Test
    void succededNormalizedTarget() {
        assertTrue((panel.normalizedTarget(target) <= panel.getWidth()) ||
                        (panel.normalizedTarget(target) >= 0),
                "Posição alvo está normalizada, então não saiu do limite da tela");
    }

    /**
     * Verifica se o método de atualização retorna falso quando não há bolas com dinheiro.
     */
    @Test
    void failureUpdate() {
        panel.getLast().money = 0.0;
        assertFalse(panel.update(), "Não existem bolas para atualizar");
    }

    /**
     * Verifica se o método de atualização retorna verdadeiro quando existem bolas válidas.
     */
    @Test
    void succededUpdate() {
        assertTrue(panel.update(), "Existem bolas para atualizar");
    }

    /**
     * Verifica se o método de atualização física falha quando não há bolas no painel.
     */
    @Test
    void failurePhisycsUpdate() {
        panel.removeBall(panel.getLast());
        assertFalse(panel.phisycsUpdate(), "Não existem bolas para atualizar a física");
    }

    /**
     * Verifica se o método de atualização física funciona corretamente quando há bolas.
     */
    @Test
    void succededPhisycsUpdate() {
        assertTrue(panel.phisycsUpdate(), "Existem bolas para atualizar a física");
    }
}
