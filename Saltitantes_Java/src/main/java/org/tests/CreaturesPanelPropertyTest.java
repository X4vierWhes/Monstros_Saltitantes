package org.tests;

import org.example.model.Creature;
import org.example.model.CreaturesPanel;
import org.example.model.SQLite;
import org.example.model.User;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import net.jqwik.api.Property;
import net.jqwik.api.ForAll;
import net.jqwik.api.Provide;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.constraints.IntRange;

import javax.swing.*;
import java.util.List;
import java.util.Random;

public class CreaturesPanelPropertyTest {

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
     * Teste de propriedade: O guardião deve sempre ser a última criatura,
     * independentemente de quantas criaturas são adicionadas antes ou depois.
     */
    @Property
    boolean guardianAlwaysLast(@ForAll("creaturePositions") List<@IntRange(min = 0, max = 700) Integer> positions) {
        MockitoAnnotations.initMocks(this);
        JFrame frame = new JFrame();
        User user = new User("Whesley", "1234", "dog");
        randi = new Random();
        panel = new CreaturesPanel(width, height, user);
        frame.add(panel);
        frame.pack();
        panel.bd = bd;


        // Adiciona criaturas antes da simulação
        for (int pos : positions) {
            panel.addCreature(pos);
        }

        // Inicia simulação (guardião é criado aqui)
        panel.initSimulation(80);

        // Adiciona criaturas após o guardião
        for (int pos : positions) {
            panel.addCreature(pos);
        }

        // Verifica se o último é o guardião
        return panel.getLast().isGuardian;
    }

    /**
     * Teste de propriedade: Criaturas devem sempre estar nos limites da tela
     * independentemente de quantas atualizações fisicas são feitas.
     */
    @Property
    boolean creaturesAlwaysWithinBounds(@ForAll("creaturePositions") List<@IntRange(min=0, max=700) Integer> positions) {
        MockitoAnnotations.initMocks(this);
        User user = new User("Test", "123", "common");
        panel = new CreaturesPanel(width, height, user);

        for(int pos : positions) {
            panel.addCreature(pos);
        }
        // Simula alguns passos de física
        for(int i=0; i<10; i++) {
            panel.phisycsUpdate();
        }

        synchronized (panel.Creatures) {
            for(Creature c : panel.Creatures) {
                if (c.x < 0 || c.x > width - CreaturesPanel.CREATURE_SIZE) {
                    return false;
                }
            }
        }
        return true;
    }


    @Provide
    Arbitrary<List<Integer>> creaturePositions() {
        return Arbitraries.integers().between(0, width - CreaturesPanel.CREATURE_SIZE)
                .list()
                .ofMinSize(1)
                .ofMaxSize(20);
    }
}
