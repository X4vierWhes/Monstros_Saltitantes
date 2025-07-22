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
import java.awt.*;
import java.awt.event.ActionEvent; // Necessário para simular eventos de Timer
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*; // Para usar verify, times, eq

public class CreaturesPanelEstruturalTest {

    /** Painel a ser testado. */
    private CreaturesPanel panel;

    /** Largura padrão do painel. */
    private final int width = 720;

    /** Altura padrão do painel. */
    private final int height = 480;

    /** Gerador de posições aleatórias. */
    private Random randi;

    /** Mock do banco de dados SQLite. */
    @Mock
    private SQLite bd;

    /** Mock do usuário. */
    @Mock
    private User mockUser;

    /**
     * Inicializa o painel e adiciona uma bola antes de cada teste.
     * Agora, usa o mockUser para evitar NotAMockException.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this); // Inicializa os mocks @Mock
        JFrame frame = new JFrame();
        randi = new Random();
        panel = new CreaturesPanel(width, height, mockUser, bd); // Passa o mockUser e bd
        frame.add(panel);
        frame.pack();
        int posX = randi.nextInt(width - CreaturesPanel.CREATURE_SIZE);
        panel.addCreature(posX);
        // Não é necessário calcular 'target' no setUp para todos os testes estruturais
    }

    /**
     * Libera os recursos após cada teste.
     */
    @AfterEach
    void tearDown() {
        // Verifica se os timers estão rodando antes de tentar pará-los para evitar NullPointerException
        if (panel.phisycsTimer != null && panel.phisycsTimer.isRunning()) {
            panel.phisycsTimer.stop();
        }
        if (panel.updateTimer != null && panel.updateTimer.isRunning()) {
            panel.updateTimer.stop();
        }
        // Fechar o BD, se não for null
        if (panel.bd != null) {
            panel.bd.close();
        }
        panel = null;
        randi = null;
    }

    // --- Testes Estruturais Adicionais/Aprimorados ---

    /**
     * Testa o construtor que aceita apenas User (para cobertura do segundo construtor).
     */
    @Test
    void testConstructorWithOnlyUser() {
        // Remova a criatura inicial para começar limpo
        panel.Creatures.clear();
        CreaturesPanel newPanel = new CreaturesPanel(width, height, mockUser);
        assertNotNull(newPanel);
        assertEquals(width, newPanel.getPreferredSize().width);
        assertEquals(height, newPanel.getPreferredSize().height);
        assertEquals(Color.BLACK, newPanel.getBackground());
        assertNull(newPanel.bd, "O construtor com apenas User não deve inicializar 'bd'");
    }

    /**
     * Testa o método addCreature para cobrir o branch onde o 'last' NÃO é um guardião.
     */
    @Test
    void addCreature_lastIsNotGuardianBranch() {
        panel.Creatures.clear();
        Creature existingCreature = new Creature(50, 100, 1, 1, new JLabel());
        panel.Creatures.add(existingCreature); // Adiciona uma criatura que não é guardião

        panel.addCreature(200);
        assertEquals(2, panel.Creatures.size(), "Deve ter duas criaturas na lista.");
        assertEquals(existingCreature, panel.Creatures.getFirst(), "A primeira criatura deve ser a existente.");
        assertFalse(panel.getLast().isGuardian, "A última criatura não deve ser um guardião.");
    }

    /**
     * Testa o método addCreature para cobrir o branch onde o 'last' É um guardião.
     * Garante que o guardião é removido temporariamente e recolocado.
     */
    @Test
    void addCreature_lastIsGuardianBranch() {
        panel.Creatures.clear();
        panel.addCreature(50); // Adiciona uma criatura normal primeiro
        panel.createGuardian(300); // Adiciona um guardião, que se torna o último

        Creature initialGuardian = panel.getLast();
        assertTrue(initialGuardian.isGuardian, "A última criatura deve ser um guardião.");

        panel.addCreature(200); // Adiciona uma nova criatura

        assertEquals(3, panel.Creatures.size(), "Deve ter três criaturas (original, nova, guardião).");
        assertEquals(initialGuardian, panel.getLast(), "O guardião deve ser o último após a adição.");
        assertFalse(panel.Creatures.get(1).isGuardian, "A criatura do meio deve ser a nova e não um guardião.");
    }

