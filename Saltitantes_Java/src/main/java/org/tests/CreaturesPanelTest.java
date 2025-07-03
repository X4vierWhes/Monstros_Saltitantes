package org.tests;

import org.example.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.ArrayList;
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
 * @author ValentinaClash
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
        User user = new User("Whesley", "1234", "dog");
        randi = new Random();
        panel = new CreaturesPanel(width, height, user);
        frame.add(panel);
        frame.pack();
        int posX = randi.nextInt(width - CreaturesPanel.CREATURE_SIZE);
        panel.addCreature(posX);
        target = panel.calcTarget(panel.getLast());
        panel.bd = new SQLite();
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
    void failureIsCanUpdate() {
        panel.getLast().canMove = true;
        assertFalse(panel.isCanUpdate(), "Não deve atualizar enquanto há bolas em movimento");
    }

    /**
     * Verifica que o painel pode ser atualizado se nenhuma bola estiver em movimento.
     */
    @Test
    void succededIsCanUpdate() {
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
    void succededUpdate() {
        assertTrue(panel.update(),
                "O método update() deve prosseguir com bolas válidas");
    }

    /**
     * Verifica que a atualização física ainda ocorre mesmo com uma única bola.
     */
    @Test
    void PhisycsUpdate() {
        panel.removeCreature(panel.getLast());
        assertTrue(panel.phisycsUpdate(),
                "A atualização física deveria continuar com uma bola restante");
        panel.Creatures.remove(panel.Creatures.getLast());
        assertFalse(panel.phisycsUpdate(), "Atualização fisica nao deve ocorrer com lista vazia");

        panel.removeCreature(panel.getLast());
        panel.addCreature(80);
        panel.initSimulation(0);
        assertTrue(panel.phisycsUpdate(),
                "A atualização física deve ocorrer normalmente com bolas no painel");
    }

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
}
