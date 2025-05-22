package org.tests;

import org.example.Ball;
import org.example.BallPanel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class BallPanelTest {
    private BallPanel panel;
    private final int width = 720;
    private final int height = 480;
    private Random randi;
    private double target;

    @BeforeEach
    void setUp(){
        randi = new Random();
        panel = new BallPanel(width, height);
        int posX = randi.nextInt(width - BallPanel.BALL_SIZE);
        panel.addBall(posX);
        target = panel.calcTarget(panel.getLast());

    }

    @AfterEach
    void tearDown(){
        panel = null;
        randi = null;
    }

    @Test
    void calcNextPositionTest() {
        int nextPosition = panel.calcNextPosition(panel.getLast());
        assertTrue(nextPosition >= 0 && nextPosition <= panel._getWidht(),
               "Fora do limite do horizonte(" + panel._getWidht() + ")/Posição = " + nextPosition);
    }

    @Test
    void succededThiefTest(){
        int posX = randi.nextInt(width - BallPanel.BALL_SIZE);
        panel.addBall(posX);
        assertTrue(panel.thiefNeighbor(panel.getLast()),
                "Monstro saltitante não possui vizinhos para roubar");
    }

    @Test
    void failureThiefTest(){
        assertFalse(panel.thiefNeighbor(panel.getLast()), "Monstro saltitante não possui vizinhos para roubar");
    }

    @Test
    void failureNormalizedTarget(){
        assertTrue((target  > panel.getWidth()) || (target < 0),
                "Posição alvo não está normalizada, ent" +
                        "ão saiu do limite da tela");
    }

    @Test
    void succededNormalizedTarget(){
        assertTrue((panel.normalizedTarget(target)  <= panel.getWidth()) || (panel.normalizedTarget(target) >= 0),
                "Posição alvo está normalizada, ent" +
                        "ão não saiu do limite da tela");
    }

    @Test
    void failureUpdate(){
        panel.getLast().money = 0.0;
        assertFalse(panel.update(), "Não existem bolas para atualizar");
    }

    @Test
    void succededUpdate(){
        assertTrue(panel.update(), "Existem bolas para atualizar");
    }

    @Test
    void failurePhisycsUpdate(){
        panel.removeBall(panel.getLast());
        assertFalse(panel.phisycsUpdate(), "Não existem bolas para atualizar a fisica");
    }

    @Test
    void succededPhisycsUpdate(){
        assertTrue(panel.phisycsUpdate(), "Existem bolas para atualizar a fisica");
    }
}
