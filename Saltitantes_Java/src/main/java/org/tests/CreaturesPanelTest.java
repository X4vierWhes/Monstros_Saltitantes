package org.tests;

import org.example.model.Creature;
import org.example.model.CreaturesPanel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de testes unitários para a classe {@link CreaturesPanel}.
 * <p>
 * Verifica o comportamento dos métodos relacionados ao movimento,
 * roubo, adição, remoção, normalização e atualização de bolas.
 * Utiliza JUnit 5 como framework de testes.
 * </p>
 *
 * @author SeuNome
 * @version 1.0
 */
public class CreaturesPanelTest {

    /** Painel a ser testado. */
    private CreaturesPanel panel;

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
        JFrame frame = new JFrame();

        randi = new Random();
        panel = new CreaturesPanel(width, height);
        frame.add(panel);
        frame.pack();
        System.out.println(panel.getWidth());
        int posX = randi.nextInt(width - CreaturesPanel.CREATURE_SIZE);
        panel.addCreature(posX);
        target = panel.calcTarget(panel.getLast());
    }

    /**
     * Libera os recursos após cada teste.
     */
    @AfterEach
    void tearDown() {
        panel = null;
        randi = null;
        //target = 0.0;
    }

    /**
     * Verifica que o painel não pode ser atualizado se alguma bola ainda estiver em movimento.
     */
    @Test
    void failureIsCanUpdate() {
        panel.getLast().canMove = true;
        assertFalse(panel.isCanUpdate(), "Não deve atualizar enquanto há bolas em movimento");
    }

    /**
     * Verifica que o painel pode ser atualizado se nenhuma bola estiver em movimento.
     */
    @Test
    void succededIsCanUpdate() {
        assertTrue(panel.isCanUpdate(), "Deve atualizar quando não há bolas em movimento");
    }

    /**
     * Verifica se uma nova bola é adicionada corretamente ao painel.
     */
    @Test
    void testAddCreature() {
        int posX = randi.nextInt(width - CreaturesPanel.CREATURE_SIZE);
        panel.addCreature(posX);
        assertNotNull(panel.getLast(), "A bola não foi adicionada corretamente");
        assertNotEquals(posX, panel.getLast().x, "A posição X da bola deveria ter sido normalizada");
    }

    /**
     * Verifica que a bola não pode ser removida se houver apenas uma no painel.
     */
    @Test
    void testRemoveCreature() {
        assertFalse(panel.removeCreature(panel.getLast()),
                "A bola não pode ser removida se for a única no painel");
    }

    /**
     * Verifica se uma bola pode ser removida corretamente quando há mais de uma.
     */
    @Test
    void testRemoveCreatureMoreOneCreature() {
        Creature lastCreature = panel.getLast();
        int posX = randi.nextInt(width - CreaturesPanel.CREATURE_SIZE);
        panel.addCreature(posX);
        panel.removeCreature(lastCreature);
        assertNotSame(lastCreature, panel.getLast(),
                "A bola antiga ainda é a última após a tentativa de remoção");
    }

    /**
     * Verifica que o método getLast() nunca retorna null, mesmo após tentativa de remoção inválida.
     */
    @Test
    void testGetLastWithEmptyList() {
        panel.removeCreature(panel.getLast());
        assertNotNull(panel.getLast(),
                "getLast() não deve retornar null pois removeCreature não remove a última bola");
    }

    /**
     * Verifica se a próxima posição calculada está dentro dos limites do painel.
     */
    @Test
    void calcNextPositionTest() {
        int nextPosition = panel.calcNextPosition(panel.getLast());
        assertTrue(nextPosition >= 0 && nextPosition <= panel.getWidth(),
                "A próxima posição está fora dos limites do painel: " + nextPosition);
    }

    /**
     * Verifica se o roubo entre vizinhos é bem-sucedido quando há mais de uma bola.
     */
    @Test
    void succededThiefTest() {
        int posX = randi.nextInt(width - CreaturesPanel.CREATURE_SIZE);
        panel.addCreature(posX);
        assertTrue(panel.thiefNeighbor(panel.getLast()),
                "O roubo deveria ser possível com múltiplas bolas");
    }

    /**
     * Verifica que o roubo falha quando há apenas uma bola.
     */
    @Test
    void failureThiefTest() {
        assertFalse(panel.thiefNeighbor(panel.getLast()),
                "O roubo não deve ocorrer com apenas uma bola no painel");
    }

    /**
     * Verifica se a posição alvo calculada está fora dos limites antes da normalização.
     */
    @Test
    void failureNormalizedTarget1() {
        assertTrue(target > panel.getWidth() || target < 0,
                "A posição alvo ainda não foi normalizada e está fora dos limites");
    }

    /**
     * Verifica se a posição alvo normalizada está dentro dos limites do painel.
     */
    @Test
    void succededNormalizedTarget() {
        System.out.println(panel.getWidth());
        assertTrue((panel.normalizedTarget(target) <= panel.getWidth()) &&
                        (panel.normalizedTarget(target) >= 0),
                "Posição alvo está normalizada, então não saiu do limite da tela");
    }

    /**
     * Verifica que o método update() retorna verdadeiro quando há bolas com dinheiro.
     */
    @Test
    void succededUpdate() {
        assertTrue(panel.update(),
                "O método update() deve prosseguir com bolas válidas");
    }

    /**
     * Verifica que a atualização física ainda ocorre mesmo com uma única bola.
     */
    @Test
    void failurePhisycsUpdate() {
        panel.removeCreature(panel.getLast());
        assertTrue(panel.phisycsUpdate(),
                "A atualização física deveria continuar com uma bola restante");
    }

    /**
     * Verifica se a atualização física ocorre corretamente quando há bolas no painel.
     */
    @Test
    void succededPhisycsUpdate() {
        assertTrue(panel.phisycsUpdate(),
                "A atualização física deve ocorrer normalmente com bolas no painel");
    }
}
