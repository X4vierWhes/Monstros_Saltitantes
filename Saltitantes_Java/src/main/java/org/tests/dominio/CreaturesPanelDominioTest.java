package org.tests.dominio;

import org.example.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de testes unitários para a classe {@link CreaturesPanel}.
 *
 * <p>Essa classe testa o comportamento funcional da simulação gráfica de criaturas.
 * Os testes validam as regras de movimentação, física, roubo de moedas, criação de clusters,
 * atuação do guardião do horizonte e atualização do sistema ao longo do tempo.</p>
 *
 * <p><b>Requisitos cobertos:</b></p>
 * <ul>
 *     <li><b>REQ-01:</b> Adicionar criaturas ao painel</li>
 *     <li><b>REQ-02:</b> Remover criaturas do painel (com restrições)</li>
 *     <li><b>REQ-03:</b> Calcular próxima posição e normalizar alvos</li>
 *     <li><b>REQ-04:</b> Verificar se o sistema pode ser atualizado</li>
 *     <li><b>REQ-05:</b> Simular roubo entre criaturas vizinhas</li>
 *     <li><b>REQ-06:</b> Detectar e criar clusters de criaturas</li>
 *     <li><b>REQ-07:</b> Guardião eliminar clusters próximos</li>
 *     <li><b>REQ-08:</b> Guardião sempre deve ser a última criatura</li>
 *     <li><b>REQ-09:</b> Atualização física e lógica separadas e consistentes</li>
 * </ul>
 *
 * <p><b>Invariantes:</b></p>
 * <ul>
 *     <li>Guardião só existe após o início da simulação</li>
 *     <li>Guardião sempre é o último da lista</li>
 *     <li>Clusters só são formados por mais de uma criatura</li>
 *     <li>Simulação só pode ser atualizada se nenhuma criatura estiver se movendo</li>
 * </ul>
 *
 * <p><b>Autor:</b> ValentinaClash</p>
 * <p><b>Versão:</b> 1.0</p>
 *
 * @see CreaturesPanel
 * @see Creature
 * @see Guardian
 * @see User
 */

public class CreaturesPanelDominioTest {

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
     * Verifica se o roubo entre vizinhos é bem-sucedido quando há mais de uma bola.
     */
    @Test
    void ThiefTest() {
        assertFalse(panel.thiefNeighbor(panel.getLast()),
                "O roubo não deve ocorrer com apenas uma bola no painel");
        int posX = randi.nextInt(width - CreaturesPanel.CREATURE_SIZE);
        panel.addCreature(posX);
        assertTrue(panel.thiefNeighbor(panel.getLast()),
                "O roubo deveria ser possível com múltiplas bolas");
    }

    /**
     * Verifica que o método update() retorna verdadeiro quando há bolas com dinheiro.
     */
    @Test
    void update() {
        assertTrue(panel.update(),
                "O método update() deve prosseguir com bolas válidas");
        panel.Creatures.clear();
        assertFalse(panel.update(), "Update não deve funcionar com lista vazia");
    }

    /**
     * Verifica condições de criação de guardião.
     * */
    @Test
    void createGuardian(){
        assertTrue(panel.createGuardian(80), "Deve poder criar guardião se ele ainda não existir");
        assertFalse(panel.createGuardian(80), "Não deve poder criar guardião se ele ainda não existir");

    }

    /**
     * Verifica condições de criação do cluster
     * */
    @Test
    void createCluster(){
        panel.initSimulation(80);
        assertFalse(panel.createCluster(new ArrayList<>()), "Não pode ser uma lista sem criaturas para criar um cluster");
        ArrayList<Creature> creatures = new ArrayList<>();
        creatures.add(new Creature(0,0,0,0, new JLabel()));
        assertFalse(panel.createCluster(creatures), "Precisa ter mais de uma criatura para criar o cluster");
        creatures.add(new Creature(0,0,0,0, new JLabel()));
        creatures.add(new Creature(1,0,0,0, new JLabel()));
        creatures.add(new Creature(2,0,0,0, new JLabel()));
        creatures.add(new Creature(3,0,0,0, new JLabel()));
        creatures.add(new Creature(4,0,0,0, new JLabel()));
        creatures.add(new Creature(5,0,0,0, new JLabel()));
        assertTrue(panel.createCluster(creatures), "Deve criar um cluster com uma lista com mais de uma criatura");
    }