    /**
     * Testa o método `createCluster` cobrindo o branch de `if (grupo.size() > 1)` no `checkCluster()`.
     */
    @Test
    void createCluster_successfulClusterCreationBranch() {

        // Crie um guardião para que o cluster possa ser criado
        panel.createGuardian(300);
        Creature guardian = panel.getLast(); // Salva a referência do guardião

        panel.addCreature(100);
        panel.addCreature(100);
        panel.addCreature(100);
        double creaturesGold = 0.0;
        for(Creature aux: panel.Creatures){
            if(!aux.isGuardian){
                aux.x = 100;
                creaturesGold += aux.gold;
            }
        }

        // Simula o comportamento do checkCluster ao chamar createCluster
        boolean clusterCreated = panel.createCluster((ArrayList<Creature>) panel.Creatures);

        assertTrue(clusterCreated, "Um cluster deve ser criado quando há criaturas colidindo e um guardião.");
        assertEquals(2, panel.Creatures.size(), "Deve haver o cluster e o guardião na lista."); // c1 e c2 são removidos, 1 cluster é adicionado.
        Creature newCluster = panel.Creatures.getFirst();
        assertTrue(newCluster.isCluster, "A nova criatura deve ser um cluster.");
        assertEquals(creaturesGold, newCluster.gold, "O ouro do cluster deve ser a soma das criaturas.");
    }

    /**
     * Testa o método `createCluster` cobrindo o branch de `if(!getLast().isGuardian) return false;`.
     */
    @Test
    void createCluster_noGuardianBranch() {
        panel.Creatures.clear();
        Creature c1 = new Creature(100, 10, 1, 1, new JLabel());
        Creature c2 = new Creature(100 + CreaturesPanel.fuseDistance / 2, 10, 1, 1, new JLabel());
        panel.Creatures.add(c1);
        panel.Creatures.add(c2); // Nenhuma criatura guardiã

        ArrayList<Creature> collidingCreatures = new ArrayList<>();
        collidingCreatures.add(c1);
        collidingCreatures.add(c2);

        boolean clusterCreated = panel.createCluster(collidingCreatures);
        assertFalse(clusterCreated, "Nenhum cluster deve ser criado se não houver guardião.");
        assertEquals(2, panel.Creatures.size(), "A lista de criaturas não deve mudar.");
    }


    /**
     * Testa o método `isCanUpdate` cobrindo o branch `if (aux.canMove && !aux.isGuardian) return false;`.
     */
    @Test
    void isCanUpdate_creatureCanMoveBranch() {
        panel.Creatures.clear();
        Creature movingCreature = new Creature(100, 10, 1, 1, new JLabel());
        movingCreature.canMove = true;
        panel.Creatures.add(movingCreature);

        assertFalse(panel.isCanUpdate(), "isCanUpdate deve ser false se uma criatura normal pode mover.");
    }

    /**
     * Testa o método `thiefNeighbor` cobrindo o branch `if (Creatures.size() <= 1) return false;`.
     */
    @Test
    void thiefNeighbor_singleCreatureBranch() {
        panel.Creatures.clear();
        panel.addCreature(100);
        assertFalse(panel.thiefNeighbor(panel.getLast()), "thiefNeighbor deve retornar false com apenas uma criatura.");
    }


    /**
     * Testa o método `thiefNeighbor` cobrindo o branch `!thief.isGuardian`.
     */
    @Test
    void thiefNeighbor_thiefIsGuardianBranch() {

        panel.createGuardian(100);
        Creature guardian = panel.getLast();
        Creature victim = new Creature(200, 10, 1, 1, new JLabel());
        panel.Creatures.add(0, victim); // Adiciona vítima antes do guardião

        panel.canUpdate = true;
        // O método thiefNeighbor tem uma condição "!neighbor.isGuardian". O "thief" pode ser um guardião e ainda tentar roubar, mas ele não vai se considerar um vizinho.
        // O teste precisa verificar se o guardião NÃO rouba. No seu método, ele só rouba se a criatura não for um guardião.
        assertFalse(panel.thiefNeighbor(guardian), "Um guardião não deve tentar roubar.");
    }

