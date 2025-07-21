package org.tests;

import org.example.model.Creature;
import org.example.model.CreaturesPanel;
import org.example.model.SQLite;
import org.example.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.swing.*;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CreaturesPanelFronteiraTest {

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

    /** Mock do banco de dados SQLite. */
    @Mock
    private SQLite bd;
    /**
     * Inicializa o painel e adiciona uma bola antes de cada teste.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        JFrame frame = new JFrame();
        User user = new User("Whesley", "1234", "dog");
        randi = new Random();
        panel = new CreaturesPanel(width, height, user);
        frame.add(panel);
        frame.pack();
        int posX = randi.nextInt(width - CreaturesPanel.CREATURE_SIZE);
        panel.addCreature(posX);
        target = panel.calcTarget(panel.getLast());
        panel.bd = bd;
    }

    /**
     * Libera os recursos após cada teste.
     */
    @AfterEach
    void tearDown() {
        panel.bd.close();
        panel = null;
        randi = null;
    }
    /**
     * Verifica se a próxima posição calculada está dentro dos limites do painel.
     */
    @Test
    void calcNextPositionTest() {
        int nextPosition = panel.calcNextPosition(panel.getLast());
        assertTrue(nextPosition >= 0 && nextPosition <= panel.getWidth(),
                "A próxima posição está fora dos limites do painel: " + nextPosition);
        nextPosition = -1;
        assertFalse(nextPosition >= 0 && nextPosition <= panel.getWidth(),
                "A próxima posição está fora dos limites do painel: " + nextPosition);
        nextPosition = panel.getWidth() + 10;
        assertFalse(nextPosition >= 0 && nextPosition <= panel.getWidth(),
                "A próxima posição está fora dos limites do painel: " + nextPosition);

    }

    /**
     * Verifica se a posição alvo calculada está fora dos limites antes da normalização.
     */
    @Test
    void NormalizedTarget() {
        assertTrue(target > panel.getWidth() || target < 0,
                "A posição alvo ainda não foi normalizada e está fora dos limites");

        System.out.println(panel.getWidth());
        assertTrue((panel.normalizedTarget(target) <= panel.getWidth()) &&
                        (panel.normalizedTarget(target) >= 0),
                "Posição alvo está normalizada, então não saiu do limite da tela");
    }

    /**
     * Verifica que o método getLast() nunca retorna null, mesmo após tentativa de remoção inválida.
     */
    @Test
    void testGetLastWithEmptyList() {
        panel.removeCreature(panel.getLast());
        assertNotNull(panel.getLast(),
                "getLast() não deve retornar null pois removeCreature não remove a última bola");
        panel.Creatures.clear();
        assertNull(panel.getLast(), "getLast deve retornar null se a lista estiver vazia");
    }

    /**
     * Verifica que a atualização física ainda ocorre mesmo com uma única bola.
     */
    @Test
    void phisycsUpdate() {
        // Deve retornar true com pelo menos uma criatura
        assertTrue(panel.phisycsUpdate(),
                "A atualização física deveria funcionar com uma bola");

        // Remove todas as criaturas
        panel.Creatures.clear();
        assertFalse(panel.phisycsUpdate(), "Atualização física não deve ocorrer com lista vazia");

        // Reinicializa com uma nova criatura
        panel.Creatures.clear();
        panel.addCreature(80);
        Creature c = panel.getLast();
        c.canMove = true;
        c.target = c.x + 10;
        panel.startSimulation = true;
        panel.canUpdate = true;

        int updates = 0;
        int maxUpdates = 1000;

        while (c.canMove && updates < maxUpdates) {
            panel.phisycsUpdate();
            updates++;
        }

        assertFalse(c.canMove, "A criatura deve parar de se mover ao atingir o alvo");
        assertEquals(c.target, c.x, "A posição final deve ser igual ao alvo");
    }

    /**
     * Verifica condições de fim da simulação por condição de vitoria
     * */
    @Test
    void checkEndConditionVictory() {
        for(Creature c: panel.Creatures){
            c.gold = 10;
        }
        panel.startSimulation = true;
        panel.createGuardian(80);
        panel.getLast().gold = 1000;
        panel.user.setPoints(1000.0);

        assertTrue(panel.checkEndCondition(), "Simulação deve terminar com vitória");

        for(Creature c: panel.Creatures){
            c.gold = 100;
        }
        panel.startSimulation = true;
        panel.createGuardian(80);
        panel.getLast().gold = 1000;
        panel.user.setPoints(400.0);

        assertFalse(panel.checkEndCondition(), "Simulação deve terminar com derrota");
    }
}