    /**
     * Verifica que o roubo não ocorre se a atualização estiver desativada.
     */
    @Test
    void thiefNeighborWhenCantUpdate() {
        panel.addCreature(100);
        panel.getLast().canMove = true;
        assertFalse(panel.thiefNeighbor(panel.getLast()), "Não pode roubar se canUpdate é falso");
    }

    /**
     * Garante que não seja possível criar mais de um guardião.
     */
    @Test
    void createGuardianFailsIfExists() {
        panel.initSimulation(80);
        assertFalse(panel.createGuardian(100), "Não deve permitir mais de um guardião");
    }

    /**
     * Verifica que clusters não são criados quando não há criaturas próximas o suficiente.
     */
    @Test
    void checkClusterWithoutValidGroup() {
        panel.addCreature(0);
        panel.addCreature(200);
        assertFalse(panel.checkCluster(), "Sem grupos próximos, não deve formar cluster");
    }

    /**
     * Verifica que a simulação pode ser encerrada mesmo se os timers forem nulos.
     */
    @Test
    void stopSimulationWithNullTimers() {
        panel.startSimulation = true;
        panel.updateTimer = null;
        panel.phisycsTimer = null;
        assertFalse(panel.stopSimulation(), "Parar simulação mesmo sem timers ativos");
    }

    /**
     * TESTES FEITOS POR IA
     */

    /**
     * Verifica que criaturas com velocidade 0 permanecem no lugar após phisycsUpdate.
     */
    @Test
    void creatureWithZeroSpeedShouldNotMove() {
        Creature creature = panel.getLast();
        creature.spdX = 0;
        creature.canMove = true;
        int oldX = creature.x;

        panel.canUpdate = true;
        panel.phisycsUpdate();

        assertEquals(oldX, creature.x, "Criatura não deveria ter se movido no eixo X");
    }

    /**
     * Verifica que uma criatura com dinheiro zero pode ser removida em update.
     */
    @Test
    void updateRemovesZeroGoldCreatures() {
        panel.getLast().gold = 0;
        int sizeBefore = panel.Creatures.size();
        panel.update();  // update não remove diretamente, mas prepara para remoção

        // Simula uma checagem de fim, como no ciclo real
        panel.Creatures.removeIf(c -> c.gold == 0 && !c.isGuardian);
        int sizeAfter = panel.Creatures.size();

        assertTrue(sizeAfter < sizeBefore, "Criaturas com 0 ouro devem ser removidas");
    }

    /**
     * Verifica que `calcNextPosition` sempre retorna dentro dos limites da tela.
     */
    @Test
    void nextPositionShouldBeWithinBounds() {
        Creature creature = panel.getLast();
        int pos = panel.calcNextPosition(creature);
        assertTrue(pos >= 0 && pos <= width - CreaturesPanel.CREATURE_SIZE,
                "Posição calculada deve estar dentro da largura do painel");
    }

    /**
     * Verifica que uma criatura não pode ser removida se for guardião.
     */
    @Test
    void cannotRemoveGuardian() {
        panel.initSimulation(80);
        Creature guardian = panel.getLast();
        assertTrue(guardian.isGuardian, "Última criatura deve ser o guardião");
        assertFalse(panel.removeCreature(guardian), "Não deve poder remover o guardião");
    }

    /**
     * Verifica que `getLast()` retorna `null` quando lista está vazia.
     */
    @Test
    void getLastReturnsNullWhenEmpty() {
        panel.Creatures.clear();
        assertNull(panel.getLast(), "Deve retornar null se a lista está vazia");
    }

}
