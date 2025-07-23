package org.tests;

import org.example.model.Creature;
import org.example.model.CreaturesPanel;
import org.example.model.Guardian;
import org.example.model.SQLite;
import org.example.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    /** Mock do usuário. */
    @Mock
    private User mockUser;

    /**
     * Inicializa o painel e adiciona uma bola antes de cada teste.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        JFrame frame = new JFrame();
        //mockUser = new User("TestUser", "pass", "common");
        randi = new Random();
        panel = new CreaturesPanel(width, height, mockUser, bd); // Pass the mock bd
        frame.add(panel);
        frame.pack();
        int posX = randi.nextInt(width - CreaturesPanel.CREATURE_SIZE);
        panel.addCreature(posX);
        target = panel.calcTarget(panel.getLast());
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
        assertTrue(nextPosition >= 0 && nextPosition <= panel.getWidth() - CreaturesPanel.CREATURE_SIZE,
                "A próxima posição está fora dos limites do painel: " + nextPosition);

        // Teste com valor extremo mínimo para calcTarget
        Creature c = new Creature(0, 0, 0, 0, new JLabel());
        c.gold = -1000000;
        nextPosition = panel.calcNextPosition(c);
        assertTrue(nextPosition >= 0, "A próxima posição não deve ser negativa");

        // Teste com valor extremo máximo para calcTarget
        c.gold = 1000000;
        nextPosition = panel.calcNextPosition(c);
        assertTrue(nextPosition <= panel.getWidth() - CreaturesPanel.CREATURE_SIZE, "A próxima posição não deve exceder a largura do painel");
    }

    /**
     * Verifica se a posição alvo calculada está fora dos limites antes da normalização.
     */
    @Test
    void NormalizedTarget() {
        // Teste para garantir que a normalização funcione para valores positivos muito grandes
        int positiveTarget = 10000000;
        Creature aux = new Creature(80,80, 1,1, new JLabel());
        aux.target = positiveTarget;
        double normalizedPositive = panel.normalizedTarget(panel.calcTarget(aux));
        assertTrue(normalizedPositive >= 0.0 && normalizedPositive <= 1.0,
                "Normalização de alvo positivo falhou: " + normalizedPositive);

        // Teste para valor zero
        double zeroTarget = 0.0;
        double normalizedZero = panel.normalizedTarget(zeroTarget);
        assertTrue(normalizedZero >= 0.0 && normalizedZero <= 1.0,
                "Normalização de alvo zero falhou: " + normalizedZero);
    }

    /**
     * Verifica que o método getLast() nunca retorna null, mesmo após tentativa de remoção inválida.
     */
    @Test
    void testGetLastWithEmptyList() {
        // Inicialmente com uma criatura
        assertNotNull(panel.getLast(), "getLast() não deve retornar null após setUp");

        // Tenta remover a única criatura (não deve remover se for a única criatura normal)
        panel.removeCreature(panel.getLast());
        assertNotNull(panel.getLast(),
                "getLast() não deve retornar null pois removeCreature não remove a última bola se for a única");

        // Adiciona um guardião e uma criatura normal, depois tenta remover o guardião (não deve remover)
        panel.addCreature(10);
        panel.createGuardian(20);
        Creature guardian = panel.getLast();
        assertTrue(guardian.isGuardian);
        panel.removeCreature(guardian);
        assertNotNull(panel.getLast(), "Não deve remover o guardião");
        assertTrue(panel.getLast().isGuardian, "O guardião ainda deve ser o último na lista");

        panel.Creatures.clear();
        assertNull(panel.getLast(), "getLast deve retornar null se a lista estiver vazia");
    }

    /**
     * Verifica que a atualização física ainda ocorre mesmo com uma única bola.
     */
    @Test
    void phisycsUpdate() {
        // Deve retornar true com pelo menos uma criatura
        assertTrue(panel.phisycsUpdate(), "A atualização física deveria funcionar com uma bola");

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

        // Teste de pulo (y-position)
        c.y = 100;
        c.spdY = 0;
        int initialY = c.y;
        panel.phisycsUpdate();
        assertTrue(c.y > initialY, "A criatura deve cair devido à gravidade");
        panel.phisycsUpdate();
        panel.phisycsUpdate();

        // Simula a criatura atingindo o chão e pulando
        for (int i = 0; i < 50; i++) {
            panel.phisycsUpdate();
        }
        assertEquals(panel.getHeight() - CreaturesPanel.CREATURE_SIZE - 40, c.y, "A criatura deve estar no chão após o pulo e queda");
    }

    /**
     * Testa o método thiefNeighbor em condições de fronteira.
     */
    @Test
    void thiefNeighborBoundaryTests() {
        // Cenário 1: Apenas uma criatura (não deve ocorrer roubo)
        panel.Creatures.clear();
        panel.addCreature(100);
        assertFalse(panel.thiefNeighbor(panel.getLast()), "Roubo não deve ocorrer com apenas uma criatura");

        // Cenário 2: Duas criaturas, uma tenta roubar a outra
        panel.Creatures.clear();
        Creature thief = new Creature(100, panel.getHeight() - CreaturesPanel.CREATURE_SIZE - 40, 1, 1, new JLabel());
        thief.gold = 1000;
        Creature victim = new Creature(200, panel.getHeight() - CreaturesPanel.CREATURE_SIZE - 40, 1, 1, new JLabel());
        victim.gold = 500;
        panel.Creatures.add(thief);
        panel.Creatures.add(victim);

        panel.canUpdate = true; // Permite a atualização
        assertTrue(panel.thiefNeighbor(thief), "Roubo deve ocorrer entre duas criaturas");
        assertEquals(1250, thief.gold, "Ladrão deve ter a metade do ouro da vítima adicionado");
        assertEquals(250, victim.gold, "Vítima deve ter a metade do ouro removido");


        // Cenário 3: Roubo quando canUpdate é false
        panel.Creatures.clear();
        thief = new Creature(100, panel.getHeight() - CreaturesPanel.CREATURE_SIZE - 40, 1, 1, new JLabel());
        thief.gold = 1000;
        victim = new Creature(200, panel.getHeight() - CreaturesPanel.CREATURE_SIZE - 40, 1, 1, new JLabel());
        victim.gold = 500;
        panel.Creatures.add(thief);
        panel.Creatures.add(victim);

        for(Creature aux: panel.Creatures){
            aux.canMove = true;
        }
        assertFalse(panel.thiefNeighbor(thief), "Roubo não deve ocorrer quando canUpdate é false");
        assertEquals(1000, thief.gold, "Ouro do ladrão não deve mudar");
        assertEquals(500, victim.gold, "Ouro da vítima não deve mudar");

        // Cenário 4: Ladrão é um guardião (não deve roubar)
        panel.Creatures.clear();
        Guardian guardianThief = new Guardian(100, panel.getHeight() - CreaturesPanel.CREATURE_SIZE - 40, 1, 1, new JLabel());
        guardianThief.gold = 1000;
        victim = new Creature(200, panel.getHeight() - CreaturesPanel.CREATURE_SIZE - 40, 1, 1, new JLabel());
        victim.gold = 500;
        panel.Creatures.add(guardianThief);
        panel.Creatures.add(victim);

        panel.canUpdate = true;

        assertFalse(panel.thiefNeighbor(guardianThief), "Guardiao não deve roubar");
        assertEquals(1000, guardianThief.gold, "Ouro do guardião não deve mudar se ele tentar roubar");
        assertEquals(500, victim.gold, "Ouro da vítima não deve mudar se o guardião tentar roubar");
    }

    /**
     * Testa a criação de um cluster com diferentes números de criaturas colidindo.
     */
    @Test
    void createClusterBoundaryTests() {
        // Cenário 1: Nenhuma criatura colidindo
        panel.Creatures.clear();
        assertFalse(panel.createCluster(new ArrayList<>()), "Não deve criar cluster com lista vazia");

        // Cenário 2: Apenas uma criatura colidindo
        panel.Creatures.clear();
        ArrayList<Creature> singleCreatureList = new ArrayList<>();
        singleCreatureList.add(new Creature(10, 10, 1, 1, new JLabel()));
        assertFalse(panel.createCluster(singleCreatureList), "Não deve criar cluster com apenas uma criatura");

        // Cenário 3: Duas criaturas colidindo
        panel.Creatures.clear();
        panel.addCreature(100);
        panel.addCreature(100);
        double creaturesGold = 0.0;

        for(Creature aux: panel.Creatures){
            aux.x = 100;
            creaturesGold += aux.gold;
        }
        panel.createGuardian(1);

        assertTrue(panel.createCluster((ArrayList<Creature>) panel.Creatures), "Deve criar cluster com duas criaturas");
        assertEquals(2, panel.Creatures.size(), "Deve haver o cluster e o guardião na lista"); // O cluster + o guardião
        Creature cluster = panel.Creatures.getFirst();
        assertTrue(cluster.isCluster, "A criatura criada deve ser um cluster");
        assertEquals(creaturesGold, cluster.gold, "O ouro do cluster deve ser a soma das criaturas colidindo");

        // Cenário 4: Múltiplas criaturas colidindo
        panel.Creatures.clear();
        panel.addCreature(100);
        panel.addCreature(100);
        panel.addCreature(100);
        panel.addCreature(100);
        panel.addCreature(100);
        panel.addCreature(100);
        creaturesGold = 0.0;

        for(Creature aux: panel.Creatures){
            aux.x = 100;
            creaturesGold += aux.gold;
        }
        panel.createGuardian(1);

        assertTrue(panel.createCluster((ArrayList<Creature>) panel.Creatures), "Deve criar cluster com múltiplas criaturas");
        assertEquals(2, panel.Creatures.size(), "Deve haver o cluster e o guardião na lista");
        cluster = panel.Creatures.getFirst();
        assertTrue(cluster.isCluster, "A criatura criada deve ser um cluster");
        assertEquals(creaturesGold, cluster.gold, "O ouro do cluster deve ser a soma das criaturas colidindo");
    }

    /**
     * Testa a criação de um guardião em condições de fronteira.
     */
    @Test
    void createGuardianBoundaryTests() {
        // Cenário 1: Criar guardião quando a lista está vazia
        panel.createGuardian(100);
        assertTrue(panel.getLast().isGuardian, "A criatura criada deve ser um guardião");

        // Cenário 2: Criar guardião quando já existe um guardião

        assertFalse(panel.createGuardian(200), "Não deve criar guardião se já existe um");
        assertEquals(2, panel.Creatures.size(), "A lista de criaturas deve continuar com apenas um guardião");
    }

    /**
     * Testa o método isCanUpdate em condições de fronteira.
     */
    @Test
    void isCanUpdateBoundaryTests() {
        // Cenário 1: Lista vazia
        panel.Creatures.clear();
        assertTrue(panel.isCanUpdate(), "isCanUpdate deve ser true se a lista de criaturas estiver vazia");

        // Cenário 2: Apenas uma criatura que não pode mover
        panel.Creatures.clear();
        Creature c1 = new Creature(10, 10, 1, 1, new JLabel());
        c1.canMove = false;
        panel.Creatures.add(c1);
        assertTrue(panel.isCanUpdate(), "isCanUpdate deve ser true se a única criatura não puder mover");

        // Cenário 3: Uma criatura que pode mover
        panel.Creatures.clear();
        c1 = new Creature(10, 10, 1, 1, new JLabel());
        c1.canMove = true;
        panel.Creatures.add(c1);
        assertFalse(panel.isCanUpdate(), "isCanUpdate deve ser false se houver uma criatura que pode mover");

        // Cenário 4: Múltiplas criaturas, todas paradas
        panel.Creatures.clear();
        c1 = new Creature(10, 10, 1, 1, new JLabel());
        c1.canMove = false;
        Creature c2 = new Creature(20, 20, 1, 1, new JLabel());
        c2.canMove = false;
        panel.Creatures.add(c1);
        panel.Creatures.add(c2);
        assertTrue(panel.isCanUpdate(), "isCanUpdate deve ser true se todas as criaturas estiverem paradas");

        // Cenário 5: Múltiplas criaturas, algumas paradas e uma se movendo
        panel.Creatures.clear();
        c1 = new Creature(10, 10, 1, 1, new JLabel());
        c1.canMove = false;
        c2 = new Creature(20, 20, 1, 1, new JLabel());
        c2.canMove = true;
        panel.Creatures.add(c1);
        panel.Creatures.add(c2);
        assertFalse(panel.isCanUpdate(), "isCanUpdate deve ser false se alguma criatura puder mover");

        // Cenário 6: Guardião que pode mover (não afeta isCanUpdate para roubo)
        panel.Creatures.clear();
        Guardian guardian = new Guardian(10, 10, 1, 1, new JLabel());
        guardian.canMove = true; // Guardião pode se mover
        panel.Creatures.add(guardian);
        assertTrue(panel.isCanUpdate(), "isCanUpdate deve ser true mesmo se o guardião pode mover");
    }


    /**
     * Testa o método removeCreature em condições de fronteira.
     */
    @Test
    void removeCreatureBoundaryTests() {
        // Cenário 1: Remover a única criatura (não deve remover)
        panel.Creatures.clear();
        panel.addCreature(100);
        assertFalse(panel.removeCreature(panel.getLast()), "Não deve remover se for a única criatura");
        assertEquals(1, panel.Creatures.size(), "A lista deve continuar com uma criatura");

        // Cenário 2: Remover um guardião (não deve remover)
        panel.Creatures.clear();
        panel.addCreature(50); // Adiciona uma criatura normal
        panel.createGuardian(100); // Adiciona um guardião
        Creature guardian = panel.getLast();
        assertTrue(guardian.isGuardian);
        assertFalse(panel.removeCreature(guardian), "Não deve remover o guardião");
        assertEquals(2, panel.Creatures.size(), "A lista deve continuar com o guardião e a criatura normal");

        // Cenário 3: Remover uma criatura normal com múltiplas criaturas presentes
        panel.Creatures.clear();
        Creature c1 = new Creature(10, 10, 1, 1, new JLabel());
        Creature c2 = new Creature(20, 20, 1, 1, new JLabel());
        Creature c3 = new Creature(30, 30, 1, 1, new JLabel());
        panel.Creatures.add(c1);
        panel.Creatures.add(c2);
        panel.Creatures.add(c3);

        assertTrue(panel.removeCreature(c2), "Deve remover a criatura do meio");
        assertEquals(2, panel.Creatures.size(), "A lista deve ter duas criaturas");
        assertFalse(panel.Creatures.contains(c2), "A criatura c2 não deve estar na lista");
    }

    /**
     * Testa o método initSimulation em condições de fronteira.
     */
    @Test
    void initSimulationBoundaryTests() {
        // Cenário 1: Iniciar simulação pela primeira vez
        panel.startSimulation = false;
        assertTrue(panel.initSimulation(100), "Deve iniciar a simulação pela primeira vez");
        assertTrue(panel.startSimulation, "startSimulation deve ser true");
        assertNotNull(panel.phisycsTimer, "phisycsTimer deve ser inicializado");
        assertNotNull(panel.updateTimer, "updateTimer deve ser inicializado");
        assertTrue(panel.getLast().isGuardian, "Um guardião deve ser criado");
        verify(mockUser, times(1)).addSimulations();
        verify(bd, times(1)).editUserByUsername(eq(mockUser.getUserName()), eq(mockUser));

        // Cenário 2: Tentar iniciar simulação novamente quando já está ativa
        assertTrue(panel.startSimulation);
        assertFalse(panel.initSimulation(100), "Não deve iniciar simulação se já estiver ativa");
        verify(mockUser, times(1)).addSimulations();
        verify(bd, times(1)).editUserByUsername(eq(mockUser.getUserName()), eq(mockUser));
    }
}