    /**
     * Testa o método `update` cobrindo o branch `if (Creatures.isEmpty()) return false;`.
     */
    @Test
    void update_emptyListBranch() {
        panel.Creatures.clear();
        assertFalse(panel.update(), "update deve retornar false com a lista de criaturas vazia.");
    }


    /**
     * Testa o método `phisycsUpdate` cobrindo o branch `if (Creatures.isEmpty()) return false;`.
     */
    @Test
    void phisycsUpdate_emptyListBranch() {
        panel.Creatures.clear();
        assertFalse(panel.phisycsUpdate(), "phisycsUpdate deve retornar false com a lista de criaturas vazia.");
    }


    /**
     * Testa o método `checkGuardian` cobrindo o branch `if(!toRemove.isEmpty())`.
     */
    @Test
    void checkGuardian_noClustersToRemoveBranch() {
        panel.createGuardian(100);
        Creature guardian = panel.getLast();
        assertTrue(guardian.isGuardian);

        // Adiciona uma criatura normal, não um cluster
        panel.addCreature(120);
        assertFalse(panel.checkGuardian(), "checkGuardian deve retornar false se não houver clusters para remover.");
        assertEquals(3, panel.Creatures.size(), "Nenhuma criatura deve ser removida.");
    }

    /**
     * Testa o método `checkCluster` cobrindo o branch `if (base.isGuardian) continue;`.
     */
    @Test
    void checkCluster_guardianSkippedBranch() {
        panel.Creatures.clear();
        panel.addCreature(50); // Uma criatura normal
        panel.createGuardian(100); // Um guardião
        panel.addCreature(150); // Outra criatura normal
        // A lista agora é: [Creature, Creature, Guardian]

        // As criaturas normais estão muito distantes para formar cluster entre si
        // Mas o guardião NÃO deve ser considerado para formação de cluster normal

        boolean clusterFormed = panel.checkCluster();
        assertFalse(clusterFormed, "Não deve formar cluster se as criaturas não colidem ou se apenas um guardião existe.");
        assertEquals(3, panel.Creatures.size(), "O guardião não deve ser removido ou afetado pelo checkCluster para formar cluster.");
    }

    /**
     * Testa o método `removeCreature` cobrindo o branch `if (Creatures.size() <= 1 || remove.isGuardian)`.
     */
    @Test
    void removeCreature_cannotRemoveBranch() {
        // Teste 1: Apenas uma criatura
        panel.Creatures.clear();
        panel.addCreature(100);
        assertFalse(panel.removeCreature(panel.getLast()), "Não deve remover se for a única criatura.");
        assertEquals(1, panel.Creatures.size());

        // Teste 2: Remover guardião
        panel.addCreature(200); // Adiciona uma segunda criatura
        panel.createGuardian(300); // Adiciona um guardião
        Creature guardian = panel.getLast();
        assertFalse(panel.removeCreature(guardian), "Não deve remover o guardião.");
        assertEquals(3, panel.Creatures.size()); // 2 normais + 1 guardião
    }

    /**
     * Testa o método `removeCreature` cobrindo o branch de remoção bem-sucedida.
     */
    @Test
    void removeCreature_successfulRemovalBranch() {
        panel.Creatures.clear();
        Creature c1 = new Creature(10, 10, 1, 1, new JLabel());
        Creature c2 = new Creature(20, 20, 1, 1, new JLabel());
        panel.Creatures.add(c1);
        panel.Creatures.add(c2);

        assertTrue(panel.removeCreature(c1), "Deve remover a criatura c1.");
        assertEquals(1, panel.Creatures.size());
        assertFalse(panel.Creatures.contains(c1), "c1 não deve estar na lista.");
        assertTrue(panel.Creatures.contains(c2), "c2 deve permanecer na lista.");
    }

    /**
     * Testa o método `getLast` cobrindo o branch `if (Creatures.isEmpty()) return null;`.
     */
    @Test
    void getLast_emptyListBranch() {
        panel.Creatures.clear();
        assertNull(panel.getLast(), "getLast deve retornar null para uma lista vazia.");
    }

