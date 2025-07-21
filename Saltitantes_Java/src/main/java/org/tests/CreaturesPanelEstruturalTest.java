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

public class CreaturesPanelEstruturalTest {

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
     * Verifica se o guardião sempre é a última criatura da lista após eventos como simulação, adição e fusão.
     */
    @Test
    void guardianIsLast(){
        assertFalse(panel.getLast().isGuardian, "Guardião so deve aparecer ao iniciar simulação");
        panel.initSimulation(80);
        assertTrue(panel.getLast().isGuardian, "Ao iniciar simulação, guardião sempre deve ser o ultimo");
        panel.addCreature(80);
        assertTrue(panel.getLast().isGuardian, "Ao adicionar nova criatura, Guardião ainda deve ser o ultimo");
        assertFalse(panel.removeCreature(panel.getLast()), "Não pode deletar o ultimo se for guardiao");
        panel.addCreature(80);
        panel.addCreature(80);
        panel.addCreature(80);
        panel.addCreature(80);
        panel.addCreature(80);
        panel.checkCluster();
        assertTrue(panel.getLast().isGuardian, "Mesmo apos criar cluster, ultimo deve ser guardião" );
        panel.addCreature(80);
        panel.addCreature(80);
        panel.addCreature(90);
        panel.addCreature(90);
        panel.addCreature(80);
        panel.checkCluster();
        panel.checkGuardian();
        assertTrue(panel.getLast().isGuardian, "Mesmo apos criar cluster e Guardiao os eliminar. Guardiao deve ser o ultimo");
    }

    /**
     * Verifica que o painel não pode ser atualizado se alguma bola ainda estiver em movimento.
     */
    @Test
    void IsCanUpdate() {
        panel.getLast().canMove = true;
        assertFalse(panel.isCanUpdate(), "Não deve atualizar enquanto há bolas em movimento");
        panel.getLast().canMove = false;
        assertTrue(panel.isCanUpdate(), "Deve atualizar quando não há bolas em movimento");
    }

    /**
     * Verifica que a bola não pode ser removida se houver apenas uma no painel.
     */
    @Test
    void removeCreature(){
        assertFalse(panel.removeCreature(panel.getLast()), "Não deve deletar se so possuir uma bola");
        panel.addCreature(80);
        assertTrue(panel.removeCreature(panel.getLast()), "Deve poder deletar se houver mais de uma criatura e ela nao for guardiam");
        panel.initSimulation(80);
        assertFalse(panel.removeCreature(panel.getLast()), "Não deve poder deletar o guardião da lista");
        panel.addCreature(80);
        panel.addCreature(80);
        panel.addCreature(80);
        panel.addCreature(80);
        assertTrue(panel.removeCreature(panel.Creatures.getFirst()), "Deve poder deletar se não for guardião");
    }

    /**
     * Verifica condições em que Guardião elimina um cluster
     * */
    @Test
    void checkGuardian(){
        assertFalse(panel.checkGuardian(), "Se guardião nao estiver na lista ou simulação não estiver ativa. nao deve retornar true");
        panel.createGuardian(80);
        assertFalse(panel.checkGuardian(), "Se nao possui clusters, deve retornar false");
        for(int i = 0; i < 10; i++){
            panel.addCreature(100);
            panel.getLast().x = 100;
        }

        for(Creature c: panel.Creatures){
            c.x = 100;
        }
        panel.checkCluster();

        for(Creature c: panel.Creatures){
            c.x = 100;
        }

        assertTrue(panel.checkGuardian(), "Se houverem clusters proximos ao Guardiao, devem ser eliminados");

    }

    /**
     * Testa condições de inicio da simulação
     * */
    @Test
    void initSimulation(){
        panel.startSimulation = false;
        assertTrue(panel.initSimulation(80), "Simulação deveria iniciar pois esta parada");
        assertFalse(panel.initSimulation(80), "Simulação não deveria iniciar pois ja está iniciada");
    }

    /**
     * Testa condições de fim da simulação
     * */
    @Test
    void stopSimulation(){
        assertFalse(panel.stopSimulation(), "Simulação so pode ser parada se tiver começado");
        //Simulação termina com derrota
        panel.startSimulation = true;
        panel.user.setPoints(400.0);
        assertFalse(panel.stopSimulation(), "Pontos abaixo do objetivo resultam em derrota");
        panel.startSimulation = true;
        panel.user.setPoints(600.0);
        assertTrue(panel.stopSimulation(), "Pontos acima do objetivo resultam em vitoria");
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
