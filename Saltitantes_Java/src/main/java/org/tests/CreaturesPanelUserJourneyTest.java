package org.tests;

import org.example.model.CreaturesPanel;
import org.example.model.SQLite;
import org.example.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class CreaturesPanelUserJourneyTest {

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

        // --- ADICIONE ESTAS LINHAS ---
        when(mockUser.getUserName()).thenReturn("TestUser"); // Configura o mock para retornar um nome de usuário
        when(mockUser.getPoints()).thenReturn(0.0); // É bom inicializar os pontos também

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
        // Verifica se os timers estão rodando antes de tentar pará-los para evitar NullPointerException
        if (panel.phisycsTimer != null && panel.phisycsTimer.isRunning()) {
            panel.phisycsTimer.stop();
        }
        if (panel.updateTimer != null && panel.updateTimer.isRunning()) {
            panel.updateTimer.stop();
        }
        // Fechar o BD, se não for null (o mock não precisa, mas se fosse real, sim)
        // panel.bd.close(); // Esta linha pode causar NPE se bd for mock e close não for mockado
        panel = null;
        randi = null;
    }


    @Test
    void userJourney_simulacaoVitoria() {
        panel.addCreature(10);
        panel.addCreature(20);
        panel.addCreature(30);
        assertEquals(4, panel.Creatures.size(), "Deve ter 4 criaturas incluindo a do setUp.");

        panel.initSimulation(randi.nextInt(width - CreaturesPanel.CREATURE_SIZE));
        assertTrue(panel.startSimulation, "Simulação deve ter iniciado.");
        assertTrue(panel.getLast().isGuardian, "Guardião deve ter sido criado.");
        verify(mockUser, times(1)).addSimulations(); // Verifica interação do User Journey
        verify(bd, times(1)).editUserByUsername(anyString(), any(User.class)); // Agora 'anyString()' vai corresponder a "TestUser"


        when(mockUser.getPoints()).thenReturn(400.0);
        panel.updateTimer.getActionListeners()[0].actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null)); // Força um update
        panel.phisycsTimer.getActionListeners()[0].actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null)); // Força um physics update

        when(mockUser.getPoints()).thenReturn(550.0);
        panel.startSimulation = true;
        assertTrue(panel.stopSimulation(), "Devia terminar simulação com vitoria");
    }
}