    /**
     * Testa o método `initSimulation` cobrindo o branch `if(!startSimulation)`.
     */
    @Test
    void initSimulation_successfulStartBranch() {
        panel.startSimulation = false; // Garante que a simulação não está ativa
        assertTrue(panel.initSimulation(100), "A simulação deve iniciar.");
        assertTrue(panel.startSimulation, "startSimulation deve ser true.");
        assertNotNull(panel.phisycsTimer, "phisycsTimer deve ser inicializado.");
        assertNotNull(panel.updateTimer, "updateTimer deve ser inicializado.");
        assertTrue(panel.phisycsTimer.isRunning(), "phisycsTimer deve estar rodando.");
        assertTrue(panel.updateTimer.isRunning(), "updateTimer deve estar rodando.");
        verify(mockUser, times(1)).addSimulations();
        verify(bd, times(1)).editUserByUsername(eq(mockUser.getUserName()), eq(mockUser));
        assertTrue(panel.getLast().isGuardian, "Um guardião deve ser criado.");
    }

    /**
     * Testa o método `initSimulation` cobrindo o branch `else { return false; }`.
     */
    @Test
    void initSimulation_alreadyStartedBranch() {
        panel.startSimulation = true; // Simula que a simulação já está ativa
        assertFalse(panel.initSimulation(100), "A simulação não deve iniciar novamente.");
        verify(mockUser, never()).addSimulations(); // Não deve chamar novamente
        verify(bd, never()).editUserByUsername(any(), any()); // Não deve chamar novamente
    }

    /**
     * Testa o método `stopSimulation` cobrindo o branch `if(!startSimulation)`.
     */
    @Test
    void stopSimulation_notStartedBranch() {
        panel.startSimulation = false;
        assertFalse(panel.stopSimulation(), "Não deve parar se a simulação não começou.");
    }

    /**
     * Testa o método `stopSimulation` cobrindo o branch de vitória (`user.getPoints() >= 500`).
     */
    @Test
    void stopSimulation_victoryBranch() {
        panel.startSimulation = true;
        panel.user = mockUser; // Garante que estamos usando o mockUser
        when(mockUser.getPoints()).thenReturn(600.0); // Simula pontos de vitória
        panel.startPhisycsTimer(); // Inicia os timers para poder pará-los
        panel.startUpdateTimer();

        // Para simular a caixa de diálogo do JOptionPane, que bloqueia o teste,
        // você pode mockar a classe JOptionPane ou, para testes estruturais,
        // focar no comportamento lógico e verificar as chamadas de método.
        // Como JOptionPane.showMessageDialog é uma chamada estática, mocking é complexo.
        // Apenas verificaremos o estado e as interações com o mockUser/bd.

        assertTrue(panel.stopSimulation(), "stopSimulation deve retornar true para vitória.");
        assertFalse(panel.startSimulation, "startSimulation deve ser false após parar.");
        assertFalse(panel.phisycsTimer.isRunning(), "phisycsTimer deve estar parado.");
        assertFalse(panel.updateTimer.isRunning(), "updateTimer deve estar parado.");
        verify(mockUser, times(1)).addSuccesSimulations(); // Chamada de vitória
        verify(mockUser, times(1)).setPoints(eq(0.0)); // Pontos resetados
        verify(bd, times(1)).editUserByUsername(eq(mockUser.getUserName()), eq(mockUser));
    }

    /**
     * Testa o método `stopSimulation` cobrindo o branch de derrota (`else { ret = false; }`).
     */
    @Test
    void stopSimulation_defeatBranch() {
        panel.startSimulation = true;
        panel.user = mockUser; // Garante que estamos usando o mockUser
        when(mockUser.getPoints()).thenReturn(300.0); // Simula pontos de derrota
        panel.startPhisycsTimer();
        panel.startUpdateTimer();

        assertFalse(panel.stopSimulation(), "stopSimulation deve retornar false para derrota.");
        assertFalse(panel.startSimulation, "startSimulation deve ser false após parar.");
        verify(mockUser, never()).addSuccesSimulations(); // Não deve chamar vitória
        verify(mockUser, times(1)).setPoints(eq(0.0)); // Pontos resetados
        verify(bd, times(1)).editUserByUsername(eq(mockUser.getUserName()), eq(mockUser));
    }

    /**
     * Testa o método `checkEndCondition` cobrindo o branch `if (!startSimulation) return false;`.
     */
    @Test
    void checkEndCondition_notStartedBranch() {
        panel.startSimulation = false;
        assertFalse(panel.checkEndCondition(), "checkEndCondition deve retornar false se a simulação não começou.");
    }

}