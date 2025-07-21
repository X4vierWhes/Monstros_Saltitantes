package org.tests;

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

